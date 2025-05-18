package dao.mapper;

import dao.entity.文件表;

import java.util.List;


public interface 文件表_接口 {
    文件表 get_文件表(int id, int name);//获取一条记录内容，通过id获取数据，无返回空

    int insert文件表(文件表 data);// 增加一条内容

    void delete文件表(int id, int name);// 删除一条内容

    void delete文件表(int essays_id, int name, List<Integer> file_id);// 删除不在列表中的文件
}
