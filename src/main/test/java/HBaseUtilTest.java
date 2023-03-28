package src.main.test.java;

import org.apache.hadoop.hbase.TableName;
import utils.HBaseUtil;

import java.lang.reflect.Array;
import java.util.Arrays;


public class HBaseUtilTest {
    public static void main(String[] args) {
        try {
            HBaseUtil.connect();
            TableName[] tNames = HBaseUtil.getConnection().getAdmin().listTableNames();
            System.out.println(Arrays.toString(tNames));
            HBaseUtil.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
