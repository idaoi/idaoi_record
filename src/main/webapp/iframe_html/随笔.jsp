<%--
  Created by IntelliJ IDEA.
  User: 86135
  Date: 2025/4/21
  Time: 21:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>随笔</title>
    <link rel="stylesheet" type="text/css" href="随笔界面样式.css">
    <link href="style.css" rel="stylesheet"><!--文本框的样式-->
    <script src="../js/jquery-3.6.0.min.js"></script>
    <script src="../js/jsencrypt.min.js"></script>
    <script src="../js/crypto-js.min.js"></script><!--这个用于哈希-->
    <script src="../Encryption_Decryption.js"></script><!--AES加密-->
    <script type="text/javascript">
        let now_Essays_Index;
        let Essays_password = null;
        let old_Essays_num = 0;

        function Initialization_now_data() {// 初始化当前页面
            now_Essays_Index = JSON.parse(JSON.stringify(window.parent.now_Essays_Index));// 深拷贝，防止修改产生问题。
            document.getElementById("essays_name").textContent = now_Essays_Index["随笔名"];
            if (now_Essays_Index["随笔号"] <= 0) {// 是新随笔
                get_Ajax_essays_content();// 进行加载，向服务器申请新随笔号
            } else {// 根据是否有密码进行处理
                if (now_Essays_Index["加密"] === null) {// 当前页面无加密
                    get_Ajax_essays_content();// 进行加载，直接获取数据
                } else {
                    change_password_loading("password");// 让用户输入密码
                }
            }
        }

        function close_password_div() {// 关闭密码div
            document.getElementById('password_div').style.flexGrow = "0";
            document.getElementById('down_div').style.flexGrow = "1";
        }

        function open_password_div() {// 打开密码div
            document.getElementById('password_div').style.flexGrow = "1";
            document.getElementById('down_div').style.flexGrow = "0";
        }

        function show_password_error() {// 显示密码错误
            let password_input = document.getElementById('password_input');
            password_input.value = "";
            password_input.placeholder = "密码错误";
        }

        function change_password_loading(str) {// 切换密码输入和加载界面
            if (str === "loading") {// 下载等待
                document.getElementById('download-progress-bar').value = 0;
                document.getElementById('download-percent').textContent = '0%';
                document.getElementById('password_input_div').style.display = "none";
                document.getElementById('loading_text_div').style.display = "block";
                document.getElementById('loading_text').textContent = "引云篆于地，启千机化锁"// 以和启哪个好？
            } else if (str === "password") {// 密码
                document.getElementById('password_input_div').style.display = "block";
                document.getElementById('loading_text_div').style.display = "none";
            } else if (str === "post_loading") {// 上传等待
                // 更新原生进度条
                document.getElementById('download-progress-bar').value = 0;
                document.getElementById('download-percent').textContent = '0%';
                document.getElementById('password_input_div').style.display = "none";
                document.getElementById('loading_text_div').style.display = "block";
                document.getElementById('loading_text').textContent = "锁一笺于陆，送一篆归云"
            } else if (str === "error_div") {// 发生错误
                document.getElementById('password_input_div').style.display = "none";
                document.getElementById('loading_text_div').style.display = "none";
                document.getElementById('error_div').style.display = "block";
            }
        }

        function change_readOnly_edit(str) {// 修改编辑器的只读和编辑模式
            if (str === "readOnly") {
                window.editor.disable(); // 设置为只读模式
                document.getElementById('editor-toolbar').style.display = "none";// 隐藏工具栏
            } else if (str === "edit") {
                window.editor.enable();// 设置为可编辑
                document.getElementById('editor-toolbar').style.display = "block";// 显示工具栏
            }
        }

        function get_Ajax_essays_content() {// 发送ajax请求，获取一篇文章的数据
            change_password_loading("loading");
            const now = new Date();
            now.setMinutes(now.getMinutes() + 2);// 增加2min作为失效时间
            const formattedTime = now.toISOString(); // ISO 8601 格式：YYYY-MM-DDTHH:mm:ss.sssZ
            const secret_key = window.parent.parent.server_publicKeyPem.encrypt(// 如果为空，则是undefined
                // 最多装245字节， Base64字符都占1字节,下面这个大小最大可能在120左右, 36+10+36+10+24 安全
                window.parent.parent.AES_Key + now_Essays_Index["随笔号"] + "|" + now_Essays_Index["加密内容"] + "|" +
                now_Essays_Index["标签"] + "|" + formattedTime);// 密钥
            let send_data = {// 要发送过去的内容
                verify_type: "Essays_Content",
                secret_key: secret_key,// 密钥+要查询的随笔号+加密内容+时间
                signature: signMessage(secret_key, window.parent.parent.client_privateKeyPem),//签名
            };
            $.ajax({
                url: "/get_userdata",
                async: true,// 进行异步
                type: "post",// post 提交
                timeout: 30000,// 超时事件30s
                data: JSON.stringify(send_data),
                xhrFields: {//显示下载进度
                    onprogress: function (e) {
                        if (e.lengthComputable) {
                            const percent = Math.round((e.loaded / e.total) * 100);
                            // 更新原生进度条
                            document.getElementById('download-progress-bar').value = percent;
                            // 更新百分比文本
                            document.getElementById('download-percent').textContent = percent + '%';
                        }
                    }
                },
                success: function (response) {//请求成功的回调
                    let state = response["state"];
                    switch (state) {
                        case "OK":// 一般获取内容
                            switch (response["data"]) {
                                case "old_essays":// 旧随笔内容
                                    // 解密内容
                                    now_Essays_Index = JSON.parse(AES_decrypt(response["essays_index"],
                                        window.parent.parent.AES_Key, generateIV(window.parent.parent.AES_Key)));
                                    // 用户自己设置的密钥加密
                                    if (now_Essays_Index["加密"] == null) {// 内容无加密
                                        document.getElementById("delete_password").style.display = "none";
                                        Essays_password = window.parent.default_AES_key;// 密钥为用户默认密钥
                                    } else {// 内容有加密
                                        Essays_password = document.getElementById('password_input').value;
                                        // 密钥为用户输入的密钥
                                    }
                                    editor.setHtml(AES_decrypt(response["essays_data"],
                                        Essays_password, generateIV(generateAESKey(Essays_password))));// 设置数据
                                    close_password_div();// 关闭密码输入界面
                                    change_readOnly_edit("readOnly");// 进入只读模式
                                    break;
                                case "new_essays":// 创建新随笔
                                    now_Essays_Index = JSON.parse(AES_decrypt(response["new_index"],
                                        window.parent.parent.AES_Key, generateIV(window.parent.parent.AES_Key)));
                                    document.getElementById("delete_password").style.display = "none";
                                    Essays_password = window.parent.default_AES_key;// 密钥为用户默认密钥
                                    // 添加所需数据
                                    window.parent.Essays_Index.push(now_Essays_Index);// 更新父窗口的数据信息
                                    window.parent.set_Comprehensive_List();// 设置按标签索引的字典。
                                    window.parent.create_Summary();// 创建可展开的列表
                                    close_password_div();// 关闭密码输入界面
                                    change_readOnly_edit("edit");// 进入编辑模式
                                    break;
                            }
                            old_Essays_num = window.editor.getText().length;// 初始随笔长度
                            // 设置编辑和保存时间的显示
                            document.getElementById("start_time").textContent = new Date(now_Essays_Index["第一次编辑时间"]).toLocaleString();
                            document.getElementById("finish_time").textContent = new Date(now_Essays_Index["最后编辑时间"]).toLocaleString();
                            const selectElement = document.getElementById('belonging_Label');// 设置随笔分组
                            const optionElement = document.createElement('option');
                            optionElement.value = "0";
                            optionElement.textContent = "未分组";
                            selectElement.appendChild(optionElement);
                            for (const label in window.parent.All_Label) {// 动态生成所有分组选项
                                const optionElement = document.createElement('option');
                                optionElement.value = window.parent.All_Label[label]["标签号"];
                                optionElement.textContent = window.parent.All_Label[label]["标签名"];
                                selectElement.appendChild(optionElement);
                            }
                            break;
                        case "Password_error":// 密码错误
                            show_password_error();
                            change_password_loading("password");
                            break;
                        case "other_error":// 其他错误
                            change_password_loading("error_div");
                            break;
                        default:
                            change_password_loading("error_div");
                    }
                },
                error: function (xhr, status, error) {  // 请求失败的回调函数
                    change_password_loading("error_div");
                },
            });
        }

        function get_Ajax_essays_content_Submit(password_AES) {// 发送ajax请求，提交一篇文章的数据
            // 因为服务器并不记录用户密钥，所以无法进行增量更新，
            open_password_div()
            change_password_loading("post_loading");
            // 传入的参数，和Essays_password相同就是正常保存，不同是修改密码，为空是删除加密
            if (password_AES !== Essays_password) {// 非正常保存
                if (password_AES == null) {// 删除密码，
                    now_Essays_Index["加密"] = null;
                    password_AES = window.parent.default_AES_key;// 使用默认密钥
                    Essays_password = window.parent.default_AES_key;
                } else {// 修改密码
                    now_Essays_Index["加密"] = AES_encrypt(now_Essays_Index["加密内容"],
                        password_AES, generateIV(generateAESKey(password_AES)));// 设置下次解密的内容
                    Essays_password = password_AES;
                }
            }
            const essays_data = AES_encrypt(window.editor.getHtml(),// 这个内容就不用二次加密了，因为密钥只有用户知道。
                password_AES, generateIV(generateAESKey(password_AES)));// 先加密内容，防止时间过长密钥失效
            const now = new Date();
            now.setMinutes(now.getMinutes() + 2);// 增加2min作为失效时间
            const formattedTime = now.toISOString(); // ISO 8601 格式：YYYY-MM-DDTHH:mm:ss.sssZ
            const secret_key = window.parent.parent.server_publicKeyPem.encrypt(// 如果为空，则是undefined
                // 最多装245字节， Base64字符都占1字节 随笔名如果输入4*32字节 直接有120，危险，单独存放，
                // 36+10+48+36+10+24+4=168 安全
                window.parent.parent.AES_Key + now_Essays_Index["随笔号"] + "|" +
                now_Essays_Index["加密"] + "|" + now_Essays_Index["加密内容"] + "|" + now_Essays_Index["标签"] + "|" +
                formattedTime);// 密钥
            let save_insertImage = editor.getElemsByType('image').map(item => item.src);
            const regex = /^\/(Img_Obtain|Video_Obtain)\/(\d+)(?:\|\d+|\|.+)?$/;
            // 过滤符合格式的字符
            let firstNumbers = []
            save_insertImage.forEach(str => {
                const match = str.match(regex);
                if (match) {
                    firstNumbers.push(match[2]); // 提取第一个数字部分
                }
            });
            let send_data = {// 要发送过去的内容
                verify_type: "post_Essays_Content",
                secret_key: secret_key,// 密钥+随笔号+随笔名+加密+加密内容+时间
                signature: signMessage(secret_key, window.parent.parent.client_privateKeyPem),//签名
                essays_data: essays_data,// 随笔文本
                essays_name: now_Essays_Index["随笔名"],// 太长放不到RSA加密中，放外面
                save_insertImage :firstNumbers.join('|'),
                essays_change_len: window.editor.getText().length - old_Essays_num + "",// 改变的随笔长度
            };
            $.ajax({
                url: "/post_userdata",
                async: true,// 进行异步
                type: "post",// post 提交
                timeout: 180000,// 超时事件180s
                data: JSON.stringify(send_data),
                upload: {//上传进度
                    onprogress: function (e) {
                        if (e.lengthComputable) {
                            const percent = Math.round((e.loaded / e.total) * 100);
                            // 更新原生进度条
                            document.getElementById('download-progress-bar').value = percent;
                            // 更新百分比文本
                            document.getElementById('download-percent').textContent = percent + '%';
                        }
                    }
                },
                success: function (response) {//请求成功的回调
                    let state = response["state"];
                    switch (state) {
                        case "OK":
                            for (const Index in window.parent.Essays_Index) {
                                if (window.parent.Essays_Index[Index]["随笔号"] === now_Essays_Index["随笔号"]) {
                                    window.parent.Essays_Index[Index]["随笔名"] = now_Essays_Index["随笔名"];
                                    window.parent.Essays_Index[Index]["标签"] = now_Essays_Index["标签"];
                                    window.parent.Essays_Index[Index]["加密"] = now_Essays_Index["加密"];
                                    now_Essays_Index["最后编辑时间"] = new Date().getTime();
                                    window.parent.Essays_Index[Index]["最后编辑时间"] = now_Essays_Index["最后编辑时间"];
                                    break;
                                }
                            }
                            window.parent.set_Comprehensive_List();// 设置按标签索引的字典。
                            window.parent.create_Summary();// 创建可展开的列表
                            // 下面如同再次初始化
                            close_password_div()
                            if (document.getElementById("attribute_div").style.flexGrow === "1") {
                                attribute_button_OnClick();// 如果当前打开属性，关闭。
                            }
                            // 更新密钥
                            if (now_Essays_Index["加密"] == null) {// 内容无加密
                                document.getElementById("delete_password").style.display = "none";
                            } else {// 内容有加密
                                document.getElementById("delete_password").style.display = "flex";
                                // 密钥为用户输入的密钥
                            }
                            old_Essays_num = window.editor.getText().length;// 初始随笔长度
                            document.getElementById("finish_time").textContent = new Date(now_Essays_Index["最后编辑时间"]).toLocaleString();
                            change_readOnly_edit("readOnly");// 进入只读模式
                            break;
                        default:
                            change_password_loading("error_div");
                    }
                },
                error: function (xhr, status, error) {  // 请求失败的回调函数
                    change_password_loading("error_div");
                },
            });
        }

        function get_Ajax_delete_essays_content() {// 发送ajax请求，删除随笔
            open_password_div()
            change_password_loading("post_loading");
            const now = new Date();
            now.setMinutes(now.getMinutes() + 2);// 增加2min作为失效时间
            const formattedTime = now.toISOString(); // ISO 8601 格式：YYYY-MM-DDTHH:mm:ss.sssZ
            const secret_key = window.parent.parent.server_publicKeyPem.encrypt(// 如果为空，则是undefined
                // 比第一个还小，安全
                window.parent.parent.AES_Key + now_Essays_Index["随笔号"] + "|" + now_Essays_Index["加密内容"] + "|" +
                formattedTime);// 密钥
            let send_data = {// 要发送过去的内容
                verify_type: "delete_Essays_Content",
                secret_key: secret_key,// 密钥+随笔号+加密内容+时间
                signature: signMessage(secret_key, window.parent.parent.client_privateKeyPem),//签名
                essays_change_len: -old_Essays_num + "",// 改变的随笔长度
            };
            $.ajax({
                url: "/post_userdata",
                async: true,// 进行异步
                type: "post",// post 提交
                timeout: 10000,// 超时事件10s
                data: JSON.stringify(send_data),
                upload: {//上传进度
                    onprogress: function (e) {
                        if (e.lengthComputable) {
                            const percent = Math.round((e.loaded / e.total) * 100);
                            // 更新原生进度条
                            document.getElementById('download-progress-bar').value = percent;
                            // 更新百分比文本
                            document.getElementById('download-percent').textContent = percent + '%';
                        }
                    }
                },
                success: function (response) {//请求成功的回调
                    let state = response["state"];
                    switch (state) {
                        case "OK":
                            for (const Index in window.parent.Essays_Index) {
                                if (window.parent.Essays_Index[Index]["随笔号"] === now_Essays_Index["随笔号"]) {
                                    window.parent.Essays_Index.splice(Index, 1);
                                    break;
                                }
                            }
                            window.parent.set_Comprehensive_List();// 设置按标签索引的字典。
                            window.parent.create_Summary();// 创建可展开的列表
                            window.parent.changeIframeSource("iframe_html/总览.jsp");
                            break;
                        default:
                            change_password_loading("error_div");
                    }
                },
                error: function (xhr, status, error) {  // 请求失败的回调函数
                    change_password_loading("error_div");
                },
            });
        }
    </script>
    <script type="text/javascript">
        function Switch_Catalogue() {// 开关目录栏
            const catalogue_div = document.getElementById('catalogue_container');
            if (catalogue_div.style.width !== "0px") {
                catalogue_div.style.width = "0px";
            } else {
                catalogue_div.style.width = "255px";
            }
        }

        function edit_button_OnClick() {// 编辑按钮的回调
            if (window.editor.isDisabled()) {// 判断是否只读
                change_readOnly_edit("edit");// 进入编辑模式
            } else {
                change_readOnly_edit("readOnly")// 进入只读模式
            }
        }

        function save_button_OnClick() { // 保存按钮的回调
            get_Ajax_essays_content_Submit(Essays_password);
        }

        function attribute_button_OnClick() { // 属性按钮的回调，可以用来设置、修改密码，修改随笔名，修改所在的标签。
            const attribute_div = document.getElementById("attribute_div");
            const editor_content_div = document.getElementById("editor_content_div");
            if (attribute_div.style.flexGrow === "1") {// 关闭属性
                document.getElementById("edit_button").disabled = false;
                attribute_div.style.flexGrow = "0";
                editor_content_div.style.flexGrow = "1";
            } else {
                change_readOnly_edit("readOnly")// 进入只读模式，防止误操作
                document.getElementById("edit_button").disabled = true;
                attribute_div.style.flexGrow = "1";
                editor_content_div.style.flexGrow = "0";
                document.getElementById('belonging_Label').value = now_Essays_Index["标签"];
                document.getElementById("change_essays_name").value = now_Essays_Index["随笔名"];
                document.getElementById("delete_password").value = "";
                document.getElementById("new_password_input").value = "";
            }
        }

        function set_essays_name() {// 设置随笔名回调
            now_Essays_Index["随笔名"] = document.getElementById("change_essays_name").value;
            document.getElementById("essays_name").textContent = now_Essays_Index["随笔名"];
        }

        function set_essays_label() {// 设置随笔标签回调
            now_Essays_Index["标签"] = Number(document.getElementById("belonging_Label").value);
        }

        function delete_Essays_Content() {// 设置删除随笔回调
            if (document.getElementById("delete_Slider").value === "100") {
                get_Ajax_delete_essays_content();
            }
        }

        function set_Essays_password() { // 设置密码的回调
            if (document.getElementById("new_password_input").value !== "") {
                get_Ajax_essays_content_Submit(document.getElementById("new_password_input").value);
            }
        }

        function delete_Essays_password() {// 删除密码的回调
            if (document.getElementById("change_old_password_input").value === Essays_password) {
                get_Ajax_essays_content_Submit(null);
            }
        }
    </script>
</head>

<body>
<div class="big_div">
    <div class="dark_color_div">
        <div class="up_div" id="essays_name">

        </div>
        <div class="line_div"></div>
        <!--密码输入页面-->
        <div class="password_div" id="password_div">
            <div class=password_input_div>
                <div style="display: none" id=password_input_div><!--输入随笔密码-->
                    <div class="password_pattern" id="password_input_img">🔒</div>
                    <label for="password_input" id="password_input_label"></label>
                    <input type="password" id="password_input" name="password" placeholder="请输入该随笔密码"
                           class="password_text_input" maxlength="16">
                </div>
                <div id="loading_text_div"><!--正在加载提示-->
                    <div class="spinner" id="loading_spinner"></div>
                    <div class="password_text_label" id="loading_text">引云篆于地，启千机化锁</div>
                    <progress id="download-progress-bar" value="0" max="100"></progress>
                    <span id="download-percent">0%</span>
                </div>
                <div id="error_div" style="display: none"><!--发生错误提示-->
                    <div class="password_pattern">⁉</div>
                    <div class="password_text_label">发生错误，请尝试重新加载</div>
                </div>
            </div>
        </div>
        <div class="down_div" id="down_div">
            <!--开关目录栏的按钮-->
            <button class="floating_button" id="catalogue_button" onclick="Switch_Catalogue()"></button>
            <div id="catalogue_container" class="catalogue_div" style="width:255px;"><!--目录-->
                <div id="catalogue_data" class="catalogue_data_div"></div>
            </div>
            <div class="right_div"><!--右半边内容-->
                <div class="set_up_div">
                    <button class="button_span" id="edit_button" onclick="edit_button_OnClick()">编辑</button>
                    <button class="button_span" id="save_button" onclick="save_button_OnClick()">保存</button>
                    <button class="button_span" id="attribute_button" onclick="attribute_button_OnClick()">属性</button>
                    <span class="time_span" id="start_time"></span>
                    <span class="time_span" id="finish_time"></span>
                </div>
                <div class="line_div"></div><!--分割线-->
                <div class="content_div" id="attribute_div" style="flex-grow: 0"><!--属性页面，需求：设置、修改密码，修改随笔名，修改所在的标签。-->

                    <fieldset>
                        <legend>随笔基础属性</legend>
                        <div>
                            <label for="change_essays_name" id="change_essays_name_label">
                                <input id="change_essays_name" style="width: 300px;"
                                       placeholder="请输入随笔名"
                                       class="password_text_input" maxlength="32">
                            </label>
                            <button onclick="set_essays_name()">设置随笔名</button>
                        </div>

                        <div>
                            <label for="belonging_Label" style="width: 300px;justify-content: center;display: flex;">
                                所属标签:
                            </label>
                            <select class="dark_select" id="belonging_Label" onchange="set_essays_label()">
                            </select>
                        </div>
                        <div>
                            <label style="width: 300px;">
                                滑至右边确认删除
                                <input type="range" id="delete_Slider" min="0" max="100" value="0">
                            </label>
                            <button onclick="delete_Essays_Content()">删除随笔</button>
                        </div>
                        <label style="margin: auto">随笔基础属性需要点击工具栏的保存才能生效，删除随笔即刻生效。</label>
                    </fieldset>

                    <fieldset>
                        <legend>密码</legend>
                        <fieldset id="set_password">
                            <legend>设置密码</legend>
                            <div>
                                <label for="new_password_input" id="new_password_input_label">
                                    <input type="password" id="new_password_input" name="password" style="width: 300px;"
                                           placeholder="请输入新随笔密码" class="password_text_input" maxlength="16">
                                </label>
                                <button onclick="set_Essays_password()">设置密码</button>
                            </div>
                        </fieldset>
                        <fieldset id="delete_password">
                            <legend>删除密码</legend>
                            <div>
                                <label for="change_old_password_input" id="change_old_password_input_label">
                                    <input type="password" id="change_old_password_input" name="password"
                                           style="width: 300px;"
                                           placeholder="请输入该随笔密码" class="password_text_input" maxlength="16">
                                </label>
                                <button onclick="delete_Essays_password()">删除密码</button>
                            </div>
                        </fieldset>
                        <label style="margin: auto">修改随笔密码会即刻重新上传加密随笔，服务器不会记录您的密码。</label>
                    </fieldset>

                </div>
                <div class="content_div" id="editor_content_div" style="flex-grow: 1;height: 0;"><!--内容-->
                    <div id="editor-toolbar"></div><!--工具栏区域-->
                    <div id="editor-text-area" class="editor-text-area" style="flex-grow: 1;height: 0;"></div>
                    <!--文本内容区域-->
                    <div class="set_up_div" style="background-color: rgb(22, 22, 22);">
                        <span>
                            <span>已输入字符:</span>
                            <span id="text_len">0</span>
                        </span>
                        <span>
                            <span>已生成字符:</span>
                            <span id="html_len">0</span>
                            <span>/4194304————超限可能会保存失败</span>
                        </span>
                    </div>
                </div>

            </div>
        </div>
    </div>
</div>
<%--<script src="https://unpkg.com/@wangeditor/editor@latest/dist/index.js"></script>--%>
<script src="../js/index.js"></script>
<script>
    // 密码输入框回车回调
    document.getElementById('password_input').addEventListener('keydown', function (event) {
        if (event.key === "Enter" || event.code === "Enter") {
            // 用输入的密码解密密钥，如果成功，发送并验证
            let Password_entered = document.getElementById('password_input').value;
            now_Essays_Index["加密内容"] = AES_decrypt(now_Essays_Index["加密"], Password_entered, generateIV(generateAESKey(Password_entered)));
            if (now_Essays_Index["加密内容"] === "" || now_Essays_Index["加密内容"] === null) {// 为空，解密失败，切换为密码错误
                show_password_error();
            } else {// 去服务器问问对不对
                get_Ajax_essays_content();// 进行加载
            }
        }
    });
    // 随笔名不允许输入|
    document.getElementById('change_essays_name').addEventListener('keydown', function (e) {
        // 检查按下的键是否是 | 字符
        if (e.key === '|') {
            e.preventDefault(); // 阻止默认行为
        }
    });
    // 最大允许的字符数, 不做强制限制。
    const MAX_LENGTH = 4 * 1024 * 1024;//为减少计算，并且考虑到哪怕都是4字符，也能存4百万字，半部《凡人修仙传》
    const E = window.wangEditor

    // 切换语言
    const LANG = location.href.indexOf('lang=en') > 0 ? 'en' : 'zh-CN'
    E.i18nChangeLanguage(LANG)

    // 标题 DOM 容器
    const headerContainer = document.getElementById('catalogue_data')
    headerContainer.addEventListener('mousedown', event => {
        if (event.target.tagName !== 'LI') return
        event.preventDefault()
        const id = event.target.id.replace(/^li\+/, '')
        document.getElementById(id).scrollIntoView({behavior: 'smooth', block: 'start'});
        // editor.scrollToElem(id) // 滚动到标题
    })

    window.editor = E.createEditor({// 创建编辑器
        selector: '#editor-text-area',
        html: '<p><br></p>',
        config: {
            scroll: false, // 禁止编辑器滚动
            MENU_CONF: {
                uploadImage: { // 上传图片的配置
                    fieldName: "idaoi_record_uploaded-image",
                    server: "/Img_Handle",// 服务器的接口
                    // 单个文件的最大体积限制，默认为 2M
                    maxFileSize: 32 * 1024 * 1024, // 32M
                    base64LimitSize:0,
                    // 最多可上传几个文件，默认为 100
                    maxNumberOfFiles: 1,
                    // 超时时间，默认为 10 秒
                    timeout: 30 * 1000, // 30 秒
                    // 自定义插入图片
                    async customUpload(file, insertFn) {// 自定义上传
                        const maxFileSize = 32 * 1024 * 1024; // 32M
                        const fileReader = new FileReader();//读取文件内容
                        fileReader.onload = function (e) {
                            const fileContent = e.target.result;
                            // 因为url难以处理加密内容，所以舍弃加密
                            // const fileContentWordArray = CryptoJS.lib.WordArray.create(new Uint8Array(fileContent));
                            // // 用用户默认AES密钥加密文件内容
                            // const encryptedContent = AES_encrypt(fileContentWordArray,
                            //     window.parent.default_AES_key, generateIV(window.parent.default_AES_key)).toString(CryptoJS.enc.Base64);
                            // // 将加密后的内容转换为 Blob
                            // const encryptedBlob = new Blob([encryptedContent], {type: file.type});
                            // if (encryptedBlob.size > maxFileSize) {
                            //     console.log('文件超过32MB');
                            //     return;
                            // }
                            // const formData = new FormData();// 创建 FormData 并上传加密后的文件
                            // formData.append('file', encryptedBlob, file.name); // 保留原始文件名
                            const encryptedBlob = new Blob([fileContent], {type: file.type});
                            if (encryptedBlob.size > maxFileSize) {
                                console.log('文件超过32MB');
                                return;
                            }
                            const formData = new FormData();// 创建 FormData 并上传加密后的文件
                            formData.append('file', encryptedBlob, file.name); // 保留原始文件名
                            const now = new Date();
                            now.setMinutes(now.getMinutes() + 2);// 增加2min作为失效时间
                            const formattedTime = now.toISOString(); // ISO 8601 格式：YYYY-MM-DDTHH:mm:ss.sssZ
                            const secret_key = window.parent.parent.server_publicKeyPem.encrypt(// 如果为空，则是undefined
                                window.parent.parent.AES_Key + formattedTime);// 密钥
                            formData.append('secret_key', secret_key); // 密钥
                            formData.append('signature', signMessage(secret_key, window.parent.parent.client_privateKeyPem)); // 签名
                            formData.append('belong_Essays', now_Essays_Index["随笔号"]); // 所属随笔
                            try {
                                $.ajax({
                                    url: '/Img_Handle',
                                    type: 'POST',
                                    data: formData,
                                    timeout: 30*1000,// 超时事件30s
                                    processData: false,
                                    contentType: false,
                                    cache: false,
                                    success: function (response) {//请求成功的回调
                                        // 上传成功后处理
                                        insertFn(response.url, response.alt || '', response.href || '');
                                    },
                                });
                            } catch (error) {
                                console.error('上传失败:', error);
                            }
                        };
                        fileReader.readAsArrayBuffer(file); // 读取文件内容为 ArrayBuffer
                    },
                },
                uploadVideo: {
                    fieldName: "idaoi_record_uploaded-video",
                    server: "/Video_Handle",// 服务器的接口
                    // 单个文件的最大体积限制，默认为 10M
                    maxFileSize: 120 * 1024 * 1024, // 120M
                    // 最多可上传几个文件，默认为 100
                    maxNumberOfFiles: 1,
                    // 超时时间，默认为 30 秒
                    timeout: 120 * 1000, // 120 秒
                    // 自定义插入视频
                    async customUpload(file, insertFn) {// 自定义上传
                        const maxFileSize = 120 * 1024 * 1024; // 120M
                        const fileReader = new FileReader();//读取文件内容
                        fileReader.onload = function (e) {
                            const fileContent = e.target.result;
                            const encryptedBlob = new Blob([fileContent], {type: file.type});
                            if (encryptedBlob.size > maxFileSize) {
                                console.log('文件超过32MB');
                                return;
                            }
                            const formData = new FormData();// 创建 FormData 并上传加密后的文件
                            formData.append('file', encryptedBlob, file.name); // 保留原始文件名
                            const now = new Date();
                            now.setMinutes(now.getMinutes() + 2);// 增加2min作为失效时间
                            const formattedTime = now.toISOString(); // ISO 8601 格式：YYYY-MM-DDTHH:mm:ss.sssZ
                            const secret_key = window.parent.parent.server_publicKeyPem.encrypt(// 如果为空，则是undefined
                                window.parent.parent.AES_Key + formattedTime);// 密钥
                            formData.append('secret_key', secret_key); // 密钥
                            formData.append('signature', signMessage(secret_key, window.parent.parent.client_privateKeyPem)); // 签名
                            formData.append('belong_Essays', now_Essays_Index["随笔号"]); // 所属随笔
                            try {
                                $.ajax({
                                    url: '/Video_Handle',
                                    type: 'POST',
                                    data: formData,
                                    timeout: 120*1000,// 超时事件120s
                                    processData: false,
                                    contentType: false,
                                    cache: false,
                                    success: function (response) {//请求成功的回调
                                        // 上传成功后处理
                                        insertFn(response.url, response.alt || '', response.href || '');
                                    },
                                });
                            } catch (error) {
                                console.error('上传失败:', error);
                            }
                        };
                        fileReader.readAsArrayBuffer(file); // 读取文件内容为 ArrayBuffer
                    },
                }
            },
            onChange(editor) {
                // 生成目录
                const headers = editor.getElemsByTypePrefix('header')
                headerContainer.innerHTML = headers.map(header => {
                    const text = E.SlateNode.string(header)
                    const {id, type} = header
                    return `<li id="li+${id}" type="${type}" class="">${text}</li>`
                }).join('')
                const editor_Html_data = editor.getHtml();
                document.getElementById('html_len').innerHTML = editor_Html_data.length
                const editor_Text_data = editor.getText();
                document.getElementById('text_len').innerHTML = editor_Text_data.length
            }
        }
    })

    //将 FileReader 转换为 Promise
    function readerPromise(reader) {
        return new Promise((resolve, reject) => {
            reader.onload = () => resolve(reader.result);
            reader.onerror = reject;
        });
    }

    window.editor.disable(); // 设置为只读模式
    // window.editor.enable();// 设置为可编辑
    // 工具栏只读模式下隐藏
    window.toolbar = E.createToolbar({
        mode: 'simple',// 简洁模式
        editor,
        selector: '#editor-toolbar',
        config: {
            // 一个视频有点大，不给插
            // insertKeys: {
            //     index: 23, // 插入的位置，基于当前的 toolbarKeys
            //     keys: ["insertVideo", "uploadVideo"]
            // },
        }
    });

    Initialization_now_data()
</script>
</body>
</html>