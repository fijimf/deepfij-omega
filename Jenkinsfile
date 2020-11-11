pipeline {
    agent any
    environment {
        CI = 'true'
    }
    tools {
        maven "/usr/bin/mvn"
    }
    stages {
        stage('Build') {
            steps {
                sh "docker build ."
            }
        }
    }
    post {
        always {
            cleanWs()
        }
    }
}