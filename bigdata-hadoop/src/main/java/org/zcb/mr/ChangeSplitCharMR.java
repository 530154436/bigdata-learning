package org.zcb.mr;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;


public class ChangeSplitCharMR extends Configured implements Tool {
    @Override
    public int run(String[] strings) throws Exception {
        /*
         * 构建Job
         */
        Job job = Job.getInstance(this.getConf(), "changeSplit");
        job.setJarByClass(ChangeSplitCharMR.class);

        /*
         * 配置Job
         */
        //input：读取需要转换的文件
        job.setInputFormatClass(TextInputFormat.class);
        Path inputPath = new Path("/data/cases/case01/test01.txt");
        FileInputFormat.setInputPaths(job, inputPath);

        //map：调用Mapper
        job.setMapperClass(ChangeSplitMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(NullWritable.class);

        //reduce：不需要Reduce过程
        job.setNumReduceTasks(0);

        //output
        job.setOutputFormatClass(TextOutputFormat.class);
        Path outputPath = new Path("/data/output/changeSplit");
        TextOutputFormat.setOutputPath(job, outputPath);

        /**
         * 提交Job
         */
        return job.waitForCompletion(true) ? 0 : -1;
    }

    public static class ChangeSplitMapper extends Mapper<LongWritable, Text, Text, NullWritable> {
        //定义输出的Key
        private Text outputKey = new Text();
        //定义输出的Value
        private NullWritable outputValue = NullWritable.get();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            //获取每条数据
            String line = value.toString();
            //将里面的||转换为|
            String newLine = line.replaceAll("\\|\\|", "|");
            //替换后的内容作为Key
            this.outputKey.set(newLine);
            //输出结果
            context.write(this.outputKey, this.outputValue);
        }
    }

    //程序入口
    public static void main(String[] args) throws Exception {
        //调用run
        Configuration conf = new Configuration();
        conf.addResource("hadoop-3.1.1/core-site.xml");
        conf.addResource("hadoop-3.1.1/hdfs-site.xml");
        conf.set("dfs.client.use.datanode.hostname", "true"); // 解决DataNode找不到的问题

        int status = ToolRunner.run(conf, new ChangeSplitCharMR(), args);
        System.exit(status);
    }
}
