package org.zcb.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;


public class HdfsUtil{
    /**
     * HDFS 配置，依次加载的参数信息的顺序是：
     * 1、加载 yarn-default.xml
     * 2、加载通过conf.addResources()加载的配置文件
     * 3、加载conf.set(name, value)
     */
    private String fsUri = "hdfs://hadoop101:9000";
    private String user = "hadoop";

    private Configuration conf = null;
    private FileSystem fs = null;

    public HdfsUtil(){
        this(null);
    }
    public HdfsUtil(Map<String, String> conf){
        this.setConfiguration(conf);
    }
    public HdfsUtil(Map<String, String> conf, String user){
        this(conf);
        this.user = user;
    }
    public HdfsUtil(Map<String, String> conf, String user, String fsUri){
        this(conf, user);
        this.fsUri = fsUri;
    }
    public void setConfiguration(Map<String, String> config){
        if(this.conf == null){
            this.conf = new Configuration();
        }
        conf.addResource("hadoop-3.1.1/core-site.xml");
        conf.addResource("hadoop-3.1.1/hdfs-site.xml");
        conf.set("dfs.client.use.datanode.hostname", "true"); // 解决DataNode找不到的问题
        if(config!= null && !config.isEmpty()){
            for (Map.Entry<String, String> e : config.entrySet()) {
                conf.set(e.getKey(), e.getValue());
            }
        }
    }
    public Configuration getConfiguration() {return conf;}

    /**
     * HDFS连接和关闭
     */
    public void connect(){
        try {
            URI uri = new URI(this.fsUri);
            fs = FileSystem.get(uri, conf, user);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException | URISyntaxException e) {
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
     * 判断目录或文件是否存在
     * @param path 目录或文件
     */
    public boolean exists(String path) throws IOException {
        return fs.exists(new Path(path));
    }

    /**
     * 创建目录
     * @param path 目录路径
     */
    public boolean mkdirs(String path) throws IOException {
        boolean flag = false;
        if(!exists(path)) {
            System.out.println("目录创建成功：" + path);
            flag = fs.mkdirs(new Path(path));
        } else {
            System.out.println("目录已存在：" + path);
        }
        return flag;
    }

    /**
     * 列出指定目录下的文件以及块的信息
     * @param path  目录
     * @param recursive 是否递归遍历
     */
    public void listFiles(String path, boolean recursive){
        RemoteIterator<LocatedFileStatus> iterator = null;
        try {
            iterator = fs.listFiles(new Path(path), recursive);
            while (iterator.hasNext()){
                LocatedFileStatus fileStatus = iterator.next();
                // 文件的block信息
                BlockLocation[] blockLocations = fileStatus.getBlockLocations();
                System.out.printf("文件路径: %s, 块的数量: %s.\n", fileStatus.getPath(), blockLocations.length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 移动/重命名目录或文件
     * @param src 源文件在集群中的路径
     * @param dst 目标文件在集群中的路径
     */
    public boolean rename(String src, String dst) throws IOException {
        if(!exists(src) || exists(dst)){
            System.out.printf("源文件不存在或目标文件存在: %s, %s\n", src, dst);
            return false;
        }
        boolean flag = fs.rename(new Path(src), new Path(dst));
        System.out.printf("文件命名程刚成功: %s => %s\n", src, dst);
        return flag;
    }

    /**
     * 删除文件或目录
     * @param path 文件在集群中的路径
     * @param recursive 是否递归删除（允许删除非空文件夹）
     */
    public boolean delete(String path, boolean recursive) throws IOException {
        if(!exists(path)){
            System.out.printf("文件不存在: %s\n", path);
            return false;
        }
        boolean flag = fs.delete(new Path(path), recursive);
        System.out.printf("文件删除成功: %s.\n", path);
        return flag;
    }

    /**
     * 上传文件
     * @param delSrc 是否删除本机的文件
     * @param overwrite 是否覆盖集群上的文件
     * @param src 要上传的文件在本机的路径
     * @param dst 要上传到集群中的路径
     */
    public boolean upload(boolean delSrc, boolean overwrite, String src, String dst) throws IOException {
        fs.copyFromLocalFile(delSrc, overwrite, new Path(src), new Path(dst));
        System.out.printf("文件上传成功: %s => %s.\n", src, dst);
        return true;
    }
    public boolean upload(boolean overwrite, String src, String dst) throws IOException {
        return upload(false, overwrite, src, dst);
    }
    public boolean upload(String src, String dst) throws IOException {
        return upload(true, src, dst);
    }

    /**
     * 写文件(覆盖源文件、类似上传)
     * 通过调用FileSystem实例的create方法获取写文件的输出流。通常获得输出流之后，可以直接对这个输出流进行写入操作，
     * 将内容写入HDFS的指定文件中。写完文件后，需要调用close方法关闭输出流。
     * @param dst 目标文件路径
     * @param content 写入的内容
     */
    public boolean create(String dst, String content) throws IOException{
        FSDataOutputStream out = null;
        try {
            out = fs.create(new Path(dst));
            out.write(content.getBytes());
            out.hsync();
            System.out.printf("文件内容写入成功: %s.\n", dst);
            return true;
        } finally {
            assert out != null;
            out.close();
        }
    }

    /**
     * 追加文件内容
     * 对于已经在HDFS中存在的文件，可以追加指定的内容，以增量的形式在该文件现有内容的后面追加。
     * 通过调用FileSystem实例的append方法获取追加写入的输出流。
     * 然后使用该输出流将待追加内容添加到HDFS的指定文件后面。追加完指定的内容后，需要调用close方法关闭输出流。
     * @param dst 目标文件路径
     * @param content 写入的内容
     */
    public boolean append(String dst, String content) throws IOException{
        FSDataOutputStream out = null;
        try {
            out = fs.append(new Path(dst));
            out.write(content.getBytes());
            out.hsync();
            System.out.printf("文件内容追加成功: %s => %s.\n", dst, content);
            return true;
        } finally {
            assert out != null;
            out.close();
        }
    }

    /**
     * 下载文件
     * @param delSrc 是否删除集群中的文件
     * @param src 要下载的文件在集群中的路径
     * @param dst 要下载到本机的路径
     * @param useRaw 是否不要校验（false 为需要校验，会多一个 .crc 文件）
     */
    public boolean download(boolean delSrc, String src, String dst, boolean useRaw) throws IOException {
        Path srcPath = new Path(src);
        if(!fs.exists(srcPath)){
            System.out.printf("文件不存在: %s\n", src);
            return false;
        }
        fs.copyToLocalFile(delSrc, new Path(src), new Path(dst), useRaw);
        System.out.printf("文件下载成功: %s => %s.\n", src, dst);
        return true;
    }
    public boolean download(String src, String dst) throws IOException {
        return download(false, src, dst, true);
    }

    /**
     * 读文件
     * 获取HDFS上某个指定文件的内容。通过调用FileSystem实例的open方法获取读取文件的输入流。
     * 然后使用该输入流读取HDFS的指定文件的内容。读完文件后，需要调用close方法关闭输入流。
     * @param path 文件在集群中的路径
     */
    public String read(String path) throws IOException {
        if(!exists(path)){
            return null;
        }
        FSDataInputStream in = null;
        BufferedReader reader = null;
        StringBuilder strBuilder = new StringBuilder();
        try {
            in = fs.open(new Path(path));
            reader = new BufferedReader(new InputStreamReader(in));
            String sTempOneLine;
            // write file
            while ((sTempOneLine = reader.readLine()) != null) {
                strBuilder.append(sTempOneLine);
            }
            return strBuilder.toString();
        } finally {
            // make sure the streams are closed finally.
            IOUtils.closeStream(reader);
            IOUtils.closeStream(in);
        }
    }
}