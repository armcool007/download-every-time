#1. Uninstall old versions (if any)
sudo apt-get remove docker docker-engine docker.io containerd runc


#2. Update apt package index
sudo apt-get update
sudo apt-get install -y ca-certificates curl gnupg lsb-release

#3. Add Docker’s official GPG key
sudo mkdir -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg

#4. Set up the stable repository
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] \
  https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

#5. Update apt and install Docker
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

#6. Verify installation
docker --version

#7. Run Docker without sudo (optional)
sudo usermod -aG docker $USER
