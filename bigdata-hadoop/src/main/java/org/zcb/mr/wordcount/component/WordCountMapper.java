package org.zcb.mr.wordcount.component;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;


/**
 * 将每行数据按照指定分隔符进行拆分
 *
 * Mapper<KEYIN, VALUEIN, KEYOUT, VALUEOUT>
 * Mapper<LongWritable, Text, Text, IntWritable>
 * LongWritable，表示输入数据的键是一个长整型（通常表示文件中的字节偏移量）。
 * Text，表示输入数据的值是文本类型（即每一行的内容）。
 * Text，表示输出的数据键是文本类型（即每个单词）。
 * IntWritable，表示输出的数据值是整型（即每个单词的计数，1）。
 */
public class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] words = value.toString().split("\t");
        for(String word: words){
            context.write(new Text(word), new IntWritable(1));
        }
    }
}
