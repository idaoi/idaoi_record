package dao.mapper;

import dao.entity.用户信息表;

import java.sql.Blob;

public interface 用户信息表_接口 {
    用户信息表 get_用户信息表(int id);//获取一条记录内容，通过id获取数据，无返回空

    void insert用户信息表(用户信息表 data);// 增加一条内容

    void update用户信息表(用户信息表 data);// 更改一条内容

    void update头像(int id, Blob img);// 根据id修改头像

    void update标签(int id, String label);// 根据id修改标签

    void delete用户信息表(int id);// 删除一条内容

    void Change字数(int id, int num);// 修改，添加num的值

    void Change篇数(int id, int num);// 修改，添加num的值

    void Change次数(int id, int num);// 修改，添加num的值

    void Change时间(int id, int num);// 修改，添加num的值
}
