package hbase;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import utils.HBaseUtil;

public class StudentCourse {
    public static void main(String[] args) {
        HBaseUtil.connect();

        // 1. 创建表
        // createTable();

        // 2. 添加记录
        // addRecords();

        // 3. 查询数据
        // scanColumn();

        // 4. 修改数据
        // modifyData();

        // 5. 删除数据
        // deleteRow();
    }

    public static void createTable(){
        String[] student = {"Info", "Score"};
        String[] course = {"C_Name", "C_Credit"};
        boolean isSuccess = HBaseUtil.createTable("Student", student);
        if(!isSuccess){
            HBaseUtil.deleteTable("Student");
            HBaseUtil.createTable("Student", student);
        }
        HBaseUtil.createTable("Course", course);
    }

    public static void addRecords(){
        HBaseUtil.put("Course", "123001", "C_Name", "","Math");
        HBaseUtil.put("Course", "123001", "C_Credit", "","2.0");
        HBaseUtil.put("Course", "123002", "C_Name", "","Comr Science" );
        HBaseUtil.put("Course", "123002", "C_Credit", "", "5.0");
        HBaseUtil.put("Course", "123003", "C_Credit", "","5.0");
        HBaseUtil.put("Course", "123003", "C_Name", "", "English" );

        // String[] columnFamilies = {"C_Name", "C_Credit"};
        // String[] columns = {"", ""};
        // String[] values = {"Math", "2.0"};
        // HBaseUtil.put("Course", "123003", columnFamilies, columns, values);

        HBaseUtil.put("Student", "2015001", "Info", "S_Name", "Zhangsan");
        HBaseUtil.put("Student", "2015002", "Info", "S_Name", "Mary");
        HBaseUtil.put("Student", "2015003", "Info", "S_Name", "Lisi");
        HBaseUtil.put("Student", "2015001", "Info", "S_Sex", "male");
        HBaseUtil.put("Student", "2015002", "Info", "S_Sex", "female");
        HBaseUtil.put("Student", "2015003", "Info", "S_Sex", "male");
        HBaseUtil.put("Student", "2015001", "Info", "S_Age", "23");
        HBaseUtil.put("Student", "2015002", "Info", "S_Age", "22");
        HBaseUtil.put("Student", "2015003", "Info", "S_Age", "24" );

        HBaseUtil.put("Student", "2015001", "Score", "Math", "86" );
        HBaseUtil.put("Student", "2015001", "Score", "English", "69" );
        HBaseUtil.put("Student", "2015002", "Score", "Comr Science", "77" );
        HBaseUtil.put("Student", "2015002", "Score", "English", "99" );
        HBaseUtil.put("Student", "2015003", "Score", "Math", "98" );
        HBaseUtil.put("Student", "2015003", "Score", "Comr Science", "95");
    }

    public static void scanColumn(){
        HBaseUtil.get("Student", "2015001", null, null);
        HBaseUtil.get("Student", "2015001", "Score", null);
        HBaseUtil.get("Student", "2015001", "Score", "Math");
        HBaseUtil.get("Student", "2015001", "Score", "YuWen");

        HBaseUtil.scan("Student", "Score", null);
        HBaseUtil.scan("Student", "Score", "Math");
        HBaseUtil.scan("Student", "Score", "Biology");
    }

    public static void modifyData(){
        HBaseUtil.get("Student", "2015001", "Score", "Math");
        HBaseUtil.put("Student", "2015001", "Score", "Math", "100");
        HBaseUtil.get("Student", "2015001", "Score", "Math");
    }

    public static void deleteRow(){
        HBaseUtil.get("Student", "2015001", null, null);
        HBaseUtil.delete("Student", "2015001");
        HBaseUtil.get("Student", "2015001", null, null);
    }
}
