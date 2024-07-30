package zcb.hdfs;

import org.junit.jupiter.api.*;
import org.zcb.hdfs.HdfsUtil;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


public class HdfsUtilTest {

    public static HdfsUtil hdfs = null;

    @BeforeAll
    public static void init() {
        System.out.println("连接HDFS");
        hdfs = new HdfsUtil();
        hdfs.connect();
    }

    @AfterAll
    public static void cleanup() {
        System.out.println("关闭HDFS");
        hdfs.close();
    }

    @Test
    @DisplayName("测试HDFS配置文件设置")
    @Disabled
    void testHdfsConfigure(){
        Map<String, String> conf = new HashMap<String, String>();
        // conf.put("dfs.replication", "1");
        hdfs.setConfiguration(conf);
        for (Map.Entry<String, String> e : hdfs.getConfiguration()) {
            System.out.println(e.getKey() + "\t" + e.getValue());
        }
    }

    @Test
    @DisplayName("测试文件上传和下载")
    // @Disabled
    void testFileUploadAndDownload() throws IOException {
        // 更改操作用户
        // System.setProperty("HADOOP_USER_NAME", "hadoop");
        String userDir = System.getProperty("user.dir");
        String srcPath = Paths
                .get(userDir, "src", "main", "resources", "hadoop-3.1.1", "hdfs-site.xml")
                .toString();
        hdfs.put(srcPath, "/hdfs-site.xml", true);
    }
}