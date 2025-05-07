package dao.impl;

import dao.MySQL_Account;
import dao.entity.标签表;
import dao.entity.随笔索引表;
import dao.mapper.标签表_接口;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class 标签表_实现 implements 标签表_接口 {
    @Override
    public 标签表 get_标签表(int id) {
        String sql = "SELECT * FROM 标签表 WHERE 标签号 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setInt(1, id);//第一个问号参数为id
            try (ResultSet rs = pstmt.executeQuery()) {// 执行查询，并将结果存储在ResultSet对象中
                if (rs.next()) {
                    return new 标签表(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int insert标签表(标签表 data) {
        String sql = "INSERT INTO 标签表 (账号,标签名) " +
                "VALUES ( ?, ?)";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setInt(1, data.get账号());
            pstmt.setString(2, data.get标签名());
            pstmt.executeUpdate();// 执行提交
            // 获取生成的键
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    // 主键列名为标签号
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
    public void update标签表(标签表 data) {
        String sql = "UPDATE 标签表 SET  账号 = ?, 标签名 = ? WHERE 标签号 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setInt(1, data.get账号());
            pstmt.setString(2, data.get标签名());
            pstmt.setInt(3, data.get标签号());
            pstmt.executeUpdate();// 执行提交
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update标签表(int id, String str, int name) {
        String sql = "UPDATE 标签表 SET 标签名 = ? WHERE 标签号 = ? and 账号=?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setString(1, str);
            pstmt.setInt(2, id);
            pstmt.setInt(3, name);
            pstmt.executeUpdate();// 执行提交
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete标签表(int id) {
        String sql = "DELETE FROM 标签表 WHERE 标签号 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setInt(1, id);
            pstmt.executeUpdate();// 执行提交
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete标签表(int id, int name) {
        String sql = "DELETE FROM 标签表 WHERE 标签号 = ? and 账号 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setInt(1, id);
            pstmt.setInt(2, name);
            pstmt.executeUpdate();// 执行提交
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<标签表> get_all_标签表(int 账号) {
        String sql = "SELECT * FROM 标签表 WHERE 账号 = ?";
        List<标签表> data_List = new ArrayList<>();
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setInt(1, 账号);//第一个问号参数为id
            try (ResultSet rs = pstmt.executeQuery()) {// 执行查询，并将结果存储在ResultSet对象中
                while (rs.next()) {
                    data_List.add(new 标签表(rs));
                }
            }
            return data_List;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
