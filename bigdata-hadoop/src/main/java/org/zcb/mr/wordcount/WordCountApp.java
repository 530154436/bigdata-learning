package org.zcb.mr.wordcount;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.zcb.hdfs.HdfsUtil;
import org.zcb.mr.wordcount.component.WordCountMapper;
import org.zcb.mr.wordcount.component.WordCountReducer;
import java.io.IOException;


/**
 * 组装作业(词频统计) 并提交到集群运行
 * 文本文件: /wordcount/input.txt
 */
public class WordCountApp {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        HdfsUtil hdfs = new HdfsUtil();
        hdfs.connect();
        String[] inputPaths = new String[] {"/wordcount/input.txt"};
        String outputPath = "/wordcount/output/WordCountApp";

        // 创建一个 Job
        Job job = Job.getInstance(hdfs.getConfiguration());

        // 设置运行的主类、Mapper 和 Reducer
        job.setJarByClass(WordCountApp.class);
        job.setMapperClass(WordCountMapper.class);
        job.setReducerClass(WordCountReducer.class);

        // 设置 Mapper、Reducer 输出的 key 和 value 的类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // 如果输出目录已经存在，则必须先删除，否则重复运行程序时会抛出异常
        hdfs.delete(outputPath, true);

        // 设置作业输入文件和输出文件的路径
        FileInputFormat.addInputPath(job, new Path(inputPaths[0]));
        FileOutputFormat.setOutputPath(job, new Path(outputPath));

        // 将作业提交到群集并等待它完成，参数设置为 true 代表打印显示对应的进度
        job.waitForCompletion(true);

        // 关闭之前创建的 fileSystem
        hdfs.close();
    }
}

