pipeline {
    agent any

    // Mêmes noms que dans Manage Jenkins > Tools (Atelier 3)
    tools {
        jdk   'JDK21'
        maven 'M3'
    }

    // 1) Détecter le push GitHub : le webhook GitHub appelle Jenkins,
    //    qui lance un build dès qu'un commit est poussé.
    triggers {
        githubPush()
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {
        // 2) Récupérer le projet depuis GitHub
        stage('Checkout') {
            steps {
                checkout scm
                sh 'git log -1 --oneline'
            }
        }

        // 3) Lancer les tests unitaires (services, Mockito : pas besoin de base de données)
        stage('Tests unitaires') {
            steps {
                sh "mvn -B clean test -Dtest='tn.esprit.autoloc.service.*Test' -Dsurefire.failIfNoSpecifiedTests=false"
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        // 4) Créer le livrable (.jar) sous le dossier target
        stage('Livrable') {
            steps {
                sh 'mvn -B package -DskipTests'
                sh 'ls -lh target/*.jar'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
    }

    post {
        success { echo 'Pipeline OK : tests verts, livrable dans target/' }
        failure { echo 'Pipeline en échec : voir la Console Output' }
    }
}
