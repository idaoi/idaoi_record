package dao.impl;

import dao.MySQL_Account;
import dao.entity.文件表;
import dao.mapper.文件表_接口;

import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;

public class 文件表_实现 implements 文件表_接口 {
    @Override
    public 文件表 get_文件表(int id, int name) {
        String sql = "SELECT * FROM 文件表 WHERE 文件id = ? and 账号 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setInt(1, id);//第一个问号参数为id
            pstmt.setInt(2, name);//第一个问号参数为name
            try (ResultSet rs = pstmt.executeQuery()) {// 执行查询，并将结果存储在ResultSet对象中
                if (rs.next()) {
                    return new 文件表(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int insert文件表(文件表 data) {
        String sql = "INSERT INTO 文件表 (文件名 ,文件类型, 文件内容 ,账号 ,所属随笔 ) VALUES ( ?, ?, ?, ?, ?)";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {//创建PreparedStatement对象
            pstmt.setString(1, data.get文件名());
            pstmt.setString(2, data.get文件类型());
            pstmt.setBytes(3, data.get文件内容());
            pstmt.setInt(4, data.get账号());
            pstmt.setInt(5, data.get所属随笔());
            pstmt.executeUpdate();// 执行提交
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    // 主键列名为文件id
                    return generatedKeys.getInt(1);
                }
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public void delete文件表(int id, int name) {
        String sql = "DELETE FROM 文件表  WHERE 文件id = ? and 账号 = ?";
        try (Connection conn = MySQL_Account.getDataSource().getConnection();//获取链接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建PreparedStatement对象
            pstmt.setInt(1, id);//第一个问号参数为id
            pstmt.setInt(2, name);//第一个问号参数为name
            pstmt.executeUpdate();// 执行提交
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete文件表(int essays_id, int name, List<Integer> file_id) {
        String sql;
        if (file_id.isEmpty()) {
            // 如果列表为空，则删除所有匹配用户名和随笔号的记录
            sql = "DELETE FROM 文件表 WHERE 账号 = ? AND 所属随笔 = ?";
        } else {
            // 将列表转换为逗号分隔的字符串
            String fileIdsStr = file_id.stream().map(String::valueOf).collect(Collectors.joining(","));
            sql = "DELETE FROM 文件表 WHERE 账号 = ? AND 所属随笔 = ? AND 文件id NOT IN (" + fileIdsStr + ")";
        }
        try (Connection conn = MySQL_Account.getDataSource().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, name); // 设置用户名参数
            pstmt.setInt(2, essays_id); // 设置随笔号参数
            pstmt.executeUpdate(); // 执行更新
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
