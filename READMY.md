Here’s a concise version of the instructions in English:

Jenkins Backup and Upload to S3

This guide explains how to create a backup of Jenkins, upload it to Amazon S3 using a Python virtual environment for AWS CLI, and connect to the instance via SSH.

Steps

1. Connect to the Instance via SSH

	1.	Obtain the IP address of your instance and the private key file (e.g., my-key.pem).
	2.	Connect to your instance via SSH:

ssh -i /path/to/my-key.pem ubuntu@your-instance-ip

Replace /path/to/my-key.pem with the path to your private key and your-instance-ip with your instance’s IP address.

2. Create Jenkins Backup

	1.	Locate Jenkins files (usually in /var/lib/jenkins).
	2.	Create a backup archive using tar:

sudo tar -czvf /tmp/jenkins_backup.tar.gz /var/lib/jenkins



3. Install AWS CLI in a Virtual Environment

a. Install Required Tools

sudo apt update
sudo apt install python3 python3-venv python3-pip -y

b. Create a Virtual Environment

python3 -m venv ~/awscli-env

c. Activate the Virtual Environment

source ~/awscli-env/bin/activate

d. Install AWS CLI

pip install awscli

e. Verify the Installation

aws --version

f. Configure AWS CLI

Run:

aws configure

Enter your AWS credentials:
	•	AWS Access Key ID
	•	AWS Secret Access Key
	•	Default region name (e.g., us-east-1)
	•	Default output format (e.g., json)

4. Create an S3 Bucket

If you don’t have a bucket, create one:

aws s3 mb s3://your-bucket-name

Replace your-bucket-name with your desired S3 bucket name.

5. Upload Backup to S3

Upload the backup to S3:

aws s3 cp /tmp/jenkins_backup.tar.gz s3://your-bucket-name/jenkins_backup_$(date +%F).tar.gz

6. Clean Up Temporary Files

After uploading, remove the backup file:

sudo rm /tmp/jenkins_backup.tar.gz

7. Deactivate Virtual Environment

When finished, deactivate the virtual environment:

deactivate

This guide covers connecting to an instance via SSH, creating a Jenkins backup, installing and using AWS CLI in a virtual environment, creating an S3 bucket, and uploading the backup to S3.