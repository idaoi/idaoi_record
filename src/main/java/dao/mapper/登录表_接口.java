// 登录表相关的操作，登录表这几条，一般要的话都要，就不设置获取某一项的接口了。
package dao.mapper;

import dao.entity.登录表;

import java.sql.Timestamp;

public interface 登录表_接口 {
    登录表 get_登录表(int id);//获取一条记录内容，通过id获取登录表数据，无返回空

    void insert登录表(登录表 data);// 增加一条内容

    void update登录表(登录表 data);// 更改一条内容

    void update登录表(int id,Timestamp 退出时间);// 修改退出时间

    void update登录表(int id,int 保持链接);// 修改是否保持链接

    void delete登录表(int id);// 删除一条内容
}
