package udf;


import org.apache.hadoop.hive.ql.exec.UDF;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;

public class TopWordCnt extends UDF {
    public TopWordCnt() {
    }

    public Map<String, Integer> evaluate(Integer num, String value) {
        if (value != null && !"".equals(value)) {
            String[] split = value.split(";");
            final Map<String, List<String>> result = new HashMap<String, List<String>>();
            Arrays.stream(split).forEach(new Consumer<String>() {
                public void accept(String s) {
                    if (!result.containsKey(s)) {
                        result.put(s, new ArrayList<String>());
                        ((List<String>)result.get(s)).add(s);
                    } else {
                        ((List<String>)result.get(s)).add(s);
                    }

                }
            });
            ArrayList<Entry<String, List<String>>> entries = new ArrayList(result.entrySet());
            Collections.sort(entries, new Comparator<Entry<String, List<String>>>() {
                public int compare(Entry<String, List<String>> o1, Entry<String, List<String>> o2) {
                    return ((List)o1.getValue()).size() == ((List)o2.getValue()).size() ? 0 : ((List)o2.getValue()).size() - ((List)o1.getValue()).size();
                }
            });
            num = Math.min(num, entries.size());
            int i = 0;

            LinkedHashMap result2;
            for(result2 = new LinkedHashMap(); i < num; ++i) {
                result2.put(((Entry)entries.get(i)).getKey(), ((List)((Entry)entries.get(i)).getValue()).size());
            }

            return result2;
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        String field = "天线;光学;大视场;天线;光学;天线;天线;天线;天线;能量;透镜;电荷;电荷;电荷;氧化物半导体;空间光调制器;金属氧化物;光电探测器;通信链路;通信系统;图像采集;CMOS;信号处理;激光器";
        System.out.println((new TopWordCnt()).evaluate(5, field));
    }
}