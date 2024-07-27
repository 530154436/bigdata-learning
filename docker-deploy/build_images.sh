# https://hub.docker.com/r/amd64/debian/
docker build --platform=linux/amd64 -t 15521147129/bigdata:debian-base -f base/Dockerfile .

# 清除缓存 https://blog.csdn.net/jodan179/article/details/133795155
docker run --platform linux/amd64 -d --name debian-base 15521147129/bigdata:debian-base /bin/bash -c "tail -f /dev/null"

#docker login -u "15521147129" -p "" docker.io
docker push 15521147129/bigdata:debian-base
docker ps
docker exec -it 7470dc8edf34 /bin/bash


# zk、hadoop
docker build --platform=linux/amd64 -t 15521147129/bigdata:hadoop-ha -f hadoop-ha/Dockerfile .

docker network create hadoop-docker-bridge
docker compose -f hadoop-ha/docker-compose.yml up
docker exec -it c62a9fdf530f /bin/bash