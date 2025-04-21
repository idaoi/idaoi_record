package dao.entity;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class 标签表  implements Serializable {
    //标签号 账号 标签名
    //'标签号', '账号', '标签名'
    private static final long serialVersionUID = 1L;
    private int 标签号;
    private int 账号;
    private String 标签名;


    public 标签表(ResultSet rs) throws SQLException {//用rs直接构造
        this.标签号 = rs.getInt("标签号");
        this.账号 = rs.getInt("账号");
        this.标签名 = rs.getString("标签名");
    }

    // 这些构造函数和方法没有标注的都是用编译器自带的"右键->生成"，自动生成的，
    // 要改的话，修改上面的部分，然后再自动生成即可


    public 标签表(int 标签号, int 账号, String 标签名) {
        this.标签号 = 标签号;
        this.账号 = 账号;
        this.标签名 = 标签名;
    }

    public int get标签号() {
        return 标签号;
    }

    public void set标签号(int 标签号) {
        this.标签号 = 标签号;
    }

    public int get账号() {
        return 账号;
    }

    public void set账号(int 账号) {
        this.账号 = 账号;
    }

    public String get标签名() {
        return 标签名;
    }

    public void set标签名(String 标签名) {
        this.标签名 = 标签名;
    }
}
