package dao.impl;

import dao.MySQL_Account;
import dao.entity.用户信息表;
import dao.mapper.用户信息表_接口;

import java.sql.*;


public class 用户信息表_实现 implements 用户信息表_接口 {
    @Override
    public 用户信息表 get_用户信息表(int id) {// 通过id获取用户信息表数据，无返回空
        String sql = "SELECT * FROM 用户信息表 WHERE 账号 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setInt(1, id);//第一个问号参数为id
            try (ResultSet rs = pstmt.executeQuery()) {// 执行查询，并将结果存储在ResultSet对象中
                if (rs.next()) {
                    return new 用户信息表(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void insert用户信息表(用户信息表 data) {
        String sql = "INSERT INTO 用户信息表 (账号,标签,附加值1,附加值2,头像,字数,篇数,次数,时间) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setInt(1, data.get账号());
            pstmt.setString(2, data.get标签());
            pstmt.setString(3, data.get附加值1());
            pstmt.setBlob(4, data.get附加值2());
            pstmt.setBlob(5, data.get头像());
            pstmt.setInt(6, data.get字数());
            pstmt.setInt(7, data.get篇数());
            pstmt.setInt(8, data.get次数());
            pstmt.setInt(9, data.get时间());
            pstmt.executeUpdate();// 执行提交
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update用户信息表(用户信息表 data) {
        String sql = "UPDATE 用户信息表 SET  标签 = ?, 附加值1 = ?, 附加值2 = ?, 头像 = ? ,字数= ?,篇数= ?,次数= ?,时间= ?  WHERE 账号 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setString(1, data.get标签());
            pstmt.setString(2, data.get附加值1());
            pstmt.setBlob(3, data.get附加值2());
            pstmt.setBlob(4, data.get头像());
            pstmt.setInt(5, data.get字数());
            pstmt.setInt(6, data.get篇数());
            pstmt.setInt(7, data.get次数());
            pstmt.setInt(8, data.get时间());
            pstmt.setInt(9, data.get账号());
            pstmt.executeUpdate();// 执行提交
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update头像(int id, Blob img) {
        String sql = "UPDATE 用户信息表 SET  头像 = ?  WHERE 账号 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setBlob(1, img);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();// 执行提交
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update标签(int id, String label) {
        String sql = "UPDATE 用户信息表 SET  标签 = ?  WHERE 账号 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setString(1, label);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();// 执行提交
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete用户信息表(int id) {
        String sql = "DELETE FROM 用户信息表 WHERE 账号 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setInt(1, id);
            pstmt.executeUpdate();// 执行提交
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void Change字数(int id, int num) {
        String sql = "UPDATE 用户信息表 SET  字数 = 字数 + ?  WHERE 账号 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setInt(1, num);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();// 执行提交
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void Change篇数(int id, int num) {
        String sql = "UPDATE 用户信息表 SET  篇数 = 篇数 + ?  WHERE 账号 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setInt(1, num);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();// 执行提交
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void Change次数(int id, int num) {
        String sql = "UPDATE 用户信息表 SET  次数 = 次数 + ?  WHERE 账号 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setInt(1, num);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();// 执行提交
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void Change时间(int id, int num) {
        String sql = "UPDATE 用户信息表 SET  时间 = 时间 + ?  WHERE 账号 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setInt(1, num);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();// 执行提交
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
