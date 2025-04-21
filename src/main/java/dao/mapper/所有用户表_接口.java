package dao.mapper;

import dao.entity.所有用户表;

import java.sql.Blob;

public interface 所有用户表_接口 {
    所有用户表 get_所有用户表(int id);//获取一条记录内容，通过id获取数据，无返回空

    int insert所有用户表(所有用户表 data);// 增加一条内容

    void update所有用户表(所有用户表 data);// 更改一条内容

    void update所有用户表(int id,Blob password, String salt);// 更改一条内容

    void delete所有用户表(int id);// 删除一条内容
}
