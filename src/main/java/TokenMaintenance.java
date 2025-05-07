// 这个类用于随着服务器启动，执行令牌维护，
// 会启动进程
// 第一个:每2分钟删除令牌表中过期的令牌
// 第二个:每5分钟重置一回用户链接状态。

import dao.MySQL_Account;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class TokenMaintenance implements ServletContextListener {
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // 每2分钟删除令牌表中过期的令牌
        Runnable Delete_Long_Time_2min = () -> {
            String sql = "DELETE FROM 令牌表 WHERE 失效时间<NOW()";
            try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
                pstmt.executeUpdate();// 执行提交
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };

        // 每5分钟重置链接属性
        Runnable Change_Link_5min = () -> {
            String sql1 = "UPDATE 登录表 login " +// 登录表 别名login
                    "JOIN 用户信息表 user_data ON login.账号 = user_data.账号 " +// 链接两表，条件为账号相等
                    "SET login.退出时间 = NOW() , " + // 设置退出时间
                    "user_data.时间 = user_data.时间 + TIMESTAMPDIFF(MINUTE,login.登录时间,NOW())" +// 退出时间增加登录到当前时间差
                    "WHERE login.退出时间 IS NULL AND login.保持链接 = 0"; // 只更新退出时间为空以及未保持连接的账号


            String sql2 = "UPDATE 登录表 SET 保持链接 = 0 WHERE 退出时间 IS NULL AND 保持链接 = 1 ";
            try (Connection conn = MySQL_Account.getDataSource().getConnection()) {//获取链接
                try (PreparedStatement pstmt = conn.prepareStatement(sql1)) {//创建PreparedStatement对象
                    pstmt.executeUpdate();// 执行提交
                }
                try (PreparedStatement pstmt = conn.prepareStatement(sql2)) {//创建PreparedStatement对象
                    pstmt.executeUpdate();// 执行提交
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };

        // 安排任务
        scheduler.scheduleAtFixedRate(Delete_Long_Time_2min, 0, 2, TimeUnit.MINUTES);
        scheduler.scheduleAtFixedRate(Change_Link_5min, 0, 3, TimeUnit.MINUTES);
        System.out.println("Token maintenance has been initiated 已启动令牌维护");
    }

}
