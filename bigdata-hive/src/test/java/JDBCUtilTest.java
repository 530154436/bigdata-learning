import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * create database itheima;--创建数据库
 * show databases;--列出所有数据库
 * use itheima;--切换数据库
 * create table t_student(id int,name varchar(255)); --建表
 * insert into table t_student values(1,"allen"); --插入一条数据
 * select * from t_student; --查询表数据
 */
public class JDBCUtilTest {
    @Test
    @DisplayName("测试连接Hive")
    void testHiveJdbcConnect() throws SQLException {
        Connection conn = JDBCUtil.getConnection();
        System.out.println(conn);
    }

    @Test
    @DisplayName("测试插入数据")
    void testInsert() throws SQLException {
        String sql = "insert into table t_student values(?,?)";
        Assertions.assertTrue(JDBCUtil.update(sql, 1, "刘备") != 0);
        Assertions.assertTrue(JDBCUtil.update(sql, 2, "关羽") != 0);
        Assertions.assertTrue(JDBCUtil.update(sql, 3, "张飞") != 0);
    }

    @Test
    @DisplayName("测试批量插入数据")
    void testBatchInsert() throws SQLException {
        String sql = "insert into table t_student values(?,?) ";
        Object[][] param = new Object[3][2];
        param[0][0] = 10;
        param[0][1] = "曹操";
        param[1][0] = 11;
        param[1][1] = "郭嘉";
        param[2][0] = 12;
        param[2][1] = "司马懿";
        Assertions.assertTrue(JDBCUtil.batchUpdate(sql, param) != 0);
    }

    @Test
    @DisplayName("测试删除数据")
    void testDelete() throws SQLException {
        String sql = "insert into table t_student values(?,?)";
        Assertions.assertTrue(JDBCUtil.update(sql, 200, "司马炎") != 0);
        sql = "delete from t_student where id = ?";
        Assertions.assertTrue(JDBCUtil.update(sql, 200) != 0);
    }

    @Test
    public void testBatchDelete() {
        String sql = "insert into table t_student values(?,?)";
        Assertions.assertTrue(JDBCUtil.update(sql, 101, "卧龙") != 0);
        Assertions.assertTrue(JDBCUtil.update(sql, 102, "凤雏") != 0);

        sql = "delete from t_student where id = ? ";
        Object[][] param = new Object[3][1];
        param[0][0] = 10;
        param[1][0] = 11;
        Assertions.assertTrue(JDBCUtil.batchUpdate(sql, param) != 0);
    }
}