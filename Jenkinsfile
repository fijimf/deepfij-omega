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
            when {
                not { branch 'master'}
            }
            steps {
                sh "mvn -Dmaven.test.failure.ignore=true test"
            }
        }
        stage('Deploy for production') {
            when {
                branch 'master'
            }
            steps {
                 sh "mvn --batch-mode release:prepare -Darguments=-Djacoco.skip=true"
                 sh "mvn release:perform -Dgoals=install \"-Darguments=-DskipTests -Dmaven.javadoc.skip=true\" "
                 sh "docker build ."
            }
        }
    }
}