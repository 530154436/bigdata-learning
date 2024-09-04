package udf;

import com.google.gson.Gson;
import org.apache.hadoop.hive.ql.exec.UDF;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ToJson extends UDF {
    public ToJson() {
    }

    public String evaluate(String str) {
        Map<String, Object> data = new HashMap<String, Object>();
        String[] split = str.split(";");
        Arrays.stream(split).forEach(new Consumer<String>() {
            public void accept(String s) {
                String[] split1 = s.split(":");
                String key = split1[0];
                Integer value = Integer.parseInt(split1[1]);
                data.put(key, value);
            }
        });
        return (new Gson()).toJson(data);
    }

    public static void main(String[] args) {
        String content = "100:1;206:1;242:1;244:1;37:3;62:2;63:1;71:2;98:1;99:1";
        System.out.println((new ToJson()).evaluate(content));
    }
}
