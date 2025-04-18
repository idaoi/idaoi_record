// 这个类用于处理所有和BLOB相关的转化，比如BLOB和字符串，判断BLOB是否超过大小。
package tool;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.SQLException;

public class ProcessingBlob {
    public enum Blob_Type {
        TINYBLOB, BLOB, MEDIUMBLOB, LONGBLOB;
    }

    private static final int TINYBLOB_MAX_SIZE = 255;//256
    private static final int BLOB_MAX_SIZE = 65535;//64KB
    private static final int MEDIUMBLOB_MAX_SIZE = 16777215;//16MB
    private static final int LONGBLOB_MAX_SIZE = 536870912; // 512MB

    public static String Blob_String(Blob data) throws SQLException {
        int blobLength = (int) data.length();
        // 从Blob对象中读取字节数组，从位置1开始，读取长度为blobLength的字节
        byte[] byteArray = data.getBytes(1, blobLength);
        // 将字节数组转换为字符串
        return new String(byteArray);
    }

    public static Blob String_Blob(String str) throws SQLException {
        // 将字符串转换为字节数组
        byte[] byteArray = str.getBytes();
        return new SerialBlob(byteArray);
    }

    public static boolean Less_Than_Blob(Blob data, Blob_Type blob_type) {
        // blob 大小是否小于数据库中相应类型的大小
        try {
            long blobSize = data.length(); // 获取Blob对象的大小
            switch (blob_type) {
                case TINYBLOB:
                    return blobSize > TINYBLOB_MAX_SIZE;
                case BLOB:
                    return blobSize > BLOB_MAX_SIZE;
                case MEDIUMBLOB:
                    return blobSize > MEDIUMBLOB_MAX_SIZE;
                case LONGBLOB:
                    return blobSize > LONGBLOB_MAX_SIZE;
                default:
                    return false;
            }

        } catch (SQLException e) {
            // 处理SQLException异常
            e.printStackTrace();
            return false;
        }
    }
}
