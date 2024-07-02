pipeline {
    environment {
        registryCredential = 'dockerhub-credentials'
        registryUrl = 'https://registry.hub.docker.com'
        gitCredentials = 'github-credentials'
        mvnHome = tool 'Maven'
        KUBECONFIG_CREDENTIALS_ID = 'minikube-config'
    }

    agent any

    stages {
        stage('Checkout Source') {
            steps {
                git url: 'https://github.com/emirhancebiroglu/cash_register_backend.git', branch: 'main', credentialsId: gitCredentials
            }
        }

        stage('Build and Push Images') {
            steps {
                script {
                    def services = [
                        'api-gateway',
                        'eureka-server',
                        'jwt_auth_service',
                        'product-service',
                        'reporting-service',
                        'sales-service',
                        'user_management_service'
                    ]

                    for (service in services) {
                        if (sh(returnStdout: true, script: "git diff --name-only HEAD~1..HEAD ${service}").trim()) {
                            dir("${service}") {
                                sh "${mvnHome}/bin/mvn clean package"
                            }
                            // Build Docker image
                            def dockerImage = docker.build("emirhancebiroglu/${service}", "${service}/.")
                            docker.withRegistry(registryUrl, registryCredential) {
                                dockerImage.push("latest")
                            }
                        } else {
                            echo "No changes detected in ${service}. Skipping build."
                        }
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                script {
                    withCredentials([file(credentialsId: env.KUBECONFIG_CREDENTIALS_ID, variable: 'KUBECONFIG')]) {
                        def services = [
                            'api-gateway',
                            'eureka-server',
                            'jwt_auth_service',
                            'product-service',
                            'reporting-service',
                            'sales-service',
                            'user_management_service'
                        ]

                        for (service in services) {
                            if (sh(returnStdout: true, script: "git diff --name-only HEAD~1..HEAD ${service}").trim()) {
                                // Deploy to Kubernetes
                                kubernetesDeploy(
                                    configs: "k8s/${service}/${service}-deployment.yaml,k8s/${service}/${service}-service.yaml",
                                    kubeConfig: [path: env.KUBECONFIG]
                                )
                            } else {
                                echo "No changes detected in ${service}. Skipping deployment."
                            }
                        }
                    }
                }
            }
        }
    }
}
