package mapreduce.reducer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class FileMergeReducer extends Reducer<Text, IntWritable, Text, Text> {
    private final Text emptyText = new Text("");
    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context)
            throws IOException, InterruptedException {
        // 每个 key 是唯一的
        context.write(key, emptyText);
    }
}
