# https://hub.docker.com/r/amd64/debian/
docker build --platform=linux/amd64 -t 15521147129/bigdata:debian-base -f base/Dockerfile .


# 清除缓存 https://blog.csdn.net/jodan179/article/details/133795155
docker run --platform linux/amd64 -d --name debian-base debian-base:v1.0
docker run --platform linux/amd64 -d --name debian-base-v1 debian-base:v1.0 /bin/bash -c "tail -f /dev/null"


#docker login -u "15521147129" -p "" docker.io
docker push 15521147129/bigdata:debian-base
docker exec -it bcf0d69ad9fb /bin/bash