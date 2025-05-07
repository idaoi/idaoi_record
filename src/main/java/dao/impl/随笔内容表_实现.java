package dao.impl;

import dao.MySQL_Account;
import dao.entity.随笔内容表;
import dao.mapper.随笔内容表_接口;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class 随笔内容表_实现 implements 随笔内容表_接口 {

    @Override
    public 随笔内容表 get_随笔内容表(int id) {
        String sql = "SELECT * FROM 随笔内容表 WHERE 随笔索引号 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setInt(1, id);//第一个问号参数为id
            try (ResultSet rs = pstmt.executeQuery()) {// 执行查询，并将结果存储在ResultSet对象中
                if (rs.next()) {
                    return new 随笔内容表(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void insert随笔内容表(随笔内容表 data) {
        String sql = "INSERT INTO 随笔内容表 (随笔索引号 ,随笔内容 ,附加值 ,账号 ) VALUES ( ?, ?, ?, ?)";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setInt(1, data.get随笔索引号());
            pstmt.setString(2, data.get随笔内容());
            pstmt.setString(3, data.get附加值());
            pstmt.setInt(4, data.get账号());
            pstmt.executeUpdate();// 执行提交
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update随笔内容表(随笔内容表 data) {
        String sql = "UPDATE 随笔内容表 SET  随笔内容 = ?, 附加值 = ? ,账号 = ? WHERE 随笔索引号 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setString(1, data.get随笔内容());
            pstmt.setString(2, data.get附加值());
            pstmt.setInt(3, data.get账号());
            pstmt.setInt(4, data.get随笔索引号());
            pstmt.executeUpdate();// 执行提交
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update随笔内容表(int id, String data) {
        String sql = "UPDATE 随笔内容表 SET  随笔内容 = ?WHERE 随笔索引号 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setString(1, data);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();// 执行提交
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete随笔索引表(int id) {// 这个因为外键的原因，其实应该不需要
        String sql = "DELETE FROM 随笔内容表 WHERE 随笔索引号 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setInt(1, id);
            pstmt.executeUpdate();// 执行提交
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
