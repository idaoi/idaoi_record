// ��������ڴ������к�BLOB��ص�ת��������BLOB���ַ������ж�BLOB�Ƿ񳬹���С��
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
        // ��Blob�����ж�ȡ�ֽ����飬��λ��1��ʼ����ȡ����ΪblobLength���ֽ�
        byte[] byteArray = data.getBytes(1, blobLength);
        // ���ֽ�����ת��Ϊ�ַ���
        return new String(byteArray);
    }

    public static Blob String_Blob(String str) throws SQLException {
        // ���ַ���ת��Ϊ�ֽ�����
        byte[] byteArray = str.getBytes();
        return new SerialBlob(byteArray);
    }

    public static boolean Less_Than_Blob(Blob data, Blob_Type blob_type) {
        // blob ��С�Ƿ�С�����ݿ�����Ӧ���͵Ĵ�С
        try {
            long blobSize = data.length(); // ��ȡBlob����Ĵ�С
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
            // ����SQLException�쳣
            e.printStackTrace();
            return false;
        }
    }
}
