/*
处理图片和视频
 */

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/Img_Handle", "/Video_Handle"})
public class Img_Video_Handle  extends HttpServlet {

    private static final long serialVersionUID = 1L;//序列化对象版本,确保发送方和接收方的序列化对象版本兼容

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
