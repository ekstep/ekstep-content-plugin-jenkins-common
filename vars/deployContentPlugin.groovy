def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    pipeline {
        agent {
            label 'plugin-deploy'
        }
        stages {
            stage('Preparation') {
              steps {
                git credentialsId: 'ea6eb7d0-d841-4022-8cc1-18f4fd642778', url: 'https://github.com/ekstep/AWS-Setup.git'

                sh "rm -rf dist"

                step ([$class: 'CopyArtifact',
                  projectName: config.projectName,
                  filter: 'dist/*.zip',
                  selector: [$class: 'ParameterizedBuildSelector', parameterName: 'DEPLOY_BUILD_NUMBER']
                ]);
              }
            }
            stage(name:'Deploy', concurrency: 1) {
                steps {
                    wrap([$class: 'BuildUser']) {
                       withEnv(["PLUGIN_DEPLOY_INVENTORY=${config.env}"]) {
                            sh '''
                            export ANSIBLE_HOST_KEY_CHECKING=False
                            export PLUGIN_ZIP_FILE_PATH=$(pwd)/$(ls dist/*.zip | head -1)
                            cd ansible
                            ansible-playbook -i inventory/$PLUGIN_DEPLOY_INVENTORY plugin-deploy.yml --vault-password-file ../analytics/.vault_pass.txt -v --extra-vars "plugin_zip_file_path=$PLUGIN_ZIP_FILE_PATH"
                            '''
                        }
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