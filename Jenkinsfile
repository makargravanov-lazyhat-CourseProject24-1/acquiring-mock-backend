pipeline {
    agent any

    environment {
        SERVICE_NAME = "acquiring-mock-backend"
        HTTP_PORT = 8080
        DB_URL = credentials("common-db-url")
        DB_USERNAME = credentials("common-db-username")
        DB_PASSWORD = credentials("common-db-password")
    }

    stages {
        stage("Build"){
            steps {
                sh "chmod a+x ./gradlew"
                sh "./gradlew bootJar"
            }
        }
        stage("Image"){
            steps {
                sh "sudo docker build -t jetlabs/${SERVICE_NAME}:latest -t jetlabs/${SERVICE_NAME}:${BUILD_NUMBER} --build-arg name=${SERVICE_NAME} ."
            }
        }
        stage("Deploy"){
            steps {
                script {
                    try {
                        sh "sudo docker container stop ${SERVICE_NAME}"
                        sh "sudo docker container rm ${SERVICE_NAME}"
                    } catch(err) {
                        echo "Container does not exists."
                    }
                }
                sh "sudo docker run --detach --name ${SERVICE_NAME} -e HTTP_PORT=${HTTP_PORT} -e DB_URL=${DB_URL} -e DB_USERNAME=${DB_USERNAME} -e DB_PASSWORD=${DB_PASSWORD} --network=service_postgresql--service --network=service_kafka--service jetlabs/${SERVICE_NAME}:latest"
            }
        }
    }
}