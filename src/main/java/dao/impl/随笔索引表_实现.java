package dao.impl;

import dao.MySQL_Account;
import dao.entity.随笔索引表;
import dao.mapper.随笔索引表_接口;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class 随笔索引表_实现 implements 随笔索引表_接口 {

    @Override
    public 随笔索引表 get_随笔索引表(int id) {
        String sql = "SELECT * FROM 随笔索引表 WHERE 随笔号 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setInt(1, id);//第一个问号参数为id
            try (ResultSet rs = pstmt.executeQuery()) {// 执行查询，并将结果存储在ResultSet对象中
                if (rs.next()) {
                    return new 随笔索引表(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int insert随笔索引表(随笔索引表 data) {
        String sql = "INSERT INTO 随笔索引表 (随笔名 ,标签 ,第一次编辑时间 ,最后编辑时间 ,内容 ,加密 ,加密内容 ,附加值 ,账号) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setString(1, data.get随笔名());
            pstmt.setInt(2, data.get标签());
            pstmt.setTimestamp(3, data.get第一次编辑时间());
            pstmt.setTimestamp(4, data.get最后编辑时间());
            pstmt.setInt(5, data.get内容());
            pstmt.setString(6, data.get加密());
            pstmt.setString(7, data.get加密内容());
            pstmt.setString(8, data.get附加值());
            pstmt.setInt(9, data.get账号());
            pstmt.executeUpdate();// 执行提交
            // 获取生成的键
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    // 主键列名为随笔号
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
    public void update随笔索引表(随笔索引表 data) {
        String sql = "UPDATE 随笔索引表 SET 随笔名 = ?, 标签 = ?, 第一次编辑时间 = ?, 最后编辑时间 = ?, 内容 = ?,加密 = ?, 加密内容 = ?, 附加值 = ? , 账号 = ?  " +
                "WHERE 随笔号 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setString(1, data.get随笔名());
            pstmt.setInt(2, data.get标签());
            pstmt.setTimestamp(3, data.get第一次编辑时间());
            pstmt.setTimestamp(4, data.get最后编辑时间());
            pstmt.setInt(5, data.get内容());
            pstmt.setString(6, data.get加密());
            pstmt.setString(7, data.get加密内容());
            pstmt.setString(8, data.get附加值());
            pstmt.setInt(9, data.get账号());
            pstmt.setInt(10, data.get随笔号());
            pstmt.executeUpdate();// 执行提交
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update随笔索引表(int id, Timestamp 最后编辑时间) {
        String sql = "UPDATE 随笔索引表 SET 最后编辑时间 = ? WHERE 随笔号 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setTimestamp(1, 最后编辑时间);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();// 执行提交
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<随笔索引表> get_all_随笔索引表(int 账号) {
        String sql = "SELECT 随笔号 ,随笔名 ,标签 ,第一次编辑时间 ,最后编辑时间 ,内容 ,加密  ,附加值  FROM 随笔索引表 WHERE 账号 = ?";
        List<随笔索引表> data_List = new ArrayList<>();
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setInt(1, 账号);//第一个问号参数为id
            try (ResultSet rs = pstmt.executeQuery()) {// 执行查询，并将结果存储在ResultSet对象中
                while (rs.next()) {
                    data_List.add(new 随笔索引表(rs));
                }
            }
            return data_List;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public void delete随笔索引表(int id) {
        String sql = "DELETE FROM 随笔索引表 WHERE 随笔号 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setInt(1, id);
            pstmt.executeUpdate();// 执行提交
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
