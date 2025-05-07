package dao.mapper;

import dao.entity.随笔索引表;

import java.sql.Timestamp;
import java.util.List;

public interface 随笔索引表_接口 {
    随笔索引表 get_随笔索引表(int id);//获取一条记录内容，通过id获取登录表数据，无返回空

    int insert随笔索引表(随笔索引表 data);// 增加一条内容

    void update随笔索引表(随笔索引表 data);// 更改一条内容

    void update随笔索引表(int id, Timestamp 最后编辑时间);// 修改最后编辑时间

    List<随笔索引表> get_all_随笔索引表(int 账号);// 发送一个账号的所有随笔索引，无返回空列表

    void delete随笔索引表(int id, int name);// 删除一条内容
}
