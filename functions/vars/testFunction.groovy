def call(String environment, String platform, String ttype) {

    def ENVIRONMENT = environment
    def PLATFORM = platform
    def TTYPE = ttype
    def app_repo = 'A'

    if (PLATFORM == 'ios') {
        app_repo = 'B'
    }

    pipeline {
        agent any
        stages {
            stage('Checkout tests codebase') {
                steps {
                    script {
                        
                    }
                }
            }
            stage('Update app') {
                steps {
                    sh "echo ${ENVIRONMENT}"
                }
            }
            stage('Run tests') {
                steps {
                    sh "echo ${ENVIRONMENT}"
                }
            }
        }
        post {
            always {
                cleanWs()
                // send notifications
            }
        }
    }
}
