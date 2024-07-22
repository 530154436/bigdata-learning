package mapreduce.mapper;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class FileMergeMapper extends Mapper<Object, Text, Text, IntWritable> {

    private final Text text = new Text();
    private final IntWritable one = new IntWritable(1);

    @Override
    protected void map(Object key, Text value, Context context)
            throws IOException, InterruptedException {
        text.set(value.toString());
        context.write(text, one);
    }
}
