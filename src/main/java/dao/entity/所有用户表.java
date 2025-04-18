package dao.entity;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

public class 所有用户表 implements Serializable{
    //账号 用户名 密码 其他值 密码盐值
    //'账号', '用户名', '密码' ,'其他值','密码盐值'
    private static final long serialVersionUID = 1L;
    private int 账号;
    private Blob 用户名;
    private Blob 密码;
    private Blob 其他值;
    private String 密码盐值;

    public 所有用户表(ResultSet rs) throws SQLException {//用rs直接构造
        this.账号 = rs.getInt("账号");
        this.用户名 = rs.getBlob("用户名");
        this.密码 = rs.getBlob("密码");
        this.其他值 = rs.getBlob("其他值");
        this.密码盐值 = rs.getString("密码盐值");
    }

    // 这些构造函数和方法没有标注的都是用编译器自带的"右键->生成"，自动生成的，
    // 要改的话，修改上面的部分，然后再自动生成即可

    public 所有用户表(int 账号, Blob 用户名, Blob 密码, Blob 其他值, String 密码盐值) {
        this.账号 = 账号;
        this.用户名 = 用户名;
        this.密码 = 密码;
        this.其他值 = 其他值;
        this.密码盐值 = 密码盐值;
    }

    public int get账号() {
        return 账号;
    }

    public void set账号(int 账号) {
        this.账号 = 账号;
    }

    public Blob get用户名() {
        return 用户名;
    }

    public void set用户名(Blob 用户名) {
        this.用户名 = 用户名;
    }

    public Blob get密码() {
        return 密码;
    }

    public void set密码(Blob 密码) {
        this.密码 = 密码;
    }

    public Blob get其他值() {
        return 其他值;
    }

    public void set其他值(Blob 其他值) {
        this.其他值 = 其他值;
    }

    public String get密码盐值() {
        return 密码盐值;
    }

    public void set密码盐值(String 密码盐值) {
        this.密码盐值 = 密码盐值;
    }
}
