package algo.ch03_枚举;

import org.apache.lucene.util.RamUsageEstimator;
import java.util.HashMap;

public class Homework01 {

    int[] getTable(){
        return new int[]{
                0, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2,
                2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
                2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
                3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
                3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
                3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4,
                4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
                4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
                4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
                4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
                4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
                5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
                5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
                5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6,
                6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
                6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 8
        };
    }

    HashMap<Character, Integer> getHashMap(){
        int[] table = getTable();
        HashMap<Character, Integer> map = new HashMap<>();
        for(int i=0; i<table.length; i++){
            map.put((char)i, table[i]);
        }
        return map;
    }

    int calcNumberOfBits01(char value) {
        int count = 0;
        while (value != 0){
            int iReminder = value % 2;
            if(iReminder == 1)
                count++;
            value /= 2;
        }
        return count;
    }

    int calcNumberOfBits02(int[] TABLE, char value) {
        return TABLE[value];
    }

    int calcNumberOfBits03(
            HashMap<Character, Integer> CHAR_MAPPER, Character value) {
        return CHAR_MAPPER.get(value);
    };

    void test01(int loop){
        long startTime = System.currentTimeMillis();

        for(int i=0; i<loop; i++){
            for(char j=1; j<256; j++){
                calcNumberOfBits01(j);
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("calcNumberOfBits01 程序运行时间：" + (endTime - startTime) +"ms");
    }

    void test02(int loop){
        int[] TABLE = getTable();
        long startTime = System.currentTimeMillis();

        for(int i=0; i<loop; i++){
            for(char j=1; j<256; j++){
                calcNumberOfBits02(TABLE, j);
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("calcNumberOfBits02 程序运行时间：" + (endTime - startTime) +"ms");

        String size = RamUsageEstimator.humanSizeOf(TABLE);
        System.out.println("TABLE内存大小为:"  + size);
    }

    void test03(int loop){
        HashMap<Character, Integer> CHAR_MAP = getHashMap();
        long startTime = System.currentTimeMillis();

        for(int i=0; i<loop; i++){
            for(char j=1; j<256; j++){
                calcNumberOfBits03(CHAR_MAP, j);
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("calcNumberOfBits03 程序运行时间：" + (endTime - startTime) +"ms");

        String size = RamUsageEstimator.humanSizeOf(CHAR_MAP);
        System.out.println("CHAR_MAP内存大小为:"  + size);
    }

    public static void main(String[] args){
        Homework01 h = new Homework01();
        h.test01(100000);
        h.test02(100000);
        h.test03(100000);
    }
}
