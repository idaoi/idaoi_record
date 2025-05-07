package dao.mapper;

import dao.entity.随笔内容表;

public interface 随笔内容表_接口 {
    随笔内容表 get_随笔内容表(int id);//获取一条记录内容，通过随笔索引号获取随笔数据，无返回空
    void insert随笔内容表(随笔内容表 data);// 增加一条内容
    void update随笔内容表(随笔内容表 data);// 更改一条内容
    void update随笔内容表(int id ,String data);// 更改一条内容
    void delete随笔索引表(int id);// 删除一条内容
}
