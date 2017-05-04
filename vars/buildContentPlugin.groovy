def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    pipeline {
        agent {
            label 'plugin-build'
        }
        triggers {
            pollSCM 'H/5 * * * *'
        }
        tools {
            nodejs 'NodeJS 7.9.0'
        }
        stages {
            stage('Build') {
                steps {
                    sh '''
                    rm -rf bower_components/content-editor* bower_components/renderer node_modules/ekstep-content-plugin-dev-common
                    bower cache clean
                    npm install
                    gulp
                    '''
                }
                post {
                    always {
                        junit 'coverage/junit-test-report.xml'
                        publishHTML([reportDir: 'coverage/html-test-report', reportFiles: 'index.html', reportName: 'Test Report', allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportTitles: ''])
                        publishHTML([reportDir: 'coverage/phantomjs', reportFiles: 'index.html', reportName: 'Test Coverage Report', allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportTitles: ''])
                    }
                    success {
                        archive 'dist/*.zip'

                        publishHTML([reportDir: 'docs/gen', reportFiles: 'index.html', reportName: 'JsDoc', allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportTitles: ''])

                        step([
                            $class: 'S3BucketPublisher',
                            entries: [[
                                sourceFile: 'dist/*.zip',
                                bucket: 'ekstep-public-dev/contributed-plugins',
                                selectedRegion: 'ap-south-1',
                                noUploadOnFailure: true,
                                managedArtifacts: true,
                                flatten: true,
                                showDirectlyInBrowser: true,
                                keepForever: true,
                            ]],
                            profileName: 'aws-iam-role-based-access',
                            dontWaitForConcurrentBuildCompletion: false,
                        ])
                    }
                }
            }
        }
        post {
            success{
                slackSend(color: 'good', message: "SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)")
            }
            failure {
                slackSend(color: 'danger', message: "FAILURE: ${env.JOB_NAME} #${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)")
            }
        }
    }
}