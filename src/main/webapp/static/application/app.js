/**
 * Created by chenguojun on 8/10/16.
 */
;
(function ($, window, document, undefined) {
    window.App = {
        projectName: "/orange-base",
        href: "..",
        requestMapping: {}
    };

    /**
     * 下载文件
     * @param href
     */
    App.download = function (href) {
        var xhr = new XMLHttpRequest();
        xhr.open('GET', ".." + href, true);
        xhr.responseType = 'arraybuffer';
        xhr.onload = function () {
            if (this.status === 200) {
                var filename = "";
                var disposition = xhr.getResponseHeader('Content-Disposition');
                if (disposition && disposition.indexOf('attachment') !== -1) {
                    var filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
                    var matches = filenameRegex.exec(disposition);
                    if (matches != null && matches[1]) {
                        filename = matches[1].replace(/['"]/g, '');
                    }
                }
                var type = xhr.getResponseHeader('Content-Type');
                var blob = new Blob([this.response], {type: type});
                if (typeof window.navigator.msSaveBlob !== 'undefined') {
                    // IE workaround for "HTML7007: One or more blob URLs were revoked by closing
                    // the blob for which they were created. These URLs will no longer resolve as
                    // the data backing the URL has been freed."
                    window.navigator.msSaveBlob(blob, filename);
                } else {
                    var URL = window.URL || window.webkitURL;
                    var downloadUrl = URL.createObjectURL(blob);

                    if (filename) {
                        // use HTML5 a[download] attribute to specify filename
                        var a = document.createElement("a");
                        // safari doesn't support this yet
                        if (typeof a.download === 'undefined') {
                            window.location = downloadUrl;
                        } else {
                            a.href = downloadUrl;
                            a.download = filename;
                            document.body.appendChild(a);
                            a.click();
                        }
                    } else {
                        window.location = downloadUrl;
                    }

                    setTimeout(function () {
                        URL.revokeObjectURL(downloadUrl);
                    }, 100);
                }
            }
        };
        xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        xhr.setRequestHeader('X-Auth-Token', App.token);
        xhr.send();
    }

    App.scrollTo = function (el, offeset) {
        var pos = (el && el.size() > 0) ? el.offset().top : 0;

        if (el) {
            if ($("body").hasClass('page-header-fixed')) {
                pos = pos - $('.page-header').height();
            } else if ($('body').hasClass('page-header-top-fixed')) {
                pos = pos - $('.page-header-top').height();
            } else if ($('body').hasClass('page-header-menu-fixed')) {
                pos = pos - $('.page-header-menu').height();
            }
            pos = pos + (offeset ? offeset : -1 * el.height());
        }

        $('html,body').animate({
            scrollTop: pos
        }, 'slow');
    };

    /**
     * 更改右侧内容标题
     * @param title
     */
    App.title = function (title) {
        $("#main-title").text(title);
    };
    /**
     * 右侧内容方法集合
     * @type {{append: App.body.append, empty: App.body.empty}}
     */
    App.content = {
        append: function (ele) {
            $("#main-body").append(ele);
        },
        empty: function () {
            $("#main-body").empty();
        },
        find: function (ele) {
            return $("#main-body").find(ele);
        }
    };

    App.$content = function () {
        return $("#main-body");
    };

    App.menu = function () {
        $this = $("#side-menu");
        $this.find("li.submenu").children("a").on("click", function (e) {
            e.preventDefault();
            var $li = $(this).parent("li");
            var $ul = $(this).next("ul");
            if ($li.hasClass("open")) {
                $ul.slideUp(150);
                $li.removeClass("open");
            } else {
                if ($li.parent("ul").hasClass("nav")) {
                    $this.find("li > ul").slideUp(150);
                    $this.find("li").removeClass("open");
                }
                $ul.slideDown(150);
                $li.addClass("open");
            }
        });
        $this.find("li[class!=submenu]").children("a").on("click", function (e) {
            e.preventDefault();
            var $li = $(this).parent("li");
            if ($li.parent("ul").hasClass("nav")) {
                $this.find("li > ul").slideUp(150);
                $this.find("li").removeClass("open");
            }
            $this.find("li.current").removeClass("current");
            $(this).parents("li").addClass("current");
            $li.parent("ul").parent("li").addClass("open");
        });
        $this.find("li[class!=submenu] > a")
            .each(function () {
                    var url = $(this).attr("data-url");
                    var f = App.requestMapping[url];
                    if (f != undefined) {
                        $(this).on("click", function () {
                            var title = $(this).attr("data-title");
                            $(this).parent("li").parent("ul").show().parent("li").parent("ul").show();
                            App[f].page(title);
                            window.history.pushState({}, 0, 'http://' + window.location.host + App.projectName + '/static/index.html#!' + url);
                        });
                    }
                }
            );

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
