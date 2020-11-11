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
                sh "mvn clean compile"
            }
        }
        stage('Test') {
            steps {
                sh "mvn -Dmaven.test.failure.ignore=true test"
            }
        }
        stage('Deploy for production') {
            when {
                branch 'master'
            }
            steps {
                 sh "mvn -b release:prepare"
                 sh "mvn release:perform -Dgoals=install \"-Darguments=-DskipTests -Dmaven.javadoc.skip=true\" "
                 sh "docker build ."
            }
        }
    }
}