<%--
  Created by IntelliJ IDEA.
  User: 86135
  Date: 2025/4/19
  Time: 15:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>i道i记录</title>
    <script src="Encryption_Decryption.js"></script>
    <script src="js/crypto-js.min.js"></script><!--这个用于哈希-->
    <script type="text/javascript">
    </script>
    <style>
        html, body {
            height: 100%;
            margin: 0;
            min-width: 400px;
            min-height: 300px;
            padding: 0;
        }

        iframe {
            height: 100%;
            width: 100%;
            border: none; /* 移除 iframe 的边框 */
            margin: 0;
            padding: 0;
        }
    </style>
    <script type="text/javascript">
        var server_publicKeyPem;// 服务器的公钥
        var client_publicKeyPem;// 客户端的公钥
        var client_privateKeyPem;// 客户端的私钥 此为new JSEncrypt();
        var AES_Key;

        function changeIframeSource(newUrl) {// 修改iframe显示的页面
            let iframeElement = document.getElementById('iframe_html');
            iframeElement.src = newUrl;
        }

        function get_homepage() {
            changeIframeSource("主页.jsp");
            document.title = "主页_i道i_记录"
        }

        function get_login() {
            changeIframeSource("登录.jsp");
            document.title = "登录_i道i_记录"
        }
    </script>
</head>
<body>
<iframe id="iframe_html">
</iframe>
<script>
    get_login()
</script>
</body>
</html>
