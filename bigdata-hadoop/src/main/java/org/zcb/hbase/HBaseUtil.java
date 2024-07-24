package org.zcb.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.regionserver.NoSuchColumnFamilyException;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.Iterator;

class ResultPrinter {
    public static String getPutInfo(String columnFamily, String column, String value){
        return "Record[columnFamily=" + columnFamily +
                ", column=" + column + ", value=" + value + "]: " + "添加成功";
    }

    public static String getDeleteInfo(String rowKey){
        return "Record[rowKey=" + rowKey + "]: " + "删除成功";
    }

    public static String getResultInfo(Result res){
        if(res.isEmpty())
            return "null\n";
        StringBuilder builder = new StringBuilder();
        for(Cell cell: res.listCells()){
            builder.append("Row[");
            builder.append("rowKey=" + Bytes.toString(CellUtil.cloneRow(cell)) + ", ");
            builder.append("columnFamily=" + Bytes.toString(CellUtil.cloneFamily(cell)) + ", ");
            builder.append("column=" + Bytes.toString(CellUtil.cloneQualifier(cell)) + ", ");
            builder.append("value=" + Bytes.toString(CellUtil.cloneValue(cell)));
            builder.append("]\n");
        }
        return builder.toString();
    }

    public static String getScanResultInfo(Iterator<Result> res){
        if(!res.hasNext())
            return "null";
        StringBuilder builder = new StringBuilder();
        while (res.hasNext()){
            builder.append(getResultInfo(res.next()) + '\n');
        }
        return builder.toString();
    }
}

class TablePrinter {
    public static String getCreateInfo(String tableName){
        return "Table[tableName=" + tableName + "]: " + "创建成功";
    }
    public static String getDeleteInfo(String tableName){
        return "Table[tableName=" + tableName + "]: " + "删除成功";
    }
    public static String getExistsInfo(String tableName){
        return "Table[tableName=" + tableName + "]: " + "存在";
    }
}

public class HBaseUtil {

    private static Configuration config = null;
    private static Connection connection = null;
    static {
        config = HBaseConfiguration.create();
        config.addResource("hbase/hbase-site.xml");
    }

    /**
     * 连接HBase
     */
    public static void connect(){
        try {
            connection = ConnectionFactory.createConnection(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Connection getConnection() {
        return connection;
    }

    /**
     * 关闭连接
     */
    public static void close(){
        try {
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建表
     * @param tableName 表名
     * @param columnFamilies 存储记录各个域名称的数组(列族)
     */
    public static boolean createTable(String tableName, String[] columnFamilies){
        if(connection == null)
            connect();
        try {
            Admin admin = connection.getAdmin();
            TableName tName = TableName.valueOf(tableName);
            if(admin.tableExists(tName)){
                System.out.println(TablePrinter.getExistsInfo(tableName));
                return false;
            }

            // 创建表描述器构造器
            TableDescriptorBuilder tdBuilder = TableDescriptorBuilder.newBuilder(tName);

            // 循环添加列族信息
            for (String cf : columnFamilies) {
                ColumnFamilyDescriptorBuilder cfdBuilder =
                        ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(cf));
                tdBuilder.setColumnFamily(cfdBuilder.build());
            }

            // 执行创建表的操作
            admin.createTable(tdBuilder.build());
            System.out.println(TablePrinter.getCreateInfo(tableName));

            return true;
        } catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除表
     * @param tableName 表名
     */
    public static boolean deleteTable(String tableName){
        if(connection == null)
            connect();
        try {
            Admin admin = connection.getAdmin();
            TableName tName = TableName.valueOf(tableName);
            if(admin.tableExists(tName)){
                admin.disableTable(tName);
                admin.deleteTable(tName);
                System.out.println(TablePrinter.getDeleteInfo(tableName));
                return true;
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 添加/更新单条记录
     * @param tableName        表名
     * @param rowKey           行键
     * @param columnFamily     列族
     * @param column           列限定符
     * @param value            值
     */
    public static boolean put(String tableName, String rowKey,
                              String columnFamily, String column, String value){
        if(connection == null)
            connect();
        try {
            Table table = connection.getTable(TableName.valueOf(tableName));
            Put put = new Put(Bytes.toBytes(rowKey));

            // 添加记录
            put.addColumn(Bytes.toBytes(columnFamily),
                    Bytes.toBytes(column), Bytes.toBytes(value));
            System.out.println(
                    ResultPrinter.getPutInfo(columnFamily, column, value));

            table.put(put);
            table.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 添加/更新多条记录
     * @param tableName         表名
     * @param rowKey            行键
     * @param columnFamilies    列族
     * @param columns           列限定符
     * @param values            值
     */
    public static boolean put(String tableName, String rowKey,
                              String[] columnFamilies, String[] columns, String[] values){
        if(connection == null)
            connect();
        try {
            Table table = connection.getTable(TableName.valueOf(tableName));
            Put put = new Put(Bytes.toBytes(rowKey));

            // 列和值不一致
            if(columnFamilies.length != columns.length & columns.length != values.length){
                System.out.println("Table=" + tableName + ", " +
                        "RowKey=" + rowKey + ": 列族、列限定符和值长度不等.");
                return false;
            }

            // 添加记录
            for(int i=0; i<columnFamilies.length; i++){
                put.addColumn(Bytes.toBytes(columnFamilies[i]),
                        Bytes.toBytes(columns[i]), Bytes.toBytes(values[i]));
            }

            table.put(put);
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除表中的某一行
     * @param tableName 表名
     * @param rowKey    行
     */
    public static boolean delete(String tableName, String rowKey){
        if(connection == null)
            connect();
        try {
            Table table = connection.getTable(TableName.valueOf(tableName));
            Delete delete = new Delete(Bytes.toBytes(rowKey));

            table.delete(delete);
            table.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 查询单条(rowKey)记录
     * @param tableName         表名
     * @param rowKey            行键
     * @param columnFamily      列族
     * @param column            列限定符
     */
    public static Result get(String tableName, String rowKey,
                             String columnFamily, String column){
        if(connection == null)
            connect();
        try {
            Table table = connection.getTable(TableName.valueOf(tableName));
            Get get = new Get(Bytes.toBytes(rowKey));

            if(columnFamily != null)
                get.addFamily(Bytes.toBytes(columnFamily));
            if(column != null)
                get.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column));

            Result res = table.get(get);
            System.out.println(ResultPrinter.getResultInfo(res));

            table.close();
            return res;
        } catch (NoSuchColumnFamilyException e){
            System.out.println("NoSuchColumnFamilyException: " + columnFamily + '\n');
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询多条记录
     * @param tableName         表名
     * @param columnFamily      列族
     * @param column            列限定符
     */
    public static Iterator<Result> scan(String tableName,
                                        String columnFamily,
                                        String column){
        if(connection == null)
            connect();
        try {
            Table table = connection.getTable(TableName.valueOf(tableName));
            ResultScanner scanner = null;

            if(column != null)
                scanner = table.getScanner(
                        Bytes.toBytes(columnFamily), Bytes.toBytes(column));
            else
                scanner = table.getScanner(Bytes.toBytes(columnFamily));

            Iterator<Result> res = scanner.iterator();
            System.out.println(ResultPrinter.getScanResultInfo(res));

            table.close();
            return res;
        } catch (NoSuchColumnFamilyException e){
            System.out.println("NoSuchColumnFamilyException: " + columnFamily + '\n');
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
