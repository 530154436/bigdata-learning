package udf;


import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;


public class MapTopWordCnt extends UDF {
    public MapTopWordCnt() {
    }

    public Map<String, Integer> evaluate(Integer num, Map<String, Integer>... params) {
        final Map<String, Integer> result = new HashMap<String, Integer>();
        if (params == null) {
            return null;
        } else {
            Arrays.stream(params).forEach(new Consumer<Map<String, Integer>>() {
                public void accept(Map<String, Integer> submap) {
                    if (submap != null) {
                        for (Entry<String, Integer> unit : submap.entrySet()) {
                            String key = unit.getKey();
                            Integer value = unit.getValue();
                            if (!result.containsKey(key)) {
                                result.put(key, value);
                            } else {
                                result.put(key, (Integer) result.get(key) + value);
                            }
                        }
                    }

                }
            });
            ArrayList<Entry<String, Integer>> entries = new ArrayList<Entry<String, Integer>>(result.entrySet());
            entries.sort(new Comparator<Entry<String, Integer>>() {
                public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
                    return Objects.equals(o1.getValue(), o2.getValue()) ? 0 : (Integer) o2.getValue() - (Integer) o1.getValue();
                }
            });
            Map<String, Integer> wordcount2 = new LinkedHashMap<String, Integer>();
            num = Math.min(num, entries.size());

            for(int i = 0; i < num; ++i) {
                wordcount2.put(
                        ((Entry<String, Integer>)entries.get(i)).getKey(),
                        ((Entry<String, Integer>)entries.get(i)).getValue()
                );
            }

            return wordcount2.isEmpty() ? null : wordcount2;
        }
    }

    public static void main(String[] args) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);
        System.out.println((new MapTopWordCnt()).evaluate(3, map, null));
    }
}
