package org.zcb.hadoop.mapreduce.mapper;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class IntegerSortMapper extends Mapper<Object, Text, IntWritable, IntWritable> {

    private final IntWritable number = new IntWritable();
    private final IntWritable one = new IntWritable(1);

    @Override
    protected void map(Object key, Text value, Context context)
            throws IOException, InterruptedException {
        number.set(Integer.parseInt(value.toString()));
        context.write(number, one);
    }
}
