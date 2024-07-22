package mapreduce;

import mapreduce.mapper.FileMergeMapper;
import mapreduce.reducer.FileMergeReducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import utils.HdfsUtil;

/**
 实验内容:
    对于每行至少具有三个字段的两个输入文件，即文件 A 和文件 B，请编写MapReduce 程序，
    对两个文件进行合并，并剔除其中重复的内容，得到一个新的输出文件 C。
 */
public class FileMerge {
    public static void main(String[] args) {
        try {
            // Hadoop 配置
            Configuration conf = HdfsUtil.getConfiguration();

            // 解析参数
            String[] otherArgs = new GenericOptionsParser(conf, args)
                    .getRemainingArgs();

            // 用户自定义Job
            Job job = Job.getInstance(conf, "FileMerge");
            job.setJarByClass(FileMerge.class);

            // 设置job的Mapper
            job.setMapperClass(FileMergeMapper.class);
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(IntWritable.class);

            // 设置job的Reducer
            job.setReducerClass(FileMergeReducer.class);

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
