package org.zcb.hadoop.hdfs;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;
import org.apache.hadoop.io.IOUtils;

public class FsURL{
    static {
        URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
    }
    public static void main(String[] args) {
        URL url = null;
        try {
            url = new URL("hdfs://cluster1:9000/test/myfile.txt");
            InputStream inputStream = url.openStream();
            IOUtils.copyBytes(inputStream, System.out, new Configuration());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
