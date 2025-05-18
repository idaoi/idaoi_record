package tool;

import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private static final Properties props = new Properties();

    static {
        try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            props.load(input);
        } catch (Exception e) {
            throw new RuntimeException("加载配置文件失败", e);
        }
    }

//    public static String getDbUrl() {
//        return props.getProperty("database.Url");
//    }

}