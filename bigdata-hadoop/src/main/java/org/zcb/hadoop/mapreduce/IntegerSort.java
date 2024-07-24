package org.zcb.hadoop.mapreduce;

import org.zcb.hadoop.mapreduce.mapper.IntegerSortMapper;
import org.zcb.hadoop.mapreduce.reducer.IntegerSortReducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.zcb.hadoop.HdfsUtil;

/**
 实验内容:
    现有多个输入文件，每个文件中的每行内容均为一个整数。要求读取所有文件中的整数， 进行升序排序后，
    输出到一个新的文件中，输出的数据格式为每行两个整数，第一个数字为 第二个整数的排序位次，第二个
    整数为原待排列的整数。
 */
public class IntegerSort {
    public static void main(String[] args) {
        try {
            // Hadoop 配置
            Configuration conf = HdfsUtil.getConfiguration();

            // 解析参数
            String[] otherArgs = new GenericOptionsParser(conf, args)
                    .getRemainingArgs();

            // 用户自定义Job
            Job job = Job.getInstance(conf, "IntegerSort");
            job.setJarByClass(IntegerSort.class);

            // 设置job的Mapper
            job.setMapperClass(IntegerSortMapper.class);
            job.setOutputKeyClass(IntWritable.class);
            job.setOutputValueClass(IntWritable.class);

            // 设置job的Reducer
            job.setReducerClass(IntegerSortReducer.class);

            // 设置输入、输出文件的路径
            FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
            Path outputPath = new Path(otherArgs[1]);
            if(outputPath.getFileSystem(conf).exists(outputPath))
                outputPath.getFileSystem(conf).delete(outputPath, true);
            FileOutputFormat.setOutputPath(job, outputPath);

            job.waitForCompletion(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
