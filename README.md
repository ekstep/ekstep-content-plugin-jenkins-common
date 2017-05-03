## ekstep-content-plugin-jenkins-common

Jenkins functions for content plugin build and deployment

#### Jenkins Server Shared Library Setup

Follow the instructions in [jenkins shared library documentation](https://jenkins.io/doc/book/pipeline/shared-libraries/) to add this repository as shared library with name `ekstep-content-plugin-jenkins-common`


#### Usage in plugin project

Create a file named `Jenkinsfile` in root of your plugin project with below content

```groovy
@Library('ekstep-content-plugin-jenkins-common') _

buildContentPlugin {

}
```