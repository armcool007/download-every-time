#Install from HashiCorp Repo (Recommended)
#1. Install dependencies
sudo yum install -y yum-utils curl unzip

#2. Add HashiCorp yum repository
sudo yum-config-manager --add-repo https://rpm.releases.hashicorp.com/AmazonLinux/hashicorp.repo

#3. Install Terraform
sudo yum -y install terraform

#4. Check version
terraform -version

-----------------------------------------------------------------------------------------------------------
# for UBUNTU
-------------
# Latest version download karo
wget https://releases.hashicorp.com/terraform/1.7.5/terraform_1.7.5_linux_amd64.zip

# Unzip karo
sudo apt install unzip -y
unzip terraform_1.7.5_linux_amd64.zip

# Move karo
sudo mv terraform /usr/local/bin/

# Verify karo
terraform --version
