docker network create hadoop-docker-bridge

# https://hub.docker.com/r/amd64/debian/
docker build --platform=linux/amd64 -t 15521147129/bigdata:debian-base -f base/Dockerfile .
# 清除缓存 https://blog.csdn.net/jodan179/article/details/133795155
docker run --platform linux/amd64 -d --name debian-base 15521147129/bigdata:debian-base /bin/bash -c "tail -f /dev/null"
#docker login -u "15521147129" -p "" docker.io
docker push 15521147129/bigdata:debian-base
docker ps
docker exec -it 7470dc8edf34 /bin/bash


# hadoop
docker build --platform=linux/amd64 -t 15521147129/bigdata:hadoop-3.1.1 -f hadoop-3.1.1/Dockerfile .
docker compose -f hadoop-3.1.1/docker-compose.yml up -d
docker exec -it c62a9fdf530f /bin/bash
#docker login -u "15521147129" -p "" docker.io
docker push 15521147129/bigdata:hadoop-3.1.1


# mysql
docker build --progress=plain --platform=linux/amd64 -t 15521147129/bigdata:mysql-5.6.37 -f mysql-5.6.37/Dockerfile .
docker compose -f mysql-5.6.37/docker-compose.yml up -d
docker push 15521147129/bigdata:mysql-5.6.37