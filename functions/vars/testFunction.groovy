def call(String name) {
    pipeline {
        agent any
        stages {
            stage('Demo stage 1') {
                steps {
                    sh 'echo demo stage 1'
                }
            }
            stage('Demo stage 2') {
                steps {
                    sh 'echo demo stage 2'
                }
            }
        }
    }
}
