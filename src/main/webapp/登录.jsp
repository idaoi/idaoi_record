<%--
  Created by IntelliJ IDEA.
  User: 86135
  Date: 2025/1/25
  Time: 22:05
  To change this template use File | Settings | File Templates.
--%>
<!--此文件是登录界面-->
<!--谷歌人机验证：https://cloud.google.com/security/products/recaptcha#business-case-->
<!--极验用的行为验证4.0：https://console.geetest.com/sensbot/management-->

<!--人机验证国内使用谷歌镜像站-->
<!-- <script src="https://www.google.com/recaptcha/enterprise.js" async defer></script> -->
<!-- <script src="https://www.recaptcha.net/recaptcha/api.js" async defer></script> -->

<!--登录功能约耗时7天。1/25~2/2，除去两天走亲戚。-->
<!--写完这个时尚未用前端框架，准备实现内部功能时，从自己实现markdown转变为使用现有富文本编辑器库，
发现wangEditor 5 有用于 Vue React demo，考虑之后使用框架。毕竟我没学过前端。
-->

<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html><!--设置文档类型标记为html-->
<html>
<head>
    <meta charset="UTF-8">
    <title>登录_i道i记录</title><!--标签名-->
    <link rel="stylesheet" type="text/css" href="登录界面样式.css">
    <%--    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>--%>
    <%--    <script src="https://cdn.jsdelivr.net/npm/jsencrypt@3.0.0-rc.1/bin/jsencrypt.min.js"></script><!--RSA 加密和解密-->--%>
    <%--    <script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/4.1.1/crypto-js.min.js"></script><!--这个用于哈希-->--%>
    <%--    <script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/4.1.1/aes.min.js"></script>--%>
    <%--    <script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/4.1.1/sha256.min.js"></script>--%>

    <script src="js/jquery-3.6.0.min.js"></script>
    <script src="js/jsencrypt.min.js"></script><!--RSA 加密和解密-->
    <script src="Encryption_Decryption.js"></script><!--AES 加密和解密调用的函数-->
    <script src="js/crypto-js.min.js"></script><!--这个用于哈希-->

    <script src="js/gt4.js"><!-- 极验 --></script>
    <!--人机验证和窗口打开-->
    <script type="text/javascript">
        const renderRecaptcha_sitekey = null;// 谷歌验证用户端密钥
        const geeTest_ID = null;// 极验的业务ID

        //  谷歌人机验证部分
        function onloadCallback_recaptcha() {// 打开人机验证
            grecaptcha.enterprise.render('recaptcha_div', {
                'sitekey': renderRecaptcha_sitekey,  // 人机验证用户端密钥
                'theme': 'dark',// 深色显示
                'callback': function (token) {// 验证成功回调
                    let date = {
                        verify_type: "recaptchaToken",    // 验证类型，传到服务器后根据类型选择处理函数
                        recaptchaToken: token,//令牌
                    };
                    if (type_open === 1) {
                        date.username = document.getElementById("username").value;
                    }
                    get_Ajax_Token(date)// 发送请求
                },
            });
        }


        // 极验人机验证部分
        initGeetest4({//调用初始化函数进行初始化,极验的初始化，
            captchaId: geeTest_ID,
        }, function (captcha) {
            // captcha为验证码实例
            captcha.appendTo("#geeTest_div")// 调用appendTo将验证码插入到页的元素中
            captcha.onSuccess(function () {//获取用户进行成功验证(onSuccess)所得到的结果，该结果用于进行服务端 SDK 进行二次验证。
                let result = captcha.getValidate();
                result.verify_type = "geeTestToken"
                if (type_open === 1) {
                    result.username = document.getElementById("username").value;
                }
                get_Ajax_Token(result)// 发送请求
            })
            $('#select_button_GeeTest').click(function () {// 这里绑定了“select_button_GeeTest” 的回调
                // 关闭选择，打开极验，
                close_popup("select_popup")
                open_popup("geeTest_popup")
                captcha.reset()//极验重置
            })

        });

        //<!-- popup_big 最大的窗口-->
        //<!-- select_popup 人机验证选择窗口 -->
        //<!-- recaptcha_popup 谷歌人机验证窗口 -->
        //<!-- geeTest_popup 极验人机验证窗口 -->
        //<!-- register_popup 注册密码输入窗口 -->
        function open_popup(ur_id) {// 打开弹窗
            let popup_big_id = document.getElementById(ur_id);
            popup_big_id.style.display = "block";
        }

        function close_popup(ur_id) {//关闭弹窗
            let popup_big_id = document.getElementById(ur_id);
            popup_big_id.style.display = "none";
        }

        function close_all_popup() {// 关闭所有弹出窗口
            const all_uip = ["popup_big", "select_popup", "recaptcha_popup", "geeTest_popup", "register_popup"];
            for (let i = 0; i < all_uip.length; i++) {
                let popup_big_id = document.getElementById(all_uip[i]);
                popup_big_id.style.display = "none";
            }
            document.getElementById("register_password1").value = "";// 清空输入的密码。
            document.getElementById("register_password2").value = "";

        }

        function close_all_popup_register() {// 关闭所有弹出窗口,注册特供版本，不清空输入的密码
            const all_uip = ["popup_big", "select_popup", "recaptcha_popup", "geeTest_popup", "register_popup"];
            for (let i = 0; i < all_uip.length; i++) {
                let popup_big_id = document.getElementById(all_uip[i]);
                popup_big_id.style.display = "none";
            }
        }

        function open_select_popup() {   // 打开人机验证选择窗口
            open_popup("popup_big");
            open_popup("select_popup");
        }

        function open_recaptcha_popup() { // 打开谷歌人机验证
            if (renderRecaptcha_sitekey == null) {// 如果没有密钥，直接进去，设置这一个入口就行了，极验那边暂且不设
                let date = {
                    verify_type: "recaptchaToken",    // 验证类型，传到服务器后根据类型选择处理函数
                    recaptchaToken: null,//令牌
                };
                if (type_open === 1) {
                    date.username = document.getElementById("username").value;
                }
                get_Ajax_Token(date)// 发送请求
                return;
            }
            open_popup("popup_big");
            close_popup("select_popup");
            open_popup("recaptcha_popup");
            // 重启验证器
            grecaptcha.enterprise.reset();
        }

        function open_register_popup() {// 打开注册密码输入窗口
            open_popup("popup_big");
            open_popup("register_popup");
        }

        // function restart_geeTest() { //重启极验验证  绑定在极验初始化部分
        // }
    </script>
    <!--加密-->
    <script type="text/javascript">
        // 如果用的是http，不能防止JavaScript注入，需要设置https。
        // 第一次收到的服务器公钥是不可信的，考虑之后添加外验证来确保公钥可信。
        let server_publicKeyPem; // 服务器发来的加密公钥
        let client_publicKeyPem;// 客户端的公钥
        let client_privateKeyPem;// 客户端的私钥

        function get_clientKey() {// 获取客户端密钥
            const encrypt = new JSEncrypt({default_key_size: 2048});// 创建一个新的 JSEncrypt 实例
            encrypt.getKey();// 生成密钥对
            client_publicKeyPem = encrypt.getPublicKey();// 获取公钥和私钥
            client_privateKeyPem = new JSEncrypt();
            client_privateKeyPem.setPublicKey(encrypt.getPrivateKey());
        }

        function getCookieValue(name) {// 获取cookie中名为name变量的值
            // 获取所有cookie的字符串
            let cookies = document.cookie;
            // 创建一个数组来保存每个cookie对（通过分号和空格分隔）
            let cookieArray = cookies.split('; ');
            // 遍历数组中的每个cookie
            for (let i = 0; i < cookieArray.length; i++) {
                let cookiePair = cookieArray[i];
                // 查找等号的位置
                let eqPos = cookiePair.indexOf('=');
                // 提取cookie的名称和值
                let cookieName = cookiePair.substring(0, eqPos);
                let cookieValue = cookiePair.substring(eqPos + 1);
                // 如果cookie名称与要查找的名称匹配，则返回解码后的值
                if (cookieName === name) {
                    return decodeURIComponent(cookieValue); // 解码cookie值
                }
            }
            // 如果没有找到匹配的cookie，则返回null
            return null;
        }


        function get_PublicKey(key) {// 获取公钥
            server_publicKeyPem = new JSEncrypt();
            server_publicKeyPem.setPublicKey(key);
        }


        // 服务器令牌自带几分钟后过期，不用加时间
        function encryptString(str, server_Token) {// 加密密码
            // 想了想，服务端打算用个固定盐值，这里本来接收由服务端发来的盐值，后来弃用这一策略。
            let saltedString = str + "salt_record";
            // 使用CryptoJS进行SHA-256哈希，发到服务端后加盐
            // 添加使用一次性服务器令牌来防止重放攻击
            let hash = CryptoJS.SHA256(saltedString).toString(CryptoJS.enc.Hex) + server_Token;
            return server_publicKeyPem.encrypt(hash); // 返回 Base64 编码的加密数据
        }

        function encryptString_login(str, AES_str, server_Token) {// 登陆时用的加密密码，还会添加AES密钥
            // 想了想，服务端打算用个固定盐值，这里本来接收由服务端发来的盐值，后来弃用这一策略。
            let saltedString = str + "salt_record";
            // 使用CryptoJS进行SHA-256哈希，发到服务端后加盐
            // 添加使用一次性服务器令牌来防止重放攻击
            let hash = CryptoJS.SHA256(saltedString).toString(CryptoJS.enc.Hex) + server_Token + AES_str;
            return server_publicKeyPem.encrypt(hash); // 返回 Base64 编码的加密数据
        }

        // 检查username是否为9位数字（明符）
        function isValidUsername(username) {
            const usernameRegex = /^\d{9}$/; // 匹配9位数字的正则表达式
            return usernameRegex.test(username);
        }

        // 检查password是否为大于8位的字母数字串（暗文）
        function isValidPassword(password) {
            const passwordRegex = /^[a-zA-Z0-9]{8,16}$/; // 匹配8~16位字母数字的正则表达式
            return passwordRegex.test(password);
        }

    </script>
    <!--各种回调和ajax-->
    <script type="text/javascript">
        // 这里面的代码有精力的话建议分出来代码到其他文件中。以防止爬虫。js逆向。
        let type_open = 0;   // 记录是登录还是注册打开的人机验证（1登录，2注册）


        function logOnButtonClick() {//登录按钮回调
            // 先检查一遍格式,格式有误就不用比对了,然后看看cookie中有没有对应用户名，没就直接去验人机
            // 有名向服务器发送用户名，服务器根据用户名,Cookies判断是否需要人机验证
            // 如果不需要，用户会收到一个服务器令牌，2min内，该用户名不需要验证，然后发送数据。
            // 如果返回需要人机验证
            // 进行人机验证,转到人机验证函数处理（验证成功去发送登录）
            type_open = 1;
            //==========判断格式
            let username = document.getElementById("username").value;
            let password = document.getElementById("password").value;
            if (!isValidUsername(username)) {
                alert('账号格式错误\n账号应为9位数字');
                return;
            }
            if (!isValidPassword(password)) {
                alert('密码格式错误\n密码长度在8~16之间\n且只能包含字母数字');
                return;
            }
            // 判断最近几天，上一次登陆的用户是否为该用户
            if (getCookieValue("username") === username) {// 是了问问要不要验
                let date = {
                    verify_type: "username_Toke",    // 验证类型，传到服务器后根据类型选择处理函数
                    username: username,//用户名
                }
                get_Ajax_Token(date)// 发送请求
                return;
            }
            //验证
            open_select_popup()// 开启人机验证
        }

        function registerButtonClick() {//注册（入簿）按钮回调
            // 人机验证,之后弹出窗口输入密码，哈希后进行发送，发送完成后，服务器返回一个账号(明符)。
            // 有点反直觉的是先验证再输入密码可能会用户输密码时间过长令牌失效。
            // 当时是为了复用取巧，现已修改
            type_open = 2;
            // 注册提交，弹出一个对话框让你输密码，输入完成后进行验证，然后注册。
            open_register_popup()
        }

        function get_Ajax_Token(send_data) {// 发送ajax请求，向服务器获取令牌以及加密公钥。
            $.ajax({
                url: "/Verify_Get_Server_Token",
                async: false,// 不进行异步，不设计什么等待加载了。
                type: "POST",// post 提交
                data: JSON.stringify(send_data),
                success: function (response) {//请求成功的回调
                    let server_Token = response["Token"];
                    if (send_data["verify_type"] === "username_Toke") {//不验证判断
                        if (server_Token != null) {//不验证给了令牌
                            get_PublicKey(response["publicKey_RSA"])// 获取公钥
                            logOnSubmit(server_Token)// 登录提交
                        } else {// 没令牌，还需要验证
                            // 先删掉用户名cookie,防止重复向服务器验证
                            document.cookie = 'username =; expires=Thu, 01 Jan 1970 00:00:01 GMT; path=/';
                            open_select_popup()// 开启人机验证
                        }
                    } else if (send_data["verify_type"] === "recaptchaToken" ||
                        send_data["verify_type"] === "geeTestToken") {// 提交人机验证令牌
                        close_all_popup_register()// 关闭所有弹出窗口
                        if (type_open === 2) {
                            open_register_popup()// 打开注册窗口
                        }
                        if (server_Token != null) {//给了令牌
                            get_PublicKey(response["publicKey_RSA"])// 获取公钥
                            switch (type_open) {
                                case 1:
                                    logOnSubmit(server_Token);// 登录提交
                                    break;
                                case 2:
                                    registerSubmit(server_Token);//注册提交
                                    break;
                            }
                        } else {
                            alert('验证失败');
                        }
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {//请求失败的回调
                    alert('验证失败，出现错误');
                }
            });
        }

        function logOnSubmit(server_Token) {// 登录提交
            // 判断用户名和密码格式没问题，对密码哈希加密好了再发送
            // 验证结果令牌或者服务器提交的无需验证令牌一起发送给后端，判断令牌无误尝试登录

            // 获取表单元素
            let username = document.getElementById("username").value;
            let password = document.getElementById("password").value;
            if (!isValidUsername(username)) {
                alert('账号格式错误\n账号应为9位数字');
                return;
            }
            if (!isValidPassword(password)) {
                alert('密码格式错误\n密码长度在8~16之间\n且只能包含字母数字');
                return;
            }
            get_clientKey();
            // 去掉公钥的头部和尾部，只保留 Base64 内容
            let publicKeyBase64 = client_publicKeyPem
                .replace(/-----BEGIN (?:RSA )?PUBLIC KEY-----/g, '')
                .replace(/-----END (?:RSA )?PUBLIC KEY-----/g, '')
                .replace(/\s+/g, ''); // 移除所有空白字符（包括换行和空格）
            let AES_str = generate32BitRandomString();// 一次性AES密钥
            let AES_iv = generateIV(AES_str);
            let date = {
                verify_type: "logOnSubmit",    // 验证类型，传到服务器后根据类型选择处理函数
                username: username,//用户名
                password: encryptString_login(password, AES_str, server_Token),// 加密密码
                client_publicKey: AES_encrypt(publicKeyBase64, AES_str, AES_iv),
            }
            get_Ajax_Submit(date)// 开始登录
        }

        function registerSubmit(server_Token) {
            let password1 = document.getElementById("register_password1").value;
            let date = {
                verify_type: "registerSubmit",    // 验证类型，传到服务器后根据类型选择处理函数
                password: encryptString(password1, server_Token),// 加密密码
            }
            get_Ajax_Submit(date)// 开始注册
        }


        function register_2ButtonClick() {// 密码注册框回调
            let password1 = document.getElementById("register_password1").value;
            let password2 = document.getElementById("register_password2").value;
            if (!isValidPassword(password1)) {
                alert('密码格式错误\n密码长度在8~16之间\n且只能包含字母数字');
                return;
            }
            if (password1 === password2) {
                close_all_popup_register() // 关闭注册框
                open_select_popup()  // 让你进行一下验证
            } else {
                alert("两次密码不一致");
            }

        }

        function get_Ajax_Submit(send_data) {// 发送ajax请求，向服务器提交账号密码
            $.ajax({
                url: "/Log_In_And_Register",
                async: false,// 不进行异步，不设计什么等待加载了。
                type: "POST",// post 提交
                data: JSON.stringify(send_data),
                success: function (response) {//请求成功的回调
                    let state = response["state"];
                    if (send_data["verify_type"] === "logOnSubmit") {// 登录
                        // 请求状态，"OK"没问题，"Token error"令牌错误,
                        // "Account error"账号错误 "Password error"密码错误
                        switch (state) {
                            case "OK":
                                let secret_key = response["secret_key"];
                                //需要保留的数据有：
                                // let client_publicKeyPem;// 客户端的公钥
                                // let client_privateKeyPem;// 客户端的私钥
                                // 已经记录在cookie中的AES令牌 和 服务器发来的加密公钥
                                window.parent.server_publicKeyPem = server_publicKeyPem; // 服务器的公钥
                                window.parent.client_publicKeyPem = client_publicKeyPem;// 客户端的公钥
                                window.parent.client_privateKeyPem = client_privateKeyPem;// 客户端的私钥
                                window.parent.AES_Key = client_privateKeyPem.decrypt(secret_key);
                                window.parent.get_homepage();// 跳转到主页
                                break;
                            case "Token error":
                                alert("令牌错误，可能令牌已过期");
                                break;
                            case "Account error":
                                alert("账号错误，账号不存在");
                                break;
                            case "Password error":
                                alert("密码错误");
                                break;
                            default:
                                alert("其他错误");
                        }


                    } else if (send_data["verify_type"] === "registerSubmit") {// 注册
                        // 请求状态，"OK"没问题，"Token error"令牌错误,
                        switch (state) {
                            case "OK":
                                // 设置新账号密码
                                let inputElement = document.getElementById("username");
                                inputElement.value = response["username"];// 账号
                                inputElement = document.getElementById("password");
                                inputElement.value = document.getElementById("register_password1").value;
                                break;
                            case "Token error":
                                alert("令牌错误，可能令牌已过期");
                                break;
                            default:
                                alert("其他错误");
                        }
                        close_all_popup()// 关闭所有弹出窗口
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {//请求失败的回调
                    alert("请求获取失败")
                }
            });
        }
    </script>
</head>

<body>
<div class="big-div">
    <!--这个div以一个图片为背景。-->
    <div class="outer-div">
        <div class="left-space">
        </div>
        <!--右半边包含用户名，密码，登录，注册-->
        <div class="right-div">
            <form id="login-form" class="form_style" method="POST">
                <div class="form-group">
                    <!--很奇怪，浏览器自动填充会改变input的样式，禁止填充吧-->
                    <label for="username"></label><input type="text" id="username" name="record_username"
                                                         placeholder="明符" class="input_style"
                                                         maxlength="9" autocomplete="off"
                                                         required>
                </div>
                <div class="form-group">
                    <label for="password"></label><input type="password" id="password" name="password"
                                                         placeholder="暗文" class="input_style"
                                                         maxlength="16" required>
                </div>

                <button type="button" id="登录" class="button_style" onclick="logOnButtonClick()">登录</button>
                <button type="button" id="注册" class="button_style" onclick="registerButtonClick()">入簿</button>
            </form>
        </div>
    </div>
</div>

<!-- 弹出窗口 -->
<div id="popup_big" class="popup_big" onclick="">
    <!-- 右上角有个关闭按钮 -->
    <button id="close_popup_button" class="close_popup_button" onclick="close_all_popup()">X
    </button>
    <!--人机验证选择窗口-->
    <div id="select_popup" class="select_popup">
        <div id="select_" class="select_">
            <div id="select_button_Google" class="select_button1" onclick="open_recaptcha_popup()"></div>
            <div id="select_button_GeeTest" class="select_button2"></div><!--极验回调绑定的在初始化部分-->
        </div>
    </div>
    <!-- 谷歌人机验证窗口 -->
    <div id="recaptcha_popup" class="recaptcha_popup">
        <div id="recaptcha_div" class="recaptcha_div"></div>
        <!-- <script src="https://www.google.com/recaptcha/enterprise.js?onload=onloadCallback_recaptcha&render=explicit"
                    async defer>
            </script> -->
        <!--使用镜像站，快一点。-->
        <script src="https://www.recaptcha.net/recaptcha/enterprise.js?onload=onloadCallback_recaptcha&render=explicit"
                async defer>
        </script>
    </div>

    <!--极验人机验证窗口-->
    <div id="geeTest_popup" class=geeTest_popup>
        <div id="geeTest_div" class="geeTest_div"></div>
    </div>

    <!--注册密码输入窗口-->
    <div id="register_popup" class="register_popup">
        <div id="register_" class="register_">
            <label for="register_password1"></label><input type="password" id="register_password1" name="password"
                                                           placeholder="输入暗文" class="register_input" maxlength="16">
            <label for="register_password2"></label><input type="password" id="register_password2" name="password"
                                                           placeholder="重复暗文" class="register_input" maxlength="16">
            <button type="button" id="register_button" class="register_button" onclick="register_2ButtonClick()">注册
            </button>
            <button type="button" id="close_register_button" class="close_register_button" onclick="close_all_popup()">x
            </button>
        </div>
    </div>
</div>
</body>
<script>

    // 给几个输入框添加回车回调
    document.getElementById('username').addEventListener('keydown', function (event) {
        if (event.key === "Enter" || event.code === "Enter") {
            document.getElementById("password").focus();// 聚焦到密码框
        }
    });
    document.getElementById('password').addEventListener('keydown', function (event) {
        if (event.key === "Enter" || event.code === "Enter") {
            logOnButtonClick();// 触发登录
        }
    });
    document.getElementById('register_password1').addEventListener('keydown', function (event) {
        if (event.key === "Enter" || event.code === "Enter") {
            document.getElementById("register_password2").focus();// 聚焦到第二个输入框
        }
    });
    document.getElementById('register_password2').addEventListener('keydown', function (event) {
        if (event.key === "Enter" || event.code === "Enter") {
            register_2ButtonClick();// 触发注册
        }
    });
</script>
</html>
