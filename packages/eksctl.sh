#Install eksctl using below commands
curl --silent --location "https://github.com/weaveworks/eksctl/releases/latest/download/eksctl_$(uname -s)_amd64.tar.gz" | tar xz -C /tmp
sudo mv /tmp/eksctl /usr/local/bin
eksctl version

#TO CREATE A CLUSTER WITH ONLY ONE MACHINE WITH SIZE 20GB:
  eksctl create cluster --name karma --region ap-south-1 --node-type t3.small --nodes 1 --nodes-min 1 --nodes-max 1 --node-volume-size 20

