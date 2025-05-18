package dao.entity;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class 文件表 implements Serializable {
    //文件id 文件名 文件类型 文件内容 账号 所属随笔
    //'文件id' ,'文件名' ,'文件类型' ,'文件内容' ,'账号' ,'所属随笔'
    private static final long serialVersionUID = 1L;
    private int 文件id;
    private String 文件名;
    private String 文件类型;
    private byte[] 文件内容;
    private int 账号;
    private int 所属随笔;

    public 文件表(ResultSet rs) throws SQLException {//用rs直接构造登录表项目
        this.文件id = rs.getInt("文件id");
        this.文件名 = rs.getString("文件名");
        this.文件类型 = rs.getString("文件类型");
        this.文件内容 = rs.getBytes("文件内容");
        this.账号 = rs.getInt("账号");
        this.所属随笔 = rs.getInt("所属随笔");
    }

    // 这些构造函数和方法没有标注的都是用编译器自带的"右键->生成"，自动生成的，
    // 要改的话，修改上面的部分，然后再自动生成即可

    public 文件表(int 文件id, String 文件名, String 文件类型, byte[] 文件内容, int 账号, int 所属随笔) {
        this.文件id = 文件id;
        this.文件名 = 文件名;
        this.文件类型 = 文件类型;
        this.文件内容 = 文件内容;
        this.账号 = 账号;
        this.所属随笔 = 所属随笔;
    }

    public int get文件id() {
        return 文件id;
    }

    public void set文件id(int 文件id) {
        this.文件id = 文件id;
    }

    public String get文件名() {
        return 文件名;
    }

    public void set文件名(String 文件名) {
        this.文件名 = 文件名;
    }

    public String get文件类型() {
        return 文件类型;
    }

    public void set文件类型(String 文件类型) {
        this.文件类型 = 文件类型;
    }

    public byte[] get文件内容() {
        return 文件内容;
    }

    public void set文件内容(byte[] 文件内容) {
        this.文件内容 = 文件内容;
    }

    public int get账号() {
        return 账号;
    }

    public void set账号(int 账号) {
        this.账号 = 账号;
    }

    public int get所属随笔() {
        return 所属随笔;
    }

    public void set所属随笔(int 所属随笔) {
        this.所属随笔 = 所属随笔;
    }
}
