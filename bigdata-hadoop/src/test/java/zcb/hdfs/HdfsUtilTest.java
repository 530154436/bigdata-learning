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
    @DisplayName("HDFS上传文件")
    void testUpload() throws IOException {
        String srcPath = Paths
                .get(userDir, "src", "main", "resources", "hadoop-3.1.1", "hdfs-site.xml")
                .toString();
        hdfs.upload(srcPath, "/hdfs-site.xml");
    }

    @Test
    @DisplayName("HDFS下载文件")
    void testDownload() throws IOException {
        String dst = Paths
                .get(userDir, "src", "main", "resources", "download", "hdfs-site.xml")
                .toString();
        hdfs.download("/hdfs-site.xml", dst);
    }

    @Test
    @DisplayName("HDFS删除文件")
    void testDelete() throws IOException {
        hdfs.delete("/hdfs-site.xml", true);
    }
}