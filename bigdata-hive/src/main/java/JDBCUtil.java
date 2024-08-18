import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Objects;
import java.util.Properties;

/**
 * MySQL 5.6.37
 * 使用JDBC协议连接HiveServer2
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
}
