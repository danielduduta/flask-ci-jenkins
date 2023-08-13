pipelineJob("Repo CD job"){
  parameters {
    stringParam('DEPLOYMENT', '', 'DEPLOYMENT to deploy')
    stringParam('IMAGE', '', 'IMAGE to deploy')
  }
  properties {
    githubProjectUrl("https://github.com/danielduduta/flask-ci-jenkins")
  }
  definition {
    cps {
      sandbox(true)
      script('''
pipeline {
  agent any
  environment {
    GH_CREDS = credentials('gh-credentials')
    GH_IMAGE = "${params.IMAGE}"
    DEPLOYMENT = "${params.DEPLOYMENT}"
  }
  stages {
    stage('Deploy image') {
      steps {
        echo "deploying image ${GH_IMAGE} for deployment ${DEPLOYMENT}"
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

