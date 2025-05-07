package dao.entity;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class 随笔索引表 implements Serializable {
    //随笔号 随笔名 标签 第一次编辑时间 最后编辑时间  加密 加密内容 附加值 账号
    //'随笔号' ,'随笔名' ,'标签' ,'第一次编辑时间' ,'最后编辑时间' ,'加密','加密内容','附加值','账号'
    private static final long serialVersionUID = 1L;
    private int 随笔号;
    private String 随笔名;
    private Integer 标签;
    private Timestamp 第一次编辑时间;
    private Timestamp 最后编辑时间;
    private String 加密;
    private String 加密内容;
    private String 附加值;
    private int 账号;

    public 随笔索引表(ResultSet rs) throws SQLException {//用rs直接构造登录表项目
        this.随笔号 = rs.getInt("随笔号");
        this.随笔名 = rs.getString("随笔名");
        this.标签 = rs.getInt("标签");
        this.第一次编辑时间 = rs.getTimestamp("第一次编辑时间");
        this.最后编辑时间 = rs.getTimestamp("最后编辑时间");
        this.加密 = rs.getString("加密");
        try{
            this.加密内容 = rs.getString("加密内容");
        }catch (SQLException e){
            this.加密内容 = null;
        }
        this.附加值 = rs.getString("附加值");
        this.账号 = rs.getInt("账号");
    }

    // 这些构造函数和方法没有标注的都是用编译器自带的"右键->生成"，自动生成的，
    // 要改的话，修改上面的部分，然后再自动生成即可
    // toString 特殊处理，不返回加密内容

    @Override
    public String toString() {
        return "随笔索引表{" +
                "随笔号=" + 随笔号 +
                ", 随笔名='" + 随笔名 + '\'' +
                ", 标签=" + 标签 +
                ", 第一次编辑时间=" + 第一次编辑时间 +
                ", 最后编辑时间=" + 最后编辑时间 +
                ", 加密='" + 加密 + '\'' +
                ", 附加值='" + 附加值 + '\'' +
                ", 账号=" + 账号 +
                '}';
    }

    public 随笔索引表(int 随笔号, String 随笔名, Integer 标签, Timestamp 第一次编辑时间, Timestamp 最后编辑时间, String 加密, String 加密内容, String 附加值, int 账号) {
        this.随笔号 = 随笔号;
        this.随笔名 = 随笔名;
        this.标签 = 标签;
        this.第一次编辑时间 = 第一次编辑时间;
        this.最后编辑时间 = 最后编辑时间;
        this.加密 = 加密;
        this.加密内容 = 加密内容;
        this.附加值 = 附加值;
        this.账号 = 账号;
    }

    public int get随笔号() {
        return 随笔号;
    }

    public void set随笔号(int 随笔号) {
        this.随笔号 = 随笔号;
    }

    public String get随笔名() {
        return 随笔名;
    }

    public void set随笔名(String 随笔名) {
        this.随笔名 = 随笔名;
    }

    public Integer get标签() {
        return 标签;
    }

    public void set标签(Integer 标签) {
        this.标签 = 标签;
    }

    public Timestamp get第一次编辑时间() {
        return 第一次编辑时间;
    }

    public void set第一次编辑时间(Timestamp 第一次编辑时间) {
        this.第一次编辑时间 = 第一次编辑时间;
    }

    public Timestamp get最后编辑时间() {
        return 最后编辑时间;
    }

    public void set最后编辑时间(Timestamp 最后编辑时间) {
        this.最后编辑时间 = 最后编辑时间;
    }

    public String get加密() {
        return 加密;
    }

    public void set加密(String 加密) {
        this.加密 = 加密;
    }

    public String get加密内容() {
        return 加密内容;
    }

    public void set加密内容(String 加密内容) {
        this.加密内容 = 加密内容;
    }

    public String get附加值() {
        return 附加值;
    }

    public void set附加值(String 附加值) {
        this.附加值 = 附加值;
    }

    public int get账号() {
        return 账号;
    }

    public void set账号(int 账号) {
        this.账号 = 账号;
    }
}
