

```shell
docker network create hadoop-docker-bridge
docker network ls
docker inspect a40626810634
```
Docker有三种网络模式，bridge、host、none，在你创建容器的时候，不指定--network默认是bridge。
bridge：为每一个容器分配IP，并将容器连接到一个docker0虚拟网桥，通过docker0网桥与宿主机通信。即此模式下，不能用宿主机的IP+容器映射端口来进行Docker容器之间的通信。
host：容器不会虚拟自己的网卡，配置自己的IP，而是使用宿主机的IP和端口。这样一来，Docker容器之间的通信就可以用宿主机的IP+容器映射端口
none：无网络。

```shell
docker compose -f hadoop-ha/docker-compose.yml up
```


#### 1、Bind for 0.0.0.0:2181 failed: port is already allocated
<img src="images_qa/zookeeper_端口映射.png" width="50%" height="50%" alt=""><br>
每个容器之间环境是隔离的，所以容器内所用的端口一样。
因为在同一台宿主机，端口不能冲突，所以需要将端口映射成不同的端口号，比如2181/2182/2183。

#### 2、hadoop101: ssh: connect to host hadoop101 port 22: Connection refused


[Docker下安装zookeeper（单机 & 集群）](https://www.cnblogs.com/LUA123/p/11428113.html)