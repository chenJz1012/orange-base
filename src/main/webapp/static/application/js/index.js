/**
 * Created by chenguojun on 8/10/16.
 */
;
(function ($, window, document, undefined) {
    $(document).ready(function () {
        initIndex();
        initMenu();
    });
    var requestMapping = {
        "/api/index": "index"
    };
    App.requestMapping = $.extend({}, App.requestMapping, requestMapping);

    App.index = {
        page: function (title) {
            App.content.empty();
            App.title(title);
            var content = $('<div class="panel-body" id="index_grid"></div>');
            App.content.append(content);
            App.index.initEvents();
        }
    };
    /**
     * 初始化事件
     */
    App.index.initEvents = function () {
        var formOpts = {
            id: "index_form",//表单id
            name: "index_form",//表单名
            method: "post",//表单method
            action: "",//表单action
            ajaxSubmit: true,//是否使用ajax提交表单
            labelInline: false,
            rowEleNum: 2,
            beforeSubmit: function () {
            },
            ajaxSuccess: function () {
            },
            submitText: "保存",//保存按钮的文本
            showReset: true,//是否显示重置按钮
            resetText: "重置",//重置按钮文本
            isValidate: true,//开启验证
            buttons: [{
                type: 'button',
                text: '关闭',
                handle: function () {
                    form.setValue("attachmentIds", 1);
                    form.setValue("roles", "1,2,3");
                    form.setValue("html", "aaaa");
                }
            }],
            buttonsAlign: "center",
            //表单元素
            items: [{
                type: 'text',//类型
                name: 'name1',//name
                id: 'name1',//id
                label: '角色名',//左边label
                cls: 'input-large',
                rule: {
                    required: true
                },
                message: {
                    required: "请输入角色名"
                }
            }, {
                type: 'file',
                id: 'templatePath',
                name: 'templatePath',
                label: '模板路径',
                isAjaxUpload: true,
                onSuccess: function (data) {
                    $("#templatePath").attr("value", data.attachmentUrl);
                },
                deleteHandle: function () {
                    $("#templatePath").attr("value", "");
                },
                rule: {
                    required: true
                },
                message: {
                    required: "请上传文件"
                }
            }, {
                type: 'image',
                id: 'image',
                name: 'image',
                label: '模板图片',
                isAjaxUpload: true,
                onSuccess: function (data) {
                    $("#image").attr("value", data.attachmentUrl);
                },
                deleteHandle: function () {
                    $("#image").attr("value", "");
                },
                rule: {
                    required: true
                },
                message: {
                    required: "请上传文件"
                }
            }, {
                type: 'tree',//类型
                name: 'roles',//name
                id: 'roles',//id
                label: '菜单',//左边label
                url: App.href + "/api/sys/function/treeNodes?orange_token=" + App.token,
                data: {},
                expandAll: true,
                autoParam: ["id", "name", "pId"],
                chkStyle: "checkbox",
                rule: {
                    required: true
                },
                message: {
                    required: "请选择至少一项菜单"
                }
            }, {
                type: 'datepicker',
                name: 'datepicker',
                id: 'datepicker',
                label: '时间选择器',
                span: '4',
                config: {
                    timePicker: false,
                    singleDatePicker: true,
                    locale: {
                        format: 'YYYY-MM-DD'
                    }
                },
                rule: {
                    required: true
                },
                message: {
                    required: "请选择日期"
                }
            }, {
                type: 'kindEditor',
                name: 'introduce',
                id: 'introduce',
                label: '影片介绍',
                height: "300px",
                rule: {
                    required: true
                },
                message: {
                    required: "影片介绍"
                }
            }, {
                type: 'files',
                id: 'attachmentIds',
                name: 'attachmentIds',
                limit: 1,
                label: '上传附件',
                rule: {
                    required: true
                },
                message: {
                    required: "上传附件"
                }
            }, {
                type: 'html',
                id: 'html',
                name: 'html',
                label: '自定义html',
                html: '<div class="row"><div class="col-md-12"><div class="content-box-header"><div class="panel-title"><a href="javascript:void(0);">New vs Returning Visitors</a></div></div> <div class="content-box-large box-with-header"><p class="content">Pellentesque luctus quam quis consequat vulputate. Sed sit amet diam ipsum. Praesent in</p> </div> </div> </div>',
                eventHandle: function (ele) {
                    ele.find("a").click(function (e) {
                        alert("我点击了自定义链接");
                        e.preventDefault();
                    });
                },
                loadHandle: function (ele, value) {
                    ele.find("p.content").html(value);
                }
            }]
        };
        var form = App.content.find("#index_grid").orangeForm(formOpts);
    };

    function initIndex() {
        var token = $.cookie('tc_t');
        if (token == undefined) {
            window.location.href = './login.html';
        }
        App.token = token;
    }

    function getSubMenu(menus, menuId) {
        var subMenus = [];
        $.each(menus, function (i, m) {
            if (m.parentId == menuId) {
                subMenus.push(m);
            }
        });
        return subMenus;
    }

    function getMenu(menus, menuId) {
        var subMenus = [];
        $.each(menus, function (i, m) {
            if (m.id == menuId) {
                subMenus.push(m);
            }
        });
        return subMenus;
    }

    function getTopMenu(menus) {
        var topMenus = [];
        $.each(menus, function (i, m) {
            if (m.parentId == 0) {
                topMenus.push(m);
            } else {
                var subMenus = getMenu(menus, m.parentId);
                if (subMenus.length == 0) {
                    topMenus.push(m);
                }
            }
        });
        return topMenus;
    }

    function recursionMenu(ele, menus, subMenus) {
        if (subMenus.length > 0) {
            ele += "<ul>";
            $.each(subMenus, function (i, m) {
                ele += ('<li data-level="sub">'
                + '<a data-url="' + m.action
                + '" data-title="' + m.functionName
                + '" href="javascript:void(0);"><i class="' + (m.icon == null ? "glyphicon glyphicon-list" : m.icon) + '"></i> '
                + m.functionName
                + '</a>');
                var sMenus = getSubMenu(menus, m.id);
                ele += '</li>';
            });
            ele += "</ul>";
        }
        return ele;
    }

    function initMenu() {
        $.ajax(
            {
                type: 'GET',
                url: "../api/sys/function/current",
                contentType: "application/json",
                dataType: "json",
                beforeSend: function (request) {
                    request.setRequestHeader("X-Auth-Token", App.token);
                },
                success: function (result) {
                    if (result.code === 200) {
                        var menus = result.data;
                        var topMenus = getTopMenu(menus);
                        $.each(topMenus, function (i, m) {
                            if (m.parentId == 0) {
                                var ele = '<li data-level="top">'
                                    + '<a data-url="' + m.action
                                    + '" data-title="' + m.functionName
                                    + '" href="javascript:void(0);"><i class="' + (m.icon == null ? "glyphicon glyphicon-list" : m.icon) + '"></i> '
                                    + m.functionName
                                    + '</a>';
                                var subMenus = getSubMenu(menus, m.id);
                                if (subMenus.length > 0) {
                                    ele = recursionMenu(ele, menus, subMenus);
                                }
                                ele += '</li>';
                                var li = $(ele);
                                li.find("li[data-level=sub]").parents("li[data-level=top]").addClass("submenu").find("a:eq(0)").append('<span class="caret pull-right"></span>');
                                $("div.sidebar > .nav").append(li);
                            }
                        });
                        App.menu();
                    } else if (result.code === 401) {
                        alert("token失效,请登录!");
                        window.location.href = './login.html';
                    }
                },
                error: function (err) {
                }
            }
        );
    }

})(jQuery, window, document);