网络设置
```shell
# 构建网络
docker network create -d bridge hadoop-docker-bridge
docker network create hadoop-docker-bridge
# 检查网络
docker network inspect hadoop-docker-bridge
```
部署服务
```
docker compose -f hadoop-3.1.4/docker-compose.yml up -d 
docker compose -f mysql-5.6.37/docker-compose.yml up -d
docker compose -f hive-3.1.2/docker-compose.yml up -d
```
一键清理 Build Cache 缓存命令：
```shell
docker builder prune
```
一键清理系统 Cache 缓存命令：
```shell
docker system prune
```
