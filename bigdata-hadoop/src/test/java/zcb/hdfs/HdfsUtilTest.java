package zcb.hdfs;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.zcb.hdfs.HdfsUtil;
import java.io.IOException;


public class HdfsUtilTest {

    public HdfsUtilTest(){
        HdfsUtil.connect();
    }

    @Test
    void testFact() throws IOException {
        assertTrue(HdfsUtil.mkdir("test01"));
    }
}