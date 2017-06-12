## ekstep-content-plugin-jenkins-common

Jenkins [shared library](https://jenkins.io/doc/book/pipeline/shared-libraries/) for content plugin build and deployment

### Usage

* `buildContentPlugin`: Create a file named `Jenkinsfile` in root of your plugin project with below content

```groovy
@Library('ekstep-content-plugin-jenkins-common') _

buildContentPlugin {

}
```

* `deployContentPlugin`: Create a pipeline job in jenkins with script similar to

```groovy
@Library('ekstep-content-plugin-jenkins-common') _

deployContentPlugin {
	env = 'dev'
	pluginArtifactsSourceJob = 'ekstep/org.ekstep.plugins.extractwords/master'
}
```

> Note: deployContentPlugin is currently limited to deploying internal plugins using https://github.com/ekstep/AWS-Setup

### Jenkins Server Setup

##### 1. Shared library setup

* Follow the instructions in [jenkins shared library documentation](https://jenkins.io/doc/book/pipeline/shared-libraries/) to add this repository as shared library with name `ekstep-content-plugin-jenkins-common`

##### 2. NodeJS tool setup

* Install [jenkins plugin for nodejs](https://plugins.jenkins.io/nodejs)
* Manage Jenkins -> Configure Tools -> NodeJS Installations
	* Name: NodeJS 7.9.0
	* Version: NodeJS 7.9.0
	* Global NPM packages: gulp-cli bower

##### 3. Slack Notification setup

* Install [jenkins plugin for slack](https://plugins.jenkins.io/slack)
* Configure slack access token and notification channel following instructions [here](https://plugins.jenkins.io/slack)
