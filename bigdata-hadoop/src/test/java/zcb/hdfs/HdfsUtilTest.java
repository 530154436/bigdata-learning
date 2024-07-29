package zcb.hdfs;

import org.junit.jupiter.api.*;
import org.zcb.hdfs.HdfsUtil;
import java.io.IOException;


public class HdfsUtilTest {

    @BeforeAll
    public static void init() {
        System.out.println("连接HDFS");
        HdfsUtil.connect();
    }

    @AfterAll
    public static void cleanup() {
        System.out.println("关闭HDFS");
        HdfsUtil.close();
    }

    @Test
    @DisplayName("测试HDFS目录操作")
    @Disabled
    void testHdfsDirectoryOperation() throws IOException {
        Assertions.assertTrue(HdfsUtil.mkdir("test01"));
    }
}