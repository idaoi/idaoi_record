/*
处理图片和视频
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import tool.UserHandleClass;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@WebServlet(urlPatterns = {"/Img_Handle", "/Img_Obtain/*", "/Video_Handle", "/Video_Obtain/*"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 5,  // 5MB 文件大小超过此值时会写入临时文件
        maxFileSize = 1024 * 1024 * 120,  // 120MB 单个文件的最大大小
        maxRequestSize = 1024 * 1024 * 150 // 150MB 整个请求的最大大小
) // 启用文件上传支持
public class Img_Video_Handle extends HttpServlet {

    private static final long serialVersionUID = 1L;//序列化对象版本,确保发送方和接收方的序列化对象版本兼容

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path.endsWith("/Img_Handle") || path.endsWith("/Video_Handle")) {
            //处理图片视频的提交
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, String> r_Data = new HashMap<>();
                UserHandleClass Data_Handle = new UserHandleClass(request);
                if (path.endsWith("/Img_Handle")) {
                    r_Data = Data_Handle.Data_Handle("/Img_Obtain/");
                } else if (path.endsWith("/Video_Handle")) {
                    r_Data = Data_Handle.Data_Handle("/Video_Obtain/");
                }
                Map<String, String> responseData;
                // 创建响应JSON对象
                if (Objects.equals(r_Data.get("state"), "OK")) {
                    responseData = r_Data;
                } else {
                    responseData = new HashMap<>();
                    responseData.put("state", r_Data.get("state"));
                    // 防止泄露信息
                }
                //添加字段
                responseData.put("message", "200");

                // 将响应JSON对象写入响应
                PrintWriter out = response.getWriter();
                // 转为json字符串发送
                String jsonResponse = objectMapper.writeValueAsString(responseData);
                // 设置 Content-Type 和 Content-Length
                response.setContentType("application/json");
                response.setContentLength(jsonResponse.getBytes(StandardCharsets.UTF_8).length);
                out.write(jsonResponse);
                out.flush();
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getRequestURI();
        Pattern pattern = Pattern.compile("^/(Img_Obtain|Video_Obtain)/(\\d+|\\d+\\|\\d+|.+)$");
        if (pattern.matcher(path).matches()) {
            //处理图片视频的请求
            try {
                // 从URL路径中提取文件ID
                String pathInfo = request.getPathInfo();
                String id_str = pathInfo.substring(1);   // 去掉开头的斜杠
                // 发现文件名若有中文没法正确识别url，取消使用用户发送来的文件名，改为生成随机字符串。
                String[] id_data = id_str.split("\\|");// 文件名不允许使用|，所以用这个符号分割
                ObjectMapper objectMapper = new ObjectMapper();
                UserHandleClass Data_Obtain = new UserHandleClass(request);
                Map<String, String> responseData = new HashMap<>();
                byte[] Obtain_data = Data_Obtain.Data_Obtain(Integer.parseInt(id_data[0]), Integer.parseInt(id_data[1]), id_data[2]);

                // 设置响应类型
                response.setContentType("application/octet-stream");

                // 设置 Content-Length
                response.setContentLength(Obtain_data.length);

                // 写入响应
                try (OutputStream os = response.getOutputStream()) {
                    os.write(Obtain_data);
                }
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
            }
        }
    }
}
