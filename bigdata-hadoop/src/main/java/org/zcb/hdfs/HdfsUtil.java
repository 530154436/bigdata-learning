package org.zcb.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;


public class HdfsUtil{
    /**
     * HDFS 配置
     *
     * 依次加载的参数信息的顺序是：
     * 1、加载 yarn-default.xml
     * 2、加载通过conf.addResources()加载的配置文件
     * 3、加载conf.set(name, value)
     */
    private Configuration conf = null;
    private URI uri = null;
    private FileSystem fs = null;

    public HdfsUtil(){
        this(null);
    }
    public HdfsUtil(Map<String, String> conf){
        this.setConfiguration(conf);
    }
    public void setConfiguration(Map<String, String> config){
        if(this.conf == null){
            this.conf = new Configuration();
            try {
                this.uri = new URI("hdfs://hadoop101:9000");
                conf.addResource("hadoop-3.1.1/core-site.xml");
                conf.addResource("hadoop-3.1.1/hdfs-site.xml");
                conf.set("dfs.client.use.datanode.hostname", "true");
                if(config!= null && !config.isEmpty()){
                    for (Map.Entry<String, String> e : config.entrySet()) {
                        this.conf.set(e.getKey(), e.getValue());
                    }
                }
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public Configuration getConfiguration() {return conf;}

    /**
     * HDFS连接和关闭
     */
    public void connect(){
        try {
            String user = "hadoop";
            fs = FileSystem.get(uri, conf, user);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public FileSystem getFs() {
        return fs;
    }

    public void close(){
        try {
            fs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建目录
     * @param directory 目录路径
     */
    public void mkdir(String directory) throws IOException {
        Path path = new Path(directory);
        if(!fs.exists(path)) {
            fs.mkdirs(path);
            System.out.println("目录创建成功：" + directory);
        } else {
            System.out.println("目录已存在：" + directory);
        }
    }

    /**
     * 上传文件
     * @param delSrc 是否删除本机的文件
     * @param overwrite 是否覆盖集群上的文件
     * @param src 要上传的文件在本机的路径
     * @param dst 要上传到集群中的路径
     */
    public void upload(boolean delSrc, boolean overwrite, String src, String dst) throws IOException {
        fs.copyFromLocalFile(delSrc, overwrite, new Path(src), new Path(dst));
        System.out.printf("文件上传成功: %s => %s.\n", src, dst);
    }
    public void upload(boolean overwrite, String src, String dst) throws IOException {
        upload(false, overwrite, src, dst);
    }
    public void upload(String src, String dst) throws IOException {
        upload(true, src, dst);
    }

    /**
     * 下载文件
     * @param delSrc 是否删除集群中的文件
     * @param src 要下载的文件在集群中的路径
     * @param dst 要下载到本机的路径
     * @param useRaw 是否不要校验（false 为需要校验，会多一个 .crc 文件）
     */
    public void download(boolean delSrc, String src, String dst, boolean useRaw) throws IOException {
        fs.copyToLocalFile(delSrc, new Path(src), new Path(dst), useRaw);
        System.out.printf("文件下载成功: %s => %s.\n", src, dst);
    }
    public void download(String src, String dst) throws IOException {
        download(false, src, dst, true);
    }

    /**
     * 删除文件
     * @param path 文件在集群中的路径
     * @param recursive 是否递归删除（允许删除非空文件夹）
     */
    public void delete(String path, boolean recursive) throws IOException {
        fs.delete(new Path(path), recursive);
        System.out.printf("文件删除成功: %s.\n", path);
    }
}