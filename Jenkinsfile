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
                        if (bat(script: "git diff --name-only HEAD~1..HEAD ${service}", returnStatus: true) == 0) {
                            dir("${service}") {
                                bat "${mvnHome}\\bin\\mvn clean package"
                            }
                            // Build Docker image
                            def dockerImage = docker.build("emirhancebiroglu/${service}", "${service}/.")
                            docker.withRegistry(registryUrl, registryCredential) {
                                dockerImage.push("v1")
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
                            def deploymentExists = bat(script: "kubectl get deployment ${service} -n default", returnStatus: true)

                            if (deploymentExists == 0) {
                                echo "Deployment ${service} already exists. Restarting..."
                                bat "kubectl rollout restart deployment/${service} -n default"
                            } else {
                                echo "No existing deployment found for ${service}. Deploying new..."
                                kubernetesDeploy(
                                    configs: "k8s/${service}/${service}-deployment.yaml,k8s/${service}/${service}-service.yaml",
                                    kubeConfig: [path: env.KUBECONFIG]
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
