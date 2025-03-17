pipeline {
    agent any

    stages {
        stage('Initialize') {
            steps {
                script {
                    BRANCH_NAME = env.BRANCH_NAME ?: 'unknown'
                    IS_FEATURE_BRANCH = BRANCH_NAME.startsWith('feature/')
                    IS_PR = env.CHANGE_ID != null  // PR 여부 체크

                    echo "Current Branch: ${BRANCH_NAME}"
                    echo "Is Feature Branch? ${IS_FEATURE_BRANCH}"
                    echo "Is Pull Request? ${IS_PR}"
                }
            }
        }

        stage('Checkout Git') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            when {
                expression {
                    return !IS_FEATURE_BRANCH || IS_PR  // feature 브랜치 Push는 빌드하지 않음
                }
            }
            steps {
                sh './gradlew clean bootJar --no-daemon'
                echo 'Build Success!'
            }
        }

        stage('Test') {
            when {
                anyOf {
                    expression { IS_PR }   // 모든 PR에서 테스트 실행
                    branch 'development'   // 개발 브랜치에서 실행
                    branch 'main'          // 운영 브랜치에서도 실행
                }
            }
            steps {
                sh './gradlew test --no-daemon'
                echo 'Test Success!'
            }
        }
    }

    post {
        success {
            echo 'Pipeline executed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check the logs for details.'
        }
    }
}
