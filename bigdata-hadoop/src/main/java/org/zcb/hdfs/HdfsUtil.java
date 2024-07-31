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
    public boolean mkdir(String directory) throws IOException {
        Path path = new Path(directory);
        if(!fs.exists(path)) {
            fs.mkdirs(path);
            System.out.println("目录创建成功：" + directory);
            return true;
        } else {
            System.out.println("目录已存在：" + directory);
            return false;
        }
    }

    /**
     * 上传文件
     * @param srcFile 源文件路径
     * @param dstFile 目标文件路径
     */
    public void put(String srcFile, String dstFile) throws IOException {
        put(srcFile, dstFile, true);
    }
    public void put(String srcFile, String dstFile, boolean overwrite) throws IOException {
        Path srcPath = new Path(srcFile);
        Path dstPath = new Path(dstFile);
        fs.copyFromLocalFile(false, overwrite, srcPath, dstPath);
        System.out.println("文件存入成功：源文件   " + srcFile);
        System.out.println("文件存入成功：目的文件 " + dstFile);
    }
}