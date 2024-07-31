package org.zcb.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import java.io.IOException;
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
    private Configuration config = null;
    private FileSystem fs = null;
    public HdfsUtil(){
        this(null);
    }
    public HdfsUtil(Map<String, String> conf){
        this.setConfiguration(conf);
    }
    public void setConfiguration(Map<String, String> conf) {
        if(config == null){
            config = new Configuration();
            config.set("fs.defaultFS", "hdfs://hadoop101:9000");
            // config.addResource("hadoop-3.1.1/core-site.xml");
            // config.addResource("hadoop-3.1.1/hdfs-site.xml");
        }
        if(conf!= null && !conf.isEmpty()){
            for (Map.Entry<String, String> e : conf.entrySet()) {
                this.config.set(e.getKey(), e.getValue());
            }
        }
    }
    public Configuration getConfiguration() {return config;}

    /**
     * HDFS连接和关闭
     */
    public void connect(){
        try {
            fs = FileSystem.get(config);
        } catch (IOException e) {
            e.printStackTrace();
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