pipelineJob("Repo CI job"){
  properties {
    githubProjectUrl("https://github.com/danielduduta/flask-ci-jenkins")
    pipelineTriggers {
      triggers{
        GenericTrigger{
          genericVariables {
            genericVariable {
              key('ref')
              value('$.ref')
              regexpFilter('^(refs/heads/|refs/remotes/origin/)')
            }
            genericVariable {
              key('git_revision')
              value('$.after')
            }
            genericVariable {
              key('git_clone_url')
              value('$.repository.clone_url')
            }
            genericVariable {
              key('git_repo')
              value('$.repository.full_name')
            }
            genericVariable {
              key('git_repo_name')
              value('$.repository.name')
            }
          }
          token("secretcode")
          regexpFilterText('$ref')
          regexpFilterExpression('main(.*)')
        }
      }
    }
  }
  definition {
    cps {
      sandbox(true)
      script('''
pipeline {
  agent any
  environment {
    GH_CREDS = credentials('gh-credentials')
    ECR = "ghcr.io/${git_repo}"
    DEPLOYMENT_REPO = "https://github.com/danielduduta/flask-ci-deployment.git"
  }
  stages {
    stage('Repo checkout source code') {
      steps {
        dir('app-codebase') {
          checkout([
            $class: 'GitSCM',
            branches: [[name: "${git_revision}"]],
            doGenerateSubmoduleConfigurations: false,
            extensions: [[$class: 'CleanCheckout']],
            submoduleCfg: [],
            userRemoteConfigs: [[url: "${git_clone_url}"]]
          ])
        }
      }
    }
    stage('Build and test image') {
      steps {
        dir('app-codebase') {
            sh "make deployment-image"
            sh "make testing-image"
            sh "make tests"
        }
      }
    }
    stage('Push image to registry') {
      steps {
        sh('echo ${GH_CREDS_PSW} | docker login ghcr.io -u U${GH_CREDS_USR} --password-stdin')
        sh('docker tag ciplay:deployment ${ECR}:${git_revision}')
        sh('docker push ${ECR}:${git_revision}')
      }
    }
    stage('Deploy image to QA K8S') {
      steps {
        dir('app-deployment') {
          checkout([
            $class: 'GitSCM',
            branches: [[name: "main"]],
            doGenerateSubmoduleConfigurations: false,
            extensions: [[$class: 'CleanCheckout']],
            submoduleCfg: [],
            userRemoteConfigs: [[url: "${DEPLOYMENT_REPO}"]]
          ])
          dir("services/${git_repo_name}") {
            sh('echo "terraform kubernetes provider configured to access QA k8s"')
            sh("""echo "terraform plan -detailed-exitcode  -out tfplanout.${currentBuild.number}" """)
            sh("""echo "terragrunt apply -input=false tfplanout.${currentBuild.number}" """)
          }
        }
      }
    }
    stage('Trigger deployment QA job') {
        steps {
            build job: 'Repo QA job', wait: false, parameters: [string(name: 'DEPLOYMENT', value: "${git_repo_name}"), string(name: 'IMAGE', value: "${ECR}:${git_revision}")]
        }
    }
  }
  post {
    always {
      cleanWs()
    }
  }
}
      '''.stripIndent())
     }
   }
}

