package dao.impl;

import dao.MySQL_Account;
import dao.entity.登录表;
import dao.mapper.登录表_接口;

import java.sql.*;

public class 登录表_实现 implements 登录表_接口 {
    @Override
    public 登录表 get_登录表(int id) {// 通过id获取登录表数据，无返回空
        String sql = "SELECT * FROM 登录表 WHERE 账号 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setInt(1, id);//第一个问号参数为id
            try (ResultSet rs = pstmt.executeQuery()) {// 执行查询，并将结果存储在ResultSet对象中
                if (rs.next()) {
                    return new 登录表(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void insert登录表(登录表 data) {
        String sql = "INSERT INTO 登录表 (账号 ,ip ,登录时间 ,退出时间 ,附加值 ,保持登录密钥 ,实际保持登录密钥 ,保持链接) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setInt(1, data.get账号());
            pstmt.setString(2, data.getIp());
            pstmt.setTimestamp(3, data.get登录时间());
            if (data.get退出时间() == null) {
                pstmt.setNull(4, Types.TIMESTAMP);
            } else {
                pstmt.setTimestamp(4, data.get退出时间());
            }
            pstmt.setString(5, data.get附加值());
            pstmt.setString(6, data.get保持登录密钥());
            pstmt.setString(7, data.get实际保持登录密钥());
            pstmt.setInt(8, data.get保持链接());
            pstmt.executeUpdate();// 执行提交
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update登录表(登录表 data) {
        String sql = "UPDATE 登录表 SET ip = ?, 登录时间 = ?, 退出时间 = ?, 附加值 = ?, 保持登录密钥 = ?,实际保持登录密钥 = ?, 保持链接 = ? WHERE 账号 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setString(1, data.getIp());
            pstmt.setTimestamp(2, data.get登录时间());
            if (data.get退出时间() == null) {
                pstmt.setNull(3, Types.TIMESTAMP);
            } else {
                pstmt.setTimestamp(3, data.get退出时间());
            }
            pstmt.setString(4, data.get附加值());
            pstmt.setString(5, data.get保持登录密钥());
            pstmt.setString(6, data.get实际保持登录密钥());
            pstmt.setInt(7, data.get保持链接());
            pstmt.setInt(8, data.get账号());
            pstmt.executeUpdate();// 执行提交
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update登录表(int id, Timestamp 退出时间) {
        String sql = "UPDATE 登录表 SET 退出时间 = ? WHERE 账号 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            if (退出时间 == null) {
                pstmt.setNull(1, Types.TIMESTAMP);
            } else {
                pstmt.setTimestamp(1, 退出时间);
            }
            pstmt.setInt(2, id);
            pstmt.executeUpdate();// 执行提交
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update登录表(int id, int 保持链接) {
        String sql = "UPDATE 登录表 SET 保持链接 = ? WHERE 账号 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setInt(1, 保持链接);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();// 执行提交
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete登录表(int id) {
        String sql = "DELETE FROM 登录表 WHERE 账号 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setInt(1, id);
            pstmt.executeUpdate();// 执行提交
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
