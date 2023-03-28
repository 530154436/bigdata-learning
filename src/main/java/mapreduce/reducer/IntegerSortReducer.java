package mapreduce.reducer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class IntegerSortReducer extends Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {

    private final IntWritable lineNum = new IntWritable(1);

    @Override
    protected void reduce(IntWritable key, Iterable<IntWritable> values, Context context)
            throws IOException, InterruptedException {
        // 每个 key 是排好序的
        context.write(lineNum, key);
        lineNum.set(lineNum.get() + 1);
    }
}
