#Install from HashiCorp Repo (Recommended)
#1. Install dependencies
sudo yum install -y yum-utils curl unzip

#2. Add HashiCorp yum repository
sudo yum-config-manager --add-repo https://rpm.releases.hashicorp.com/AmazonLinux/hashicorp.repo

#3. Install Terraform
sudo yum -y install terraform

#4. Check version
terraform -version
