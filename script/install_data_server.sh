#!/bin/bash

yes | sudo apt-get update
yes | sudo apt-get install ca-certificates curl gnupg

yes | sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg

echo \
"deb [arch="$(dpkg --print-architecture)" signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
"$(. /etc/os-release && echo "$VERSION_CODENAME")" stable" | \
sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

yes | sudo apt-get update

yes | sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

sudo docker run -dp 27017:27017 --name mongo mongo:latest
sleep 10
sudo docker exec mongo mongosh admin --eval 'db.createUser({user: "root", pwd: "1234", roles: [{role: "userAdminAnyDatabase", db: "admin"}]})'

cd
mkdir redis_config
cd redis_config
touch redis.conf
echo $'maxmemory 8mb\nmaxmemory-policy allkeys-lru\nmaxmemory-samples 10' >> redis.conf

path=$(pwd)
sudo docker run -p 6379:6379 -d --name redis-cache -v "$path":/usr/redis_config redis:latest redis-server /usr/redis_config/redis.conf

echo "DONE"