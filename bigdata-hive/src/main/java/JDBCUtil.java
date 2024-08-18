import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.*;

/**
 * MySQL 5.6.37
 * 使用JDBC协议连接HiveServer2
 * 参考：<a href="https://github.com/freesoul17/DbUtils/blob/master/DbUtils.java">...</a>
 */
public class JDBCUtil {
    private static String url;
    private static String user;
    private static String password;
    private static String driver;

    // 使用静态代码块读取数据库配置信息
    static{
        try {
            // 1. ClassLoader 类加载器获取src路径下的文件
            Properties pro = new Properties();
            ClassLoader classLoader = JDBCUtil.class.getClassLoader();
            URL res  = classLoader.getResource("hive.properties");
            String path = Objects.requireNonNull(res).getPath();
            pro.load(new FileReader(path));
            System.out.println(path);

            // 2. 获取数据，赋值
            url = pro.getProperty("url");
            user = pro.getProperty("username");
            password = pro.getProperty("password");
            driver = pro.getProperty("driverClass");
            System.out.println(url);

            //4. 注册驱动
            Class.forName(driver);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 线程安全
    private static final ThreadLocal<Connection> local = new ThreadLocal<>();

    /**
     * 获取数据库连接对象
     *
     * @return 连接对象
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = local.get();
        if (conn == null) {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println(conn);
            local.set(conn);
        }
        return conn;
    }

    /**
     * 关闭资源
     * @param conn 连接对象
     * @param ps 数据库操作对象
     * @param rs 结果集
     */
    public static void close(Connection conn, Statement ps, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            }catch(SQLException e) {
                e.printStackTrace();
            }
        }
        if (ps != null) {
            try {
                ps.close();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
                local.remove();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 增删改操作
     * @param sql        sql语句
     * @param params     预编译的参数
     * @return 返回影响的行数，若返回-1，则更新失败
     */
    public static int update(String sql, Object... params){
        Connection conn = null;
        PreparedStatement psmt = null;
        int result = 0;
        try {
            //获取数据库连接对象
            conn = JDBCUtil.getConnection();
            //获取预编译语句对象
            psmt = conn.prepareStatement(sql);
            //给预编译语句赋值
            for (int i = 0; i < params.length; i++) {
                psmt.setObject(i+1, params[i]);
            }
            //执行SQL语句获取执行结果
            result = psmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //关闭数据库连接
            JDBCUtil.close(conn, psmt, null);
        }
        return result;
    }

    /**
     * 批量 增删改操作(带参数）
     *
     * @param sql        sql语句
     * @param param      预编译的参数
     * @return 返回插入成功的次数，若失败则返回0
     */
    public static int batchUpdate(String sql, Object[][] param) {
        Connection conn = null;
        PreparedStatement psmt = null;
        int count = param.length;
        try {
            conn = JDBCUtil.getConnection();
            psmt = conn.prepareStatement(sql);
            for (int i = 0; i < param.length; i++) {
                for (int j = 0; j < param[i].length; j++) {
                    psmt.setObject(j + 1, param[i][j]);
                }
                psmt.addBatch();
                if (count > 100000 && i % 1000 == 0) {
                    psmt.executeBatch();
                }
            }
            psmt.executeBatch();
            psmt.clearBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.close(conn, psmt, null);
        }
        return 0;
    }

    /**
     * 查询操作
     * @param sql        sql语句
     * @param params     预编译的参数
     * @return 返回的结果集，形如[{id=1,name=王五},{id=2,name=李四}],没有记录则返回空[]
     */
    public static List<HashMap<String, Object>> executeQuery(String sql, Object... params){
        Connection conn = null;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        try {
            //获取数据库连接对象
            conn = JDBCUtil.getConnection();
            //获取预编译语句对象
            psmt = conn.prepareStatement(sql);
            //给预编译语句赋值
            for (int i = 0; i < params.length; i++) {
                psmt.setObject(i+1,params[i]);
            }
            //执行SQL语句获取结果集
            rs = psmt.executeQuery();
            //处理结果集
            List<HashMap<String, Object>> list = new ArrayList<>();
            ResultSetMetaData metaData = psmt.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (rs.next()) {
                HashMap<String, Object> hashMap = new HashMap<>();
                for (int i = 0; i < columnCount; i++) {
                    Object columnVal = rs.getObject(i + 1);
                    String columnName = metaData.getColumnName(i + 1);
                    hashMap.put(columnName, columnVal);
                }
                list.add(hashMap);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭数据库连接
            JDBCUtil.close(conn,psmt,rs);
        }
        return null;
    }
}
