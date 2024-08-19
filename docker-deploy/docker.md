网络设置
```shell
# 构建网络
docker network create hadoop-docker-bridge
# 检查网络
docker network inspect hadoop-docker-bridge
```
一键清理 Build Cache 缓存命令：
```shell
docker builder prune
```
一键清理系统 Cache 缓存命令：
```shell
docker system prune
```
