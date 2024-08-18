import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

public class JDBCUtilTest {
    @Test
    @DisplayName("测试链接")
    void testHiveJdbcConnect() throws SQLException {
        Connection conn = JDBCUtil.getConnection();
        System.out.println(conn);
    }
}