sudo apt update
sudo apt install -y docker.io
sudo usermod -a -G docker ubuntu
sudo docker run -d --name sonar -p 9000:9000 sonarqube:lts-community
