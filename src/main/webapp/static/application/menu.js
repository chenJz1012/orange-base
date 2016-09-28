/**
 * Created by chenguojun on 9/28/16.
 */
;
(function ($, window, document, undefined) {
    App.menu = {
        "initVerticalMenu": initVerticalMenu,
        "initSideMenu": initSideMenu,
        "toggleMenu": toggleMenu
    };
    function toggleMenu() {
        var toggle = $.cookie('menu-toggle');
        if (toggle == undefined) {
            toggle = "v";
        }
        if (toggle == "v") {
            $.cookie('menu-toggle', "s", {expires: 7, path: '/'});
        } else {
            $.cookie('menu-toggle', "v", {expires: 7, path: '/'});
        }

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

    function secondMenu(ele, menus, subMenus) {
        if (subMenus.length > 0) {
            ele += "<ul class='nav nav-second-level collapse'>";
            $.each(subMenus, function (i, m) {
                ele += ('<li data-level="sub">'
                + '<a data-url="' + m.action
                + '" data-title="' + m.functionName
                + '" href="javascript:void(0);"><i class="' + (m.icon == null ? "glyphicon glyphicon-list" : m.icon) + '"></i> '
                + m.functionName
                + '</a>');
                var sMenus = getSubMenu(menus, m.id);
                ele = thirdMenu(ele, sMenus);
                ele += '</li>';
            });
            ele += "</ul>";
        }
        return ele;
    }

    function secondVerticalMenu(ele, menus, subMenus) {
        if (subMenus.length > 0) {
            ele += "<ul class='dropdown-menu animated flipInX'>";
            $.each(subMenus, function (i, m) {
                ele += ('<li data-level="sub">'
                + '<a data-url="' + m.action
                + '" data-title="' + m.functionName
                + '" href="javascript:void(0);"><i class="' + (m.icon == null ? "glyphicon glyphicon-list" : m.icon) + '"></i> '
                + m.functionName
                + '</a>');
                ele += '</li>';
            });
            ele += "</ul>";
        }
        return ele;
    }

    function thirdMenu(ele, subMenus) {
        if (subMenus.length > 0) {
            ele += "<ul class='nav nav-third-level collapse'>";
            $.each(subMenus, function (i, m) {
                ele += ('<li data-level="sub">'
                + '<a data-url="' + m.action
                + '" data-title="' + m.functionName
                + '" href="javascript:void(0);"><i class="' + (m.icon == null ? "glyphicon glyphicon-list" : m.icon) + '"></i> '
                + m.functionName
                + '</a>');
                ele += '</li>';
            });
            ele += "</ul>";
        }
        return ele;
    }

    function initSideMenu() {
        var ul = "#side-menu";
        $("ul[role=vertical]").remove();
        $(".page-wrapper").removeClass("side-page");
        $.ajax(
            {
                type: 'GET',
                url: App.href + "/api/index/current",
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
                                var ele =
                                    '<li data-level="top">'
                                    + '<a data-url="' + m.action
                                    + '" data-title="' + m.functionName
                                    + '" href="javascript:void(0);"><i class="' + (m.icon == null ? "glyphicon glyphicon-list" : m.icon) + '"></i> '
                                    + m.functionName
                                    + '</a>';
                                var subMenus = getSubMenu(menus, m.id);
                                if (subMenus.length > 0) {
                                    ele = secondMenu(ele, menus, subMenus);
                                }
                                ele += '</li>';
                                var li = $(ele);
                                li.find("li[data-level=sub]").parents("li[data-level=top]").addClass("submenu").find("a:eq(0)").append('<span class="caret pull-right"></span>');
                                $(ul).append(li);
                            }
                        });
                        $(ul).metisMenu();
                        $(ul).find("li[class!=submenu] > a")
                            .each(function () {
                                    var url = $(this).attr("data-url");
                                    var f = App.requestMapping[url];
                                    if (f != undefined) {
                                        $(this).on("click", function () {
                                            var title = $(this).attr("data-title");
                                            App[f].page(title);
                                            $(this).parent().parent().removeClass("collapse").addClass("in");
                                            window.history.pushState({}, 0, 'http://' + window.location.host + App.projectName + '/static/index.html#!' + url);
                                        });
                                    }
                                }
                            );
                        refreshHref();
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

    function initVerticalMenu() {
        var ul = "#vertical-menu";
        $("ul[role=side]").remove();
        $.ajax(
            {
                type: 'GET',
                url: App.href + "/api/index/current",
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
                                var subMenus = getSubMenu(menus, m.id);
                                var dropDown = "";
                                var toggle = "";
                                var cart = "";
                                if (subMenus.length > 0) {
                                    dropDown = 'class="dropdown"'
                                    toggle = 'class="dropdown-toggle" data-toggle="dropdown"';
                                    cart = '<span class="caret"></span>';
                                }
                                var ele =
                                    '<li ' + dropDown + ' data-level="top">'
                                    + '<a data-url="' + m.action
                                    + toggle + '" data-title="' + m.functionName
                                    + '" href="javascript:void(0);"> '
                                    + m.functionName
                                    + cart
                                    + '</a>';
                                if (subMenus.length > 0) {
                                    ele = secondVerticalMenu(ele, menus, subMenus);
                                }
                                ele += '</li>';
                                var li = $(ele);
                                $(ul).append(li);
                            }
                        });
                        $(ul).find("li[class!=dropdown] > a")
                            .each(function () {
                                    var url = $(this).attr("data-url");
                                    var f = App.requestMapping[url];
                                    if (f != undefined) {
                                        $(this).on("click", function () {
                                            var title = $(this).attr("data-title");
                                            App[f].page(title);
                                            $(this).parent().siblings("li").removeClass("active");
                                            $(this).parent().parent().parent().siblings("li").removeClass("active");
                                            $(this).parent().addClass("active");
                                            $(this).parent().parent().parent().addClass("active");
                                            window.history.pushState({}, 0, 'http://' + window.location.host + App.projectName + '/static/index.html#!' + url);
                                        });
                                    }
                                }
                            );
                        refreshHref();
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

    /**
     * 完成后执行
     */
    var refreshHref = function () {
        var location = window.location.href;
        var url = location.substring(location.lastIndexOf("#!") + 2);
        if (location.lastIndexOf("#!") > 0 && url != undefined && $.trim(url) != "") {
            $('a[data-url="' + url + '"]').trigger("click");
        } else {
            window.location.href = window.location.href + "#!/api/index";
            url = "/api/index";
            $('a[data-url="' + url + '"]').trigger("click");
        }

    }
})(jQuery, window, document);
