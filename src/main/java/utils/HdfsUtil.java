package utils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import java.io.IOException;


public class HdfsUtil{
    /**
     * HDFS 配置
     */
    private static Configuration config = null;
    private static FileSystem fs = null;
    static {
        config = new Configuration();
        config.addResource("hadoop/hdfs-site.xml");
        config.addResource("hadoop/core-site.xml");
    }
    public static Configuration getConfiguration() {
        return config;
    }

    /**
     * 连接HDFS
     */
    public static void connect(){
        try {
            fs = FileSystem.get(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static FileSystem getFs() {
        return fs;
    }

    /**
     * 关闭连接
     */
    public static void close(){
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
    public static void mkdir(String directory) throws IOException {
        if(fs == null)
            connect();
        Path path = new Path(directory);
        if(!fs.exists(path)) {
            fs.mkdirs(path);
            System.out.println("目录创建成功：" + directory);
        } else {
            System.out.println("目录已存在：" + directory);
        }
    }

    /**
     * 创建目录
     * @param srcFile 源文件路径
     * @param dstFile 目标文件路径
     */
    public static void put(String srcFile, String dstFile) throws IOException {
        if(fs == null)
            connect();
        Path srcPath = new Path(srcFile);
        Path dstPath = new Path(dstFile);
        fs.copyFromLocalFile(srcPath, dstPath);
        System.out.println("文件存入成功：源文件   " + srcFile);
        System.out.println("文件存入成功：目的文件 " + dstFile);
    }
}