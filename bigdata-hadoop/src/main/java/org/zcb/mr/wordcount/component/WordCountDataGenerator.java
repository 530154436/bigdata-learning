package org.zcb.mr.wordcount.component;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.FileSystem;
import org.zcb.hdfs.HdfsUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 产生词频统计模拟数据
 * 参考：https://github.com/heibaiying/BigData-Notes/blob/master/code/Hadoop/hadoop-word-count/
 */
public class WordCountDataGenerator {
    public static final List<String> WORD_LIST = Arrays.asList("Spark", "Hadoop", "HBase", "Kafka", "Flink", "Hive");

    /**
     * 模拟产生词频数据
     *
     * @return 词频数据
     */
    private static String generateData() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            Collections.shuffle(WORD_LIST);
            Random random = new Random();
            int endIndex = random.nextInt(WORD_LIST.size()) % (WORD_LIST.size()) + 1;
            String line = StringUtils.join(WORD_LIST.toArray(), "\t", 0, endIndex);
            builder.append(line).append("\n");
        }
        return builder.toString();
    }


    /**
     * 模拟产生词频数据并输出到本地
     *
     * @param outputPath 输出文件路径
     */
    private static void generateDataToLocal(String outputPath) {
        try {
            java.nio.file.Path path = Paths.get(outputPath);
            if (Files.exists(path)) {
                Files.delete(path);
            }
            Files.write(path, generateData().getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 模拟产生词频数据并输出到HDFS
     */
    private static void generateDataToHDFS() {
        FileSystem fileSystem = null;
        try {
            HdfsUtil hdfs = new HdfsUtil();
            hdfs.connect();
            hdfs.create("/wordcount/input.txt", generateData());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        generateDataToHDFS();
    }
}
