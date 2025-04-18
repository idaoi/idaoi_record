package dao.entity;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class 登录表 implements Serializable {
    //账号 ip 登录时间 退出时间 附加值 保持登录密钥 保持链接
    //'账号' ,'ip' ,'登录时间' ,'退出时间' ,'附加值' ,'保持登录密钥','实际保持登录密钥','保持链接'
    private static final long serialVersionUID = 1L;
    private int 账号;
    private String ip;
    private Timestamp 登录时间;
    private Timestamp 退出时间;
    private String 附加值;
    private String 保持登录密钥;
    private String 实际保持登录密钥;
    private int 保持链接;

    public 登录表(ResultSet rs) throws SQLException {//用rs直接构造登录表项目
        this.账号 = rs.getInt("账号");
        this.ip = rs.getString("ip");
        this.登录时间 = rs.getTimestamp("登录时间");
        this.退出时间 = rs.getTimestamp("退出时间");
        this.附加值 = rs.getString("附加值");
        this.保持登录密钥 = rs.getString("保持登录密钥");
        this.实际保持登录密钥 = rs.getString("实际保持登录密钥");
        this.保持链接 = rs.getInt("保持链接");
    }

    // 这些构造函数和方法没有标注的都是用编译器自带的"右键->生成"，自动生成的，
    // 要改的话，修改上面的部分，然后再自动生成即可

    public 登录表(int 账号, String ip, Timestamp 登录时间, Timestamp 退出时间, String 附加值, String 保持登录密钥, String 实际保持登录密钥, int 保持链接) {
        this.账号 = 账号;
        this.ip = ip;
        this.登录时间 = 登录时间;
        this.退出时间 = 退出时间;
        this.附加值 = 附加值;
        this.保持登录密钥 = 保持登录密钥;
        this.实际保持登录密钥 = 实际保持登录密钥;
        this.保持链接 = 保持链接;
    }

    public int get账号() {
        return 账号;
    }

    public void set账号(int 账号) {
        this.账号 = 账号;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Timestamp get登录时间() {
        return 登录时间;
    }

    public void set登录时间(Timestamp 登录时间) {
        this.登录时间 = 登录时间;
    }

    public Timestamp get退出时间() {
        return 退出时间;
    }

    public void set退出时间(Timestamp 退出时间) {
        this.退出时间 = 退出时间;
    }

    public String get附加值() {
        return 附加值;
    }

    public void set附加值(String 附加值) {
        this.附加值 = 附加值;
    }

    public String get保持登录密钥() {
        return 保持登录密钥;
    }

    public void set保持登录密钥(String 保持登录密钥) {
        this.保持登录密钥 = 保持登录密钥;
    }

    public String get实际保持登录密钥() {
        return 实际保持登录密钥;
    }

    public void set实际保持登录密钥(String 实际保持登录密钥) {
        this.实际保持登录密钥 = 实际保持登录密钥;
    }

    public int get保持链接() {
        return 保持链接;
    }

    public void set保持链接(int 保持链接) {
        this.保持链接 = 保持链接;
    }
}