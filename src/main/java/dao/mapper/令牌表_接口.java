package dao.mapper;
import dao.entity.令牌表;

public interface 令牌表_接口 {// 只用增删即可，令牌这个不用改
    令牌表  get_令牌表(String id);//获取一条记录内容，通过id获取数据，无返回空
    void insert令牌表(令牌表 data);// 增加一条内容
    void delete令牌表(String id);// 删除一条内容

}
