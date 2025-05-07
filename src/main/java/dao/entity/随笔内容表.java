// 本来想设计链表结构，但一想16MB，百万小说都能存下了，再加链表有点太抽象了。
package dao.entity;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class 随笔内容表 implements Serializable {
    // 随笔索引号 随笔内容  附加值 账号
    // '随笔索引号', '随笔内容', '附加值', '账号'
    // 特殊处理，删除延续号不暴露出来
    private static final long serialVersionUID = 1L;
    private int 随笔索引号;
    private String 随笔内容;
    private String 附加值;
    private int 账号;

    public 随笔内容表(ResultSet rs) throws SQLException {//用rs直接构造
        this.随笔索引号 = rs.getInt("随笔索引号");
        this.随笔内容 = rs.getString("随笔内容");
        this.附加值 = rs.getString("附加值");
        this.账号 = rs.getInt("账号");
    }
    // 这些构造函数和方法没有标注的都是用编译器自带的"右键->生成"，自动生成的，
    // 要改的话，修改上面的部分，然后再自动生成即可


    public 随笔内容表(int 随笔索引号, String 随笔内容, String 附加值, int 账号) {
        this.随笔索引号 = 随笔索引号;
        this.随笔内容 = 随笔内容;
        this.附加值 = 附加值;
        this.账号 = 账号;
    }

    public int get随笔索引号() {
        return 随笔索引号;
    }

    public void set随笔索引号(int 随笔索引号) {
        this.随笔索引号 = 随笔索引号;
    }

    public String get随笔内容() {
        return 随笔内容;
    }

    public void set随笔内容(String 随笔内容) {
        this.随笔内容 = 随笔内容;
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
