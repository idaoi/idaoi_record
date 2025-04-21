package dao.entity;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class 令牌表 implements Serializable {
    // 令牌值 ip 账号 失效时间
    // '令牌值', 'ip', '账号', '失效时间'
    private static final long serialVersionUID = 1L;
    private String 令牌值;
    private String ip;
    private int 账号;
    private Timestamp 失效时间;

    public 令牌表(ResultSet rs) throws SQLException {//用rs直接构造
        this.令牌值 = rs.getString("令牌值");
        this.ip = rs.getString("ip");
        this.账号 = rs.getInt("账号");
        this.失效时间 = rs.getTimestamp("失效时间");
    }


    // 这些构造函数和方法没有标注的都是用编译器自带的"右键->生成"，自动生成的，
    // 要改的话，修改上面的部分，然后再自动生成即可

    public 令牌表(String 令牌值, String ip, int 账号, Timestamp 失效时间) {
        this.令牌值 = 令牌值;
        this.ip = ip;
        this.账号 = 账号;
        this.失效时间 = 失效时间;
    }

    public String get令牌值() {
        return 令牌值;
    }

    public void set令牌值(String 令牌值) {
        this.令牌值 = 令牌值;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int get账号() {
        return 账号;
    }

    public void set账号(int 账号) {
        this.账号 = 账号;
    }

    public Timestamp get失效时间() {
        return 失效时间;
    }

    public void set失效时间(Timestamp 失效时间) {
        this.失效时间 = 失效时间;
    }
}
