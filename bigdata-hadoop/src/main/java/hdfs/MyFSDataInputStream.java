package hdfs;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.Path;
import utils.HdfsUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MyFSDataInputStream extends FSDataInputStream {

    public MyFSDataInputStream(InputStream in) {
        super(in);
    }

    /**
     * 按行读取 HDFS 中指定文件
     * @param rowNum 行号
     * @return 特定行号的内容
     */
    public String readLine(int rowNum) throws IOException {
        BufferedReader buffer = new BufferedReader(new InputStreamReader(this.in));
        String line = buffer.readLine();
        System.out.println("第1行: " + line);
        for (int i = 2; i <= rowNum & line != null; i++) {
            line = buffer.readLine();
            System.out.println("第" + i + "行: " + line);
        }
        buffer.close();
        return line;
    }

    public static void main(String[] args) {
        try {
            int rowNum = Integer.parseInt(args[0]);
            String fileName = "/test/myfile.txt";
            HdfsUtil.connect();
            FSDataInputStream is = HdfsUtil.getFs().open(new Path(fileName));

            MyFSDataInputStream fis = new MyFSDataInputStream(is);
            System.out.println("行号"+ rowNum + ": " + fis.readLine(rowNum));
            is.close();
            fis.close();
            HdfsUtil.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
