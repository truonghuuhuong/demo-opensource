pipeline {
    agent any
    tools {
        jdk 'jdk8'
        maven 'maven3'
    }
    stages {
        stage('Install') {
            steps {
                sh "mvn clean test"
            }
        }
		stage('Sonar') {
            steps {
                sh "mvn sonar:sonar -Dsonar.host.url=http://172.22.240.1:9222 -Dsonar.login=2197bb785b476c5757551be05f0687e9c427a50f"
            }
        }
    }
}