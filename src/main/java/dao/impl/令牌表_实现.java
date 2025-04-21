package dao.impl;

import dao.MySQL_Account;
import dao.entity.令牌表;
import dao.mapper.令牌表_接口;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class 令牌表_实现 implements 令牌表_接口 {
    @Override
    public 令牌表 get_令牌表(String id) {
        String sql = "SELECT * FROM 令牌表 WHERE 令牌值 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setString(1, id);//第一个问号参数为id
            try (ResultSet rs = pstmt.executeQuery()) {// 执行查询，并将结果存储在ResultSet对象中
                if (rs.next()) {
                    return new 令牌表(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void insert令牌表(令牌表 data) {
        String sql = "INSERT INTO 令牌表 (令牌值 ,ip ,账号 ,失效时间 ) VALUES ( ?, ?, ?, ?)";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setString(1, data.get令牌值());
            pstmt.setString(2, data.getIp());
            pstmt.setInt(3, data.get账号());
            pstmt.setTimestamp(4, data.get失效时间());
            pstmt.executeUpdate();// 执行提交
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete令牌表(String id) {
        String sql = "DELETE FROM 令牌表 WHERE 令牌值 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setString(1, id);
            pstmt.executeUpdate();// 执行提交
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
