package dao.mapper;

import dao.entity.标签表;

import java.sql.Blob;
import java.util.List;

public interface 标签表_接口 {
    标签表 get_标签表(int id);//获取一条记录内容，通过id获取数据，无返回空

    int insert标签表(标签表 data);// 增加一条内容

    void update标签表(标签表 data);// 更改一条内容

    void update标签表(int id, String str);// 更改一条内容

    void delete标签表(int id);// 删除一条内容

    List<标签表> get_all_标签表(int 账号);//获取记录内容，通过账号获取数据，无返回空列表
}
