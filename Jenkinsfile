pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh "docker build -t deepfij:latest -t deepfij:${TAG_NAME} ."
            }
        }
    }
    post {
        always {
            cleanWs()
        }
    }
}