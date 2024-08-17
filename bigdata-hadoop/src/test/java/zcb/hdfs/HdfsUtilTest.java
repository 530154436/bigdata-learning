package zcb.hdfs;

import org.junit.jupiter.api.*;
import org.zcb.hdfs.HdfsUtil;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


public class HdfsUtilTest {

    public static HdfsUtil hdfs = null;
    public static String userDir = null;

    @BeforeAll
    public static void init() {
        System.out.println("连接HDFS");
        hdfs = new HdfsUtil();
        userDir = System.getProperty("user.dir");
        hdfs.connect();
    }

    @AfterAll
    public static void cleanup() {
        System.out.println("关闭HDFS");
        hdfs.close();
    }

    @Test
    @DisplayName("HDFS配置文件")
    void testHdfsConfigure(){
        Map<String, String> conf = new HashMap<String, String>();
        // conf.put("dfs.replication", "1");
        hdfs.setConfiguration(conf);
        for (Map.Entry<String, String> e : hdfs.getConfiguration()) {
            System.out.println(e.getKey() + "\t" + e.getValue());
        }
    }

    @Test
    @DisplayName("HDFS-判断目录或文件是否存在")
    void testExists() throws IOException {
        System.out.println(hdfs.exists("/test"));
    }

    @Test
    @DisplayName("HDFS-创建目录")
    void testMkdirs() throws IOException {
        System.out.println(hdfs.exists("/test001"));
        System.out.println(hdfs.mkdirs("/test001"));
        System.out.println(hdfs.exists("/test001"));
    }

    @Test
    @DisplayName("HDFS-列出指定目录下的文件以及块的信息")
    void testListFiles() throws IOException {
        hdfs.listFiles("/", true);
    }

    @Test
    @DisplayName("HDFS-删除目录或文件")
    void testRename() throws IOException {
        System.out.println(hdfs.rename("/hdfs-site.xml", "/hdfs-site-rename.xml"));
    }

    @Test
    @DisplayName("HDFS-删除目录或文件")
    void testDelete() throws IOException {
        System.out.println(hdfs.delete("/hdfs-site1.xml", true));
        System.out.println(hdfs.delete("/hdfs-site-rename.xml", true));
    }

    @Test
    @DisplayName("HDFS-上传文件")
    void testUpload() throws IOException {
        String srcPath = Paths
                .get(userDir, "src", "main", "resources", "hadoop-3.1.1", "hdfs-site.xml")
                .toString();
        System.out.println(hdfs.upload(srcPath, "/hdfs-site.xml"));
    }

    @Test
    @DisplayName("HDFS-写文件")
    void testCreate() throws IOException {
        System.out.println(hdfs.create("/test.txt", "哈哈哈哈哈"));
    }

    @Test
    @DisplayName("HDFS-追加文件内容")
    void testAppend() throws IOException {
        // 报错：Failed to replace a bad datanode on the existing pipeline due to no more good datanodes
        Map<String, String> conf = new HashMap<>();
        conf.put("dfs.client.block.write.replace-datanode-on-failure.policy","NEVER");
        conf.put("dfs.client.block.write.replace-datanode-on-failure.enable","true");

        hdfs.setConfiguration(conf);
        hdfs.connect();
        System.out.println(hdfs.create("/test.txt", "哈哈哈哈哈"));
        System.out.println(hdfs.append("/test.txt", "哒哒哒哒哒"));
    }

    @Test
    @DisplayName("HDFS-下载文件")
    void testDownload() throws IOException {
        String dst = Paths
                .get(userDir, "src", "main", "resources", "download", "test.txt")
                .toString();
        System.out.println(hdfs.download("/test.txt", dst));
    }

    @Test
    @DisplayName("HDFS-读文件")
    void testRead() throws IOException {
        System.out.println(hdfs.read("/test.txt"));
    }
}