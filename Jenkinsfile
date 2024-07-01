pipeline {
    environment {
        registryCredential = 'dockerhub-credentials'
        registryUrl = 'https://registry.hub.docker.com'
        gitCredentials = 'github-credentials'
    }

    agent any

    stages {
        stage('Checkout Source') {
            steps {
                git url: 'https://github.com/emirhancebiroglu/cash_register_backend.git', branch: 'main', credentialsId: gitCredentials
            }
        }

        stage('Build and Push Images') {
            parallel {
                stage('api-gateway') {
                    steps {
                        script {
                            if (sh(returnStdout: true, script: 'git diff --name-only HEAD~1..HEAD api-gateway').trim()) {
                                def dockerImage = docker.build("emirhancebiroglu/api-gateway", "api-gateway/.")
                                docker.withRegistry(registryUrl, registryCredential) {
                                    dockerImage.push("latest")
                                }
                            } else {
                                echo "No changes detected in api-gateway. Skipping build."
                            }
                        }
                    }
                }

                stage('eureka-server') {
                    steps {
                        script {
                            if (sh(returnStdout: true, script: 'git diff --name-only HEAD~1..HEAD eureka-server').trim()) {
                                def dockerImage = docker.build("emirhancebiroglu/eureka-server", "eureka-server/.")
                                docker.withRegistry(registryUrl, registryCredential) {
                                    dockerImage.push("latest")
                                }
                            } else {
                                echo "No changes detected in eureka-server. Skipping build."
                            }
                        }
                    }
                }

                stage('jwt_auth_service') {
                    steps {
                        script {
                            if (sh(returnStdout: true, script: 'git diff --name-only HEAD~1..HEAD jwt-auth-service').trim()) {
                                def dockerImage = docker.build("emirhancebiroglu/jwt_auth_service", "jwt_auth_service/.")
                                docker.withRegistry(registryUrl, registryCredential) {
                                    dockerImage.push("latest")
                                }
                            } else {
                                echo "No changes detected in jwt_auth_service. Skipping build."
                            }
                        }
                    }
                }

                stage('product-service') {
                    steps {
                        script {
                            if (sh(returnStdout: true, script: 'git diff --name-only HEAD~1..HEAD product-service').trim()) {
                                def dockerImage = docker.build("emirhancebiroglu/product-service", "product-service/.")
                                docker.withRegistry(registryUrl, registryCredential) {
                                    dockerImage.push("latest")
                                }
                            } else {
                                echo "No changes detected in product-service. Skipping build."
                            }
                        }
                    }
                }

                stage('reporting-service') {
                    steps {
                        script {
                            if (sh(returnStdout: true, script: 'git diff --name-only HEAD~1..HEAD reporting-service').trim()) {
                                def dockerImage = docker.build("emirhancebiroglu/reporting-service", "reporting-service/.")
                                docker.withRegistry(registryUrl, registryCredential) {
                                    dockerImage.push("latest")
                                }
                            } else {
                                echo "No changes detected in reporting-service. Skipping build."
                            }
                        }
                    }
                }

                stage('sales-service') {
                    steps {
                        script {
                            if (sh(returnStdout: true, script: 'git diff --name-only HEAD~1..HEAD sales-service').trim()) {
                                def dockerImage = docker.build("emirhancebiroglu/sales-service", "sales-service/.")
                                docker.withRegistry(registryUrl, registryCredential) {
                                    dockerImage.push("latest")
                                }
                            } else {
                                echo "No changes detected in sales-service. Skipping build."
                            }
                        }
                    }
                }

                stage('user_management_service') {
                    steps {
                        script {
                            if (sh(returnStdout: true, script: 'git diff --name-only HEAD~1..HEAD user-management-service').trim()) {
                                def dockerImage = docker.build("emirhancebiroglu/user_management_service", "user_management_service/.")
                                docker.withRegistry(registryUrl, registryCredential) {
                                    dockerImage.push("latest")
                                }
                            } else {
                                echo "No changes detected in user_management_service. Skipping build."
                            }
                        }
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            parallel {
                stage('Deploy api-gateway') {
                    steps {
                        script {
                            if (sh(returnStdout: true, script: 'git diff --name-only HEAD~1..HEAD api-gateway').trim()) {
                                kubernetesDeploy(configs: "k8s/gateway/api-gateway-deployment.yaml, k8s/gateway/api-gateway-service.yaml")
                            } else {
                                echo "No changes detected in api-gateway. Skipping deployment."
                            }
                        }
                    }
                }

                stage('Deploy eureka-server') {
                    steps {
                        script {
                            if (sh(returnStdout: true, script: 'git diff --name-only HEAD~1..HEAD eureka-server').trim()) {
                                kubernetesDeploy(configs: "k8s/eureka/eureka-server-deployment.yaml, k8s/eureka/eureka-server-service.yaml")
                            } else {
                                echo "No changes detected in eureka-server. Skipping deployment."
                            }
                        }
                    }
                }

                stage('Deploy jwt_auth_service') {
                    steps {
                        script {
                            if (sh(returnStdout: true, script: 'git diff --name-only HEAD~1..HEAD jwt-auth-service').trim()) {
                                kubernetesDeploy(configs: "k8s/jwt-auth/jwt-auth-deployment.yaml, k8s/jwt-auth/jwt-auth-service.yaml")
                            } else {
                                echo "No changes detected in jwt_auth_service. Skipping deployment."
                            }
                        }
                    }
                }

                stage('Deploy product-service') {
                    steps {
                        script {
                            if (sh(returnStdout: true, script: 'git diff --name-only HEAD~1..HEAD product-service').trim()) {
                                kubernetesDeploy(configs: "k8s/product/product-deployment.yaml, k8s/product/product-service.yaml")
                            } else {
                                echo "No changes detected in product-service. Skipping deployment."
                            }
                        }
                    }
                }

                stage('Deploy reporting-service') {
                    steps {
                        script {
                            if (sh(returnStdout: true, script: 'git diff --name-only HEAD~1..HEAD reporting-service').trim()) {
                                kubernetesDeploy(configs: "k8s/reporting/reporting-deployment.yaml, k8s/reporting/reporting-service.yaml")
                            } else {
                                echo "No changes detected in reporting-service. Skipping deployment."
                            }
                        }
                    }
                }

                stage('Deploy sales-service') {
                    steps {
                        script {
                            if (sh(returnStdout: true, script: 'git diff --name-only HEAD~1..HEAD sales-service').trim()) {
                                kubernetesDeploy(configs: "k8s/sales/sales-deployment.yaml, k8s/sales/sales-service.yaml")
                            } else {
                                echo "No changes detected in sales-service. Skipping deployment."
                            }
                        }
                    }
                }

                stage('Deploy user_management_service') {
                    steps {
                        script {
                            if (sh(returnStdout: true, script: 'git diff --name-only HEAD~1..HEAD user-management-service').trim()) {
                                kubernetesDeploy(configs: "k8s/user-management/user-management-deployment.yaml, k8s/user-management/user-management-service.yaml")
                            } else {
                                echo "No changes detected in user_management_service. Skipping deployment."
                            }
                        }
                    }
                }
            }
        }
    }
}
