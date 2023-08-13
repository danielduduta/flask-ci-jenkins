pipelineJob("Repo QA job"){
  parameters {
    stringParam('DEPLOYMENT', '', 'DEPLOYMENT to QA')
    stringParam('IMAGE', '', 'IMAGE to QA')
  }
  properties {
    githubProjectUrl("https://github.com/danielduduta/flask-ci-jenkins")
  }
  definition {
    cps {
      sandbox(true)
      script('''
def NEW_IMAGE

pipeline {
  agent any
  environment {
    GH_CREDS = credentials('gh-credentials')
    GH_IMAGE = "${params.IMAGE}"
  }
  stages {
    stage('Run tests') {
      steps {
        sh """echo "Running QA tests for ${params.DEPLOYMENT} - ${params.IMAGE}" """
      }
    }
    stage('Tag and publish image') {
      steps {
        script {
            NEW_IMAGE = "${GH_IMAGE}-production"
            sh("""echo ${GH_CREDS_PSW} | docker login ghcr.io -u U${GH_CREDS_USR} --password-stdin""")
            sh("""docker tag  ${GH_IMAGE} ${GH_IMAGE}-production""")
            sh("""docker push ${GH_IMAGE}-production""")
        }
      }
    }
    stage('Trigger production deployment job') {
        steps {
            build job: 'Repo CD job', wait: false, parameters: [
                string(name: 'DEPLOYMENT', value: "${params.DEPLOYMENT}"), 
                string(name: 'IMAGE', value: NEW_IMAGE)
                ]
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

