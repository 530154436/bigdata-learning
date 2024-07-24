import org.zcb.hadoop.HdfsUtil;

public class HdfsUtilTest {
    public static void main(String[] args) {
        try {
            HdfsUtil.connect();
            // org.zcb.org.zcb.hadoop.hdfs.HdfsUtil.mkdir("/test4/");
            HdfsUtil.put("/Users/zhengchubin/Desktop/课程作业.txt",
                         "/user/zhengchubin/课程作业.txt");
            HdfsUtil.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
