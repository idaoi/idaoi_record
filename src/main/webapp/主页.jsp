<%--
  Created by IntelliJ IDEA.
  User: 86135
  Date: 2025/2/26
  Time: 22:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html><!--设置文档类型标记为html-->
<html>
<head>
    <meta charset="UTF-8">
    <title>主页_i道i记录</title><!--标签名-->
    <%--    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>--%>
    <%--    <script src="https://cdn.jsdelivr.net/npm/jsencrypt@3.0.0-rc.1/bin/jsencrypt.min.js"></script>--%>
    <%--    <script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/4.1.1/crypto-js.min.js"></script><!--这个用于哈希-->--%>
    <script src="js/jquery-3.6.0.min.js"></script>
    <script src="js/jsencrypt.min.js"></script>
    <script src="js/crypto-js.min.js"></script><!--这个用于哈希-->

    <script src="Encryption_Decryption.js"></script><!--AES加密-->

    <link rel="stylesheet" type="text/css" href="主页界面样式.css">
    <script type="text/javascript">
        let default_AES_key;// 默认AES密钥
        let Essays_Index;// 所有随笔索引
        let All_Label;// 所有标签
        let Comprehensive_List;//以标签为键，索引列表为值的字典
        let now_Essays_Index;// 打开随笔页面时，显示的随笔索引
        let now_Search_Data;// 打开搜索页面时，显示的搜索数据
        function Initialization_interface() {
            // 初始化界面
            // 该页初始化时，再向服务器请求用户基础数据，服务器会发送该账号的部分用户信息表内容，
            // 然后开启心跳检测，执行每分钟一次的登录检测，服务器会自己判断是否有效。

            // 双方有个AES密钥可以用来加密，还有对方的RSA公钥可以验证对面的签名
            // 一般内容用AES密钥加密，敏感内容用RSA加密。并附带签名。
            const now = new Date();
            now.setMinutes(now.getMinutes() + 2);// 增加2min作为失效时间
            const formattedTime = now.toISOString(); // ISO 8601 格式：YYYY-MM-DDTHH:mm:ss.sssZ
            const secret_key = window.parent.server_publicKeyPem.encrypt(window.parent.AES_Key + formattedTime);// 密钥
            let date = {
                verify_type: "Initialization",
                secret_key: secret_key,// 密钥
                signature: signMessage(secret_key, window.parent.client_privateKeyPem),//签名
            }
            // 获取用户基础数据（默认AES密钥，各种基础值）
            get_Ajax_get_userdata(date);
            get_Ajax_heartbeat();// 执行心跳检测

            // 打开侧边栏
            sidebar_add_ButtonClick();
            sidebar_homepage_ButtonClick();// 进入总览界面
        }

        function startsWithStr(string, str) {
            // 检查字符串是否非空，然后比较第一个字符
            return string.length > 0 && string[0] === str;
        }

        function set_Comprehensive_List() {// 设置Comprehensive_List
            let asterisk_num = 0;// 星标数
            let recycle_num = 0;// 回收数
            Comprehensive_List = {"未分组": []};
            for (const label in All_Label) {// 生成每个大标签组
                Comprehensive_List[All_Label[label]["标签名"]] = [];
            }
            for (const index in Essays_Index) {// 所有索引按标签分类
                let not_match = true;
                if (startsWithStr(Essays_Index[index]["随笔名"], "♻")) {// 回收不放
                    recycle_num += 1;
                } else {
                    if (startsWithStr(Essays_Index[index]["随笔名"], "⭐")) {
                        asterisk_num += 1;
                    }
                    for (const label in All_Label) {
                        if (Essays_Index[index]["标签"] === All_Label[label]["标签名"]) {
                            Comprehensive_List[All_Label[label]["标签名"]].push(Essays_Index[index])
                            not_match = false;
                        }
                    }
                    if (not_match) {
                        Comprehensive_List["未分组"].push(Essays_Index[index])
                    }
                }
            }
            // asterisk_span recycle_span
            const asterisk_span = document.getElementById('asterisk_span');
            const recycle_span = document.getElementById('recycle_span');
            asterisk_span.textContent = asterisk_num;
            recycle_span.textContent = recycle_num;
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


        function encryptString_RSA(str) {// 加密密码
            const now = new Date();
            now.setMinutes(now.getMinutes() + 2);// 增加2min作为失效时间
            const formattedTime = now.toISOString(); // ISO 8601 格式：YYYY-MM-DDTHH:mm:ss.sssZ
            let saltedString = str + "salt_record";// 想了想，服务端打算用个固定盐值，这里本来接收由服务端发来的盐值，后来弃用这一策略。
            // 使用CryptoJS进行SHA-256哈希，发到服务端后加盐
            let hash = CryptoJS.SHA256(saltedString).toString(CryptoJS.enc.Hex) + window.parent.AES_Key + formattedTime;
            return window.parent.server_publicKeyPem.encrypt(hash); // 返回 Base64 编码的加密数据
        }

        function Change_Password_Submit() {// 修改密码提交
            // 获取表单元素
            let old_password = document.getElementById("Old_Password").value;
            let new_password = document.getElementById("New_Password_1").value;
            if (new_password !== document.getElementById("New_Password_2").value) {// 新密码输入不同
                open_alert("新密码不同，请重新输入");
                return;
            }
            if (!isValidPassword(old_password)) {
                open_alert('旧密码格式错误\n密码长度在8~16之间\n且只能包含字母数字');
                return;
            }
            if (!isValidPassword(new_password)) {
                open_alert('新密码格式错误\n密码长度在8~16之间\n且只能包含字母数字');
                return;
            }
            // 这里修改密码的验证比较少，可以多加点。
            let date = {
                verify_type: "Change_Password",    // 验证类型，传到服务器后根据类型选择处理函数
                old_password: encryptString_RSA(old_password),// 加密密码
                new_password: encryptString_RSA(new_password),// 加密密码
            };
            get_Ajax_LogSubmit(date);
        }

    </script>
    <script type="text/javascript">
        function get_Ajax_heartbeat() {// 发送ajax请求，心跳检测
            $.ajax({
                url: "/heartbeat_detection",
                async: true,// 进行异步
                type: "post",// post 提交
                timeout: 10000,// 超时事件10s
                success: function (response) {//请求成功的回调
                    let state = response["heartbeat_str"];
                    if (state === "OK") {// 1min后再次执行
                        setTimeout(get_Ajax_heartbeat, 60000); // 60000毫秒等于1分钟
                    } else {// 弹出一个窗口，表示失败，然后跳转到登录界面
                        alert(state);
                        window.parent.get_login();
                    }
                },
                error: function (xhr, status, error) {  // 请求失败的回调函数
                },
            });
        }

        function get_Ajax_get_userdata(send_data) {// 发送ajax请求，获取各种用户数据
            $.ajax({
                url: "/get_userdata",
                async: true,// 进行异步
                type: "post",// post 提交
                timeout: 10000,// 超时事件10s
                data: JSON.stringify(send_data),
                success: function (response) {//请求成功的回调
                    let state = response["state"];
                    if (send_data["verify_type"] === "Initialization") {// 初始化
                        if (state === "OK") {
                            // 发来默认AES密钥，所有随笔内容，所有标签，前两者AES加密。
                            default_AES_key = AES_decrypt(response["default_AES"], window.parent.AES_Key, generateIV(window.parent.AES_Key));
                            Essays_Index = JSON.parse(AES_decrypt(response["index"], window.parent.AES_Key, generateIV(window.parent.AES_Key)));
                            All_Label = JSON.parse(response["label"]);
                            set_Comprehensive_List();// 设置按标签索引的字典。
                            create_Summary();// 创建可展开的列表
                            close_loading();
                        } else {
                            alert("初始化失败");
                        }
                    }
                },
                error: function (xhr, status, error) {  // 请求失败的回调函数
                },
            });
        }

        function get_Ajax_LogSubmit(send_data) {// 退出和修改密码的提交
            // 没有多次试错禁止，考虑之后加入
            $.ajax({
                url: "/Log_In_And_Register",
                async: false,// 不异步
                type: "post",// post 提交
                timeout: 10000,// 超时事件10s
                data: JSON.stringify(send_data),
                success: function (response) {//请求成功的回调
                    if (send_data["verify_type"] === "Log_Out_Submit") {//退出
                        let state = response["state"];
                        if (state === "OK") {// 跳转到登录界面
                            window.parent.get_login();
                        } else {// 弹出一个窗口报错，然后回到登录界面
                            alert(state);
                            window.parent.get_login();
                        }
                    }
                    if (send_data["verify_type"] === "Change_Password") {//修改密码
                        let state = response["state"];
                        if (state === "OK") {// 弹出一个窗口，表示密码已修改，然后跳转到登录界面
                            alert("密码已修改，请重新登录");
                            window.parent.get_login();
                        } else if (state === "旧密码错误" || state === "新密码错误") {// 弹出一个窗口报错,然后让你关掉后重新输入
                            open_alert(state);
                        } else {  // 剩下的可能就是错误登录了，报错后退出
                            alert("错误登录，请重新登录");
                            window.parent.get_login();
                        }
                    }
                },
                error: function (xhr, status, error) {  // 请求失败的回调函数
                },
            });
        }
    </script>
    <script type="text/javascript">
        let now_open_sidebar = 0 // 当前打开的侧边栏，0无，1设置2添加
        let currentMatchIndex = -1;// 侧边栏搜索功能显示的是哪个索引
        let matches = [];// 侧边栏搜索功能匹配的索引列表
        let last_Match_Str = ""// 侧边栏搜索功能上次的匹配字符串


        function sidebar_homepage_ButtonClick() {
            // 侧边栏_头像——返回主页的按钮回调
            changeIframeSource("iframe_html/总览.jsp")
        }

        function sidebar_setup_ButtonClick() {
            // 侧边栏_设置的按钮回调
            // 若没有打开侧边栏，给个动画，打开侧边栏，
            // 若打开其他侧边栏，关闭动画，打开侧边栏，
            // 若已打开本侧边栏，给个动画，关闭侧边栏，
            switch (now_open_sidebar) {
                case 0:
                    now_open_sidebar = 1;
                    document.getElementById("expandable_sidebar_setup").style.transition = "0.5s";
                    document.getElementById("expandable_sidebar_setup").style.width = "300px";
                    document.getElementById("sidebar_setup").className = "button_sidebar_bg";
                    break;
                case 2:
                    now_open_sidebar = 1;
                    document.getElementById("expandable_sidebar_setup").style.transition = "none";
                    document.getElementById("expandable_sidebar_add").style.transition = "none";
                    document.getElementById("expandable_sidebar_add").style.width = "0";
                    document.getElementById("expandable_sidebar_setup").style.width = "300px";

                    document.getElementById("sidebar_setup").className = "button_sidebar_bg";
                    document.getElementById("sidebar_add").className = "button_sidebar";
                    break;
                case 1:
                    now_open_sidebar = 0;
                    document.getElementById("expandable_sidebar_setup").style.transition = "0.5s";
                    document.getElementById("expandable_sidebar_setup").style.width = "0";
                    document.getElementById("sidebar_setup").className = "button_sidebar";
                    break;
            }
        }

        function sidebar_add_ButtonClick() {
            // 侧边栏_添加的按钮回调
            // 侧边栏_设置的按钮回调
            // 若没有打开侧边栏，给个动画，打开侧边栏，
            // 若打开其他侧边栏，关闭动画，打开侧边栏，
            // 若已打开本侧边栏，给个动画，关闭侧边栏，
            switch (now_open_sidebar) {
                case 0:
                    now_open_sidebar = 2;
                    document.getElementById("expandable_sidebar_add").style.transition = "0.5s";
                    document.getElementById("expandable_sidebar_add").style.width = "300px";
                    document.getElementById("sidebar_add").className = "button_sidebar_bg";
                    break;
                case 1:
                    now_open_sidebar = 2;
                    document.getElementById("expandable_sidebar_add").style.transition = "none";
                    document.getElementById("expandable_sidebar_setup").style.transition = "none";
                    document.getElementById("expandable_sidebar_setup").style.width = "0";
                    document.getElementById("expandable_sidebar_add").style.width = "300px";

                    document.getElementById("sidebar_add").className = "button_sidebar_bg";
                    document.getElementById("sidebar_setup").className = "button_sidebar";
                    break;
                case 2:
                    now_open_sidebar = 0;
                    document.getElementById("expandable_sidebar_add").style.transition = "0.5s";
                    document.getElementById("expandable_sidebar_add").style.width = "0";
                    document.getElementById("sidebar_add").className = "button_sidebar";
                    break;
            }
        }

        function create_Summary() {// 根据Comprehensive_List创建可折叠的列表，
            const display_list_div = document.getElementById('display_list_div');
            display_list_div.innerHTML = ''; // 清空所有子元素
            for (const label in Comprehensive_List) {// 遍历所有项目
                const details = document.createElement('details');
                const summary = document.createElement('summary');
                const span_txt = document.createElement("span");
                const span_add = document.createElement("span");
                span_txt.className = "summary_span";
                span_txt.textContent = label; // 设置 summary 的文本为标签名
                span_add.className = "summary_before_span";
                span_add.textContent = "+"; // 设置 summary 的文本为标签名
                span_add.addEventListener('click', function () {// 点击跳转到随笔
                    now_Essays_Index = {"随笔号": -1, "标签": label};// 创建一个新的随笔
                    changeIframeSource("iframe_html/随笔.jsp")// 跳转到随笔
                })
                summary.appendChild(span_txt);
                summary.appendChild(span_add);
                const ulElement = document.createElement('ul');// 一个大ul放随笔列表
                for (const data_key in Comprehensive_List[label]) {
                    const liElement = document.createElement('li');
                    liElement.textContent = Comprehensive_List[label][data_key]["随笔名"];
                    liElement.addEventListener('click', function () {// 点击跳转到随笔
                        now_Essays_Index = Comprehensive_List[label][data_key];
                        changeIframeSource("iframe_html/随笔.jsp")// 跳转到随笔
                    })
                    ulElement.appendChild(liElement);
                }
                details.appendChild(summary);
                details.appendChild(ulElement);
                display_list_div.appendChild(details);
            }
        }


        // 检查password是否为大于8位的字母数字串（暗文）
        function isValidPassword(password) {
            const passwordRegex = /^[a-zA-Z0-9]{8,16}$/; // 匹配8~16位字母数字的正则表达式
            return passwordRegex.test(password);
        }

        function open_popup(ur_id) {// 打开弹窗
            let popup_big_id = document.getElementById(ur_id);
            popup_big_id.style.display = "block";
        }

        function close_popup(ur_id) {//关闭弹窗
            let popup_big_id = document.getElementById(ur_id);
            popup_big_id.style.display = "none";
        }

        function change_password() {// 修改密码按钮的回调
            // 打开输入密码界面
            open_popup("popup_big");
            open_popup("Change_Password");
            // 清空问题
            document.getElementById("Old_Password").value = "";
            document.getElementById("New_Password_1").value = "";
            document.getElementById("New_Password_2").value = "";
        }


        function close_Change_Password() {// 关闭输入密码界面
            close_popup("popup_big");
            close_popup("Change_Password");

        }

        function close_alert() {// 关闭非阻断提示界面
            close_popup("popup_big_alert");
            close_popup("alert_popup");
        }

        function open_alert(str) {// 打开非阻断提示界面
            document.getElementById("alert_popup_text").textContent = str;
            open_popup("popup_big_alert");
            open_popup("alert_popup");
        }

        function open_loading() {// 打开加载界面
            open_popup("popup_big_alert");
            open_popup("loading_div");
        }

        function close_loading() {// 关闭加载界面
            close_popup("popup_big_alert");
            close_popup("loading_div");
        }


        function Log_Out() {// 退出登录的回调
            let date = {
                verify_type: "Log_Out_Submit",    // 验证类型，传到服务器后根据类型选择处理函数
            }
            get_Ajax_LogSubmit(date)// 退出
        }


        function scrollToMatch() {
            const lastMatch = matches[currentMatchIndex];
            currentMatchIndex += 1;// 入下一个索引
            if (currentMatchIndex >= matches.length) {// 越界了
                currentMatchIndex = 0;
            }
            const currentMatch = matches[currentMatchIndex];
            if (lastMatch && currentMatch) {
                lastMatch.element.classList.remove('High_light_li');// 清空上一次的高亮
                if (lastMatch.element.parentNode !== currentMatch.element.parentNode) {
                    lastMatch.element.parentNode.parentElement.open = false; // 取消展开所在部分
                }
            }
            if (currentMatch) {// 这一次存在
                currentMatch.element.classList.add('High_light_li');// 设置高亮
                currentMatch.element.parentNode.parentElement.open = true; // 展开所在部分
                currentMatch.element.scrollIntoView({behavior: 'smooth', block: 'start'});
            }

        }

        function search_data(str_data) {// 搜索数据回调
            if (str_data === "") {// 为空不匹配
                return;
            }
            // 上次和这次不相同，更新匹配列表
            if (str_data !== last_Match_Str) {
                if (matches[currentMatchIndex]) {// 清空上一次的高亮
                    matches[currentMatchIndex].element.classList.remove('High_light_li');
                    matches[currentMatchIndex].element.parentNode.parentElement.open = false; // 取消展开所在部分
                }
                last_Match_Str = str_data;
                // 获取 ID 为 display_list_div 的容器中所有的 <li> 元素
                const listItems = document.querySelectorAll('#display_list_div li');
                matches = []; // 重置匹配项
                currentMatchIndex = -1; // 重置索引
                listItems.forEach((item, index) => {// 匹配的放入索引列表
                    if (item.textContent.includes(str_data)) {
                        matches.push({element: item, index: index});
                    }
                });
                matches.sort((a, b) => a.index - b.index); // 按出现顺序排序
            }
            scrollToMatch();// 滚动
        }

        function changeIframeSource(newUrl) {// 修改iframe显示的页面
            let iframeElement = document.getElementById('iframe_html');
            iframeElement.src = newUrl;
        }
    </script>

</head>
<body>
<div class="sidebar">
    <!--侧边栏-->
    <div><!--头像，转向主页，这里为了省事直接输入的表情，表情不同的系统显示会有区别，建议换图标库-->
        <button type="button" class="button_sidebar" onclick="sidebar_homepage_ButtonClick()" id="sidebar_homepage">
            ☯
        </button>
    </div>
    <div class=sidebar_down>
        <!--设置，折叠栏转设置-->
        <button type="button" class="button_sidebar" onclick="sidebar_setup_ButtonClick()" id="sidebar_setup">⚙</button>
        <!--添加，折叠栏转添加-->
        <button type="button" class="button_sidebar" onclick="sidebar_add_ButtonClick()" id="sidebar_add">+</button>
    </div>
</div>
<div class="big_div">
    <div class="expandable_sidebar" id="expandable_sidebar_setup">
        <!--可折叠侧边栏（设置页）-->
        <button class="button_set_up" onclick="change_password()" style="margin-top: 30px;">修改密码</button>
        <button class="button_set_up" onclick="Log_Out()">退出登录</button>
    </div>
    <div class="expandable_sidebar" id="expandable_sidebar_add" style="display: flex;">
        <!--可折叠侧边栏（添加页）-->
        <div id="search_div" class="sidebar_add_div" style="height:35px;">
            <!--搜索框-->
            <label for="search_input"></label><input type="search" id="search_input" name="query"
                                                     placeholder="搜索" class="search_input"/>
        </div>
        <div id="display_list_div" class="sidebar_add_div"
             style="flex-grow:1;overflow:scroll;max-height:480px;margin-top: auto;margin-bottom: auto;">
            <!--项目列表  占据所有剩余空间 带滚动条-->
        </div>
        <div id="divider_div" class="sidebar_add_div" style="height:1.5px;background-color: rgb(55, 55, 55);">
            <!--分割线-->
        </div>
        <div id="other_list_div" class="sidebar_add_div" style="height:200px;">
            <!--其他列表  now_Search_Data-->
            <button type="button" class="other_list_Button" id="file_button">
                <span class="other_list_Button_left">📂</span>
                <span class="other_list_Button_centre">文件</span>
                <span class="other_list_Button_right" id="file_span">0</span>
            </button>
            <button type="button" class="other_list_Button" id="asterisk_button">
                <span class="other_list_Button_left">⭐</span>
                <span class="other_list_Button_centre">星标</span>
                <span class="other_list_Button_right" id="asterisk_span">0</span>
            </button>
            <button type="button" class="other_list_Button" id="recycle_button">
                <span class="other_list_Button_left">♻</span>
                <span class="other_list_Button_centre">回收</span>
                <span class="other_list_Button_right" id="recycle_span">0</span>
            </button>
        </div>
    </div>
    <iframe id="iframe_html" class="content_div">
        <!--右边的嵌套界面-->
    </iframe>
</div>


<div id="popup_big" class="popup_big">
    <!--修改密码页面 设计几乎和登录的注册弹窗相同-->
    <div id="Change_Password" class="popup_small">
        <div id="Change_Password_" class="Change_Password">
            <label for="Old_Password"></label><input type="password" id="Old_Password" name="password"
                                                     placeholder="旧暗文" class="password_input" maxlength="16">
            <label for="New_Password_1"></label><input type="password" id="New_Password_1" name="password"
                                                       placeholder="新暗文" class="password_input" maxlength="16">
            <label for="New_Password_2"></label><input type="password" id="New_Password_2" name="password"
                                                       placeholder="重复新暗文" class="password_input" maxlength="16">
            <button type="button" id="change_button" class="change_button" onclick="Change_Password_Submit()">修改
            </button>
            <button type="button" id="close_popup_button" class="close_popup_button"
                    onclick="close_Change_Password()">x
            </button>
        </div>
    </div>
</div>
<div id="popup_big_alert" class="popup_big">
    <!--用于非阻断的提示弹窗-->
    <!--因为alert弹窗会阻断函数执行导致心跳检测函数停止运行致使服务器判断离线，所以添加非阻断提示弹窗。-->
    <div id="alert_popup" class="popup_small">
        <div id="alert_popup_" class="alert_popup">
            <button type="button" id="close_popup_button_alert" class="close_popup_button"
                    onclick="close_alert()">x
            </button>
            <div id="alert_popup_text" class="alert_popup_text">
            </div>
        </div>
    </div>
    <div id="loading_div" class="popup_small" style="height: 150px;width: 200px;"><!--正在加载界面-->
        <div class="spinner"></div>
        <div class="loading-text">道生初始，混沌开元</div>
    </div>
</div>
</body>
<script>
    // 给组件添加回调
    // 密码界面不增加回车回调，防止用户输入错误
    // 搜索框回车回调
    document.getElementById('search_input').addEventListener('keydown', function (event) {
        if (event.key === "Enter" || event.code === "Enter") {
            // 执行搜索
            search_data(document.getElementById("search_input").value);
        }
    });
    // 额外列表中的按钮回调
    // 文件
    document.getElementById('file_button').addEventListener('click', function () {
    });
    // 星标
    document.getElementById('asterisk_button').addEventListener('click', function () {
    });
    // 回收
    document.getElementById('recycle_button').addEventListener('click', function () {
    });
    open_loading();
    Initialization_interface()// 初始化
</script>
</html>
