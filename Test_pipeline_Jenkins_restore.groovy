pipeline {
    agent any
    environment {
        BACKUP_DIR = "/tmp/jenkins_backup"
        ARCHIVE_NAME = "jenkins_backup_$(date +%F).tar.gz" // change your backup name
        S3_BUCKET = "your-bucket-name"
        S3_PATH = "backups/${ARCHIVE_NAME}"
        AWS_REGION = "us-east-1"
    }
    stages {
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
        stage('Download Backup from S3') {
            steps {
                script {
                    // Download backup from S3
                    echo 'Downloading backup from S3...'
                    sh """
                        aws configure set region ${AWS_REGION}
                        aws s3 cp s3://${S3_BUCKET}/${S3_PATH} ${BACKUP_DIR}/${ARCHIVE_NAME}
                    """
                }
            }
        }
        stage('Restore Jenkins Data') {
            steps {
                script {
                    // Restore the backup
                    echo 'Restoring Jenkins data...'
                    sh """
                        sudo tar -xzvf ${BACKUP_DIR}/${ARCHIVE_NAME} -C /var/lib/jenkins
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
            echo 'Restore completed successfully!'
        }
        failure {
            echo 'Restore failed!'
        }
    }
}