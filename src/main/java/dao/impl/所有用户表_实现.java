package dao.impl;

import dao.MySQL_Account;
import dao.entity.所有用户表;
import dao.mapper.所有用户表_接口;

import java.sql.*;

public class 所有用户表_实现 implements 所有用户表_接口 {

    @Override
    public 所有用户表 get_所有用户表(int id) {// 通过id获取所有用户表数据，无返回空
        String sql = "SELECT * FROM 所有用户表 WHERE 账号 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setInt(1, id);//第一个问号参数为id
            try (ResultSet rs = pstmt.executeQuery()) {// 执行查询，并将结果存储在ResultSet对象中
                if (rs.next()) {
                    return new 所有用户表(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int insert所有用户表(所有用户表 data) {
        String sql = "INSERT INTO 所有用户表 (用户名 ,密码 ,其他值, 密码盐值) VALUES (?, ?, ?, ?)";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {//创建PreparedStatement对象
            // 账号会自动递增
            pstmt.setBlob(1, data.get用户名());
            pstmt.setBlob(2, data.get密码());
            pstmt.setBlob(3, data.get其他值());
            pstmt.setString(4, data.get密码盐值());
            pstmt.executeUpdate();// 执行提交
            // 获取生成的键
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    // 主键列名为账号
                    return generatedKeys.getInt(1);
                } else {
                    return -1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public void update所有用户表(所有用户表 data) {
        String sql = "UPDATE 所有用户表 SET  用户名 = ?, 密码 = ?, 其他值 = ?,密码盐值 = ?  WHERE 账号 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setBlob(1, data.get用户名());
            pstmt.setBlob(2, data.get密码());
            pstmt.setBlob(3, data.get其他值());
            pstmt.setString(4, data.get密码盐值());
            pstmt.setInt(5, data.get账号());
            pstmt.executeUpdate();// 执行提交
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update所有用户表(int id,Blob password, String salt) {
        String sql = "UPDATE 所有用户表 SET  密码 = ?,密码盐值 = ?  WHERE 账号 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setBlob(1, password);
            pstmt.setString(2, salt);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();// 执行提交
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete所有用户表(int id) {
        String sql = "DELETE FROM 所有用户表 WHERE 账号 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setInt(1, id);
            pstmt.executeUpdate();// 执行提交
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
