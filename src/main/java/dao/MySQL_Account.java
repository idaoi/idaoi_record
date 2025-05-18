// 这个用来定义mysql账户密码，用于实现时被继承，以统一获取账号密码。
// 后来做出了修改，使用数据库连接池Apache DBCP，不再被继承
package dao;

import org.apache.commons.dbcp2.BasicDataSource;
import javax.sql.DataSource;

public class MySQL_Account {
    private static BasicDataSource dataSource;

    static {
        dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/i道i_记录");
        dataSource.setUsername("i道i_记录");
        dataSource.setPassword("idaoi_record");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // 配置连接池大小等参数
        dataSource.setInitialSize(5);
        dataSource.setMaxTotal(10);
        dataSource.setMinIdle(5);
        dataSource.setMaxIdle(10);
    }

    // 获取数据库连接
    public static DataSource getDataSource() {
        return dataSource;
    }

}
