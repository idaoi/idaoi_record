package dao.entity;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

public class 用户信息表 implements Serializable {
    //账号 标签 附加值1 附加值2 头像 字数 篇数 次数 时间
    //'账号', '标签', '附加值1' ,'附加值2', '头像','字数','篇数','次数','时间'
    private static final long serialVersionUID = 1L;
    private int 账号;
    private String 标签;
    private String 附加值1;
    private Blob 附加值2;
    private Blob 头像;
    private int 字数;
    private int 篇数;
    private int 次数;
    private int 时间;


    public 用户信息表(ResultSet rs) throws SQLException {//用rs直接构造
        this.账号 = rs.getInt("账号");
        this.标签 = rs.getString("标签");
        this.附加值1 = rs.getString("附加值1");
        this.附加值2 = rs.getBlob("附加值2");
        this.头像 = rs.getBlob("头像");
        this.字数 = rs.getInt("字数");
        this.篇数 = rs.getInt("篇数");
        this.次数 = rs.getInt("次数");
        this.时间 = rs.getInt("时间");
    }


    // 这些构造函数和方法没有标注的都是用编译器自带的"右键->生成"，自动生成的，
    // 要改的话，修改上面的部分，然后再自动生成即可

    public 用户信息表(int 账号, String 标签, String 附加值1, Blob 附加值2, Blob 头像, int 字数, int 篇数, int 次数, int 时间) {
        this.账号 = 账号;
        this.标签 = 标签;
        this.附加值1 = 附加值1;
        this.附加值2 = 附加值2;
        this.头像 = 头像;
        this.字数 = 字数;
        this.篇数 = 篇数;
        this.次数 = 次数;
        this.时间 = 时间;
    }

    public int get账号() {
        return 账号;
    }

    public void set账号(int 账号) {
        this.账号 = 账号;
    }

    public String get标签() {
        return 标签;
    }

    public void set标签(String 标签) {
        this.标签 = 标签;
    }

    public String get附加值1() {
        return 附加值1;
    }

    public void set附加值1(String 附加值1) {
        this.附加值1 = 附加值1;
    }

    public Blob get附加值2() {
        return 附加值2;
    }

    public void set附加值2(Blob 附加值2) {
        this.附加值2 = 附加值2;
    }

    public Blob get头像() {
        return 头像;
    }

    public void set头像(Blob 头像) {
        this.头像 = 头像;
    }

    public int get字数() {
        return 字数;
    }

    public void set字数(int 字数) {
        this.字数 = 字数;
    }

    public int get篇数() {
        return 篇数;
    }

    public void set篇数(int 篇数) {
        this.篇数 = 篇数;
    }

    public int get次数() {
        return 次数;
    }

    public void set次数(int 次数) {
        this.次数 = 次数;
    }

    public int get时间() {
        return 时间;
    }

    public void set时间(int 时间) {
        this.时间 = 时间;
    }
}