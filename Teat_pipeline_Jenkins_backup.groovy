pipeline {
    agent any
    environment {
        BACKUP_DIR = "/tmp/jenkins_backup"
        ARCHIVE_NAME = "jenkins_backup_${env.BUILD_ID}.tar.gz"
        S3_BUCKET = "your-bucket-name"
        S3_PATH = "backups/${ARCHIVE_NAME}"
        AWS_REGION = "us-east-1"
    }
    stages {
        stage('Create Backup Directory') {
            steps {
                script {
                    // Create a directory for backup files
                    sh 'mkdir -p ${BACKUP_DIR}'
                }
            }
        }
        stage('Archive Jenkins Data') {
            steps {
                script {
                    // Archive Jenkins data
                    echo 'Creating backup of Jenkins data...'
                    sh "sudo tar -czvf ${BACKUP_DIR}/${ARCHIVE_NAME} /var/lib/jenkins"
                }
            }
        }
        stage('Install AWS CLI if Needed') {
            steps {
                script {
                    // Check if AWS CLI is installed, and install if missing
                    def awsCliCheck = sh(script: 'aws --version', returnStatus: true)
                    if (awsCliCheck != 0) {
                        echo 'Installing AWS CLI...'
                        sh """
                            sudo apt update
                            sudo apt install python3 python3-pip -y
                            pip install --user awscli
                            export PATH=$PATH:~/.local/bin
                        """
                    }
                }
            }
        }
        stage('Upload Backup to S3') {
            steps {
                script {
                    // Upload the backup to S3
                    echo 'Uploading backup to S3...'
                    sh """
                        aws configure set region ${AWS_REGION}
                        aws s3 cp ${BACKUP_DIR}/${ARCHIVE_NAME} s3://${S3_BUCKET}/${S3_PATH}
                    """
                }
            }
        }
    }
    post {
        always {
            script {
                // Cleanup temporary files
                echo 'Cleaning up temporary files...'
                sh 'rm -rf ${BACKUP_DIR}'
            }
        }
        success {
            echo 'Backup completed successfully!'
        }
        failure {
            echo 'Backup failed!'
        }
    }
}