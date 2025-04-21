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
</head>

<body>
<div class="big_div">
    <div class="dark_color_div">
        <div class="up_div">

        </div>
        <div class="line_div"></div>
        <div class="down_div">
            <div id="catalogue_container" class="catalogue_div"></div><!--目录-->
            <button class="floating_button"></button>
            <div class="right_div">
                <div class="set_up_div">
                    <span class="button_span">编辑</span>
                    <span class="button_span">属性</span>
                </div>
                <div class="line_div"></div>
                <div class="content_div"><!--内容-->
                    <div id="editor-toolbar"></div><!--工具栏区域-->
                    <div style="flex-grow: 1;max-height: 90%;">
                        <div id="editor-text-area" style="max-height: 450px;"></div><!--文本内容区域-->
                    </div>
                </div>

            </div>
        </div>
    </div>
</div>
<script src="https://unpkg.com/@wangeditor/editor@latest/dist/index.js"></script>
<script>
    const E = window.wangEditor

    // 切换语言
    const LANG = location.href.indexOf('lang=en') > 0 ? 'en' : 'zh-CN'
    E.i18nChangeLanguage(LANG)

    // 标题 DOM 容器
    const headerContainer = document.getElementById('catalogue_container')
    headerContainer.addEventListener('mousedown', event => {
        if (event.target.tagName !== 'LI') return
        event.preventDefault()
        const id = event.target.id
        editor.scrollToElem(id) // 滚动到标题
    })

    window.editor = E.createEditor({// 创建编辑器
        selector: '#editor-text-area',
        html: '<p><br></p>',
        config: {
            MENU_CONF: {
                uploadImage: {
                    fieldName: 'your-fileName',
                }
            },
            onChange(editor) {
                const headers = editor.getElemsByTypePrefix('header')
                headerContainer.innerHTML = headers.map(header => {
                    const text = E.SlateNode.string(header)
                    const { id, type } = header
                    return `<li id="${id}" type="${type}">${text}</li>`
                }).join('')
            }
        }
    })
    // window.editor.disable(); // 设置为只读模式
    // window.editor.enable();// 设置为可编辑
    // 工具栏只读模式下隐藏
    window.toolbar = E.createToolbar({
        mode: 'simple',
        editor,
        selector: '#editor-toolbar',
        config: {}
    })
</script>
</body>
</html>