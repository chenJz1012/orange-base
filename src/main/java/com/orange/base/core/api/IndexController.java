package com.orange.base.core.api;

import com.orange.base.tools.freemarker.FreeMarkerUtil;
import com.orange.base.common.utils.ResponseUtil;
import com.orange.base.security.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenguojun on 8/23/16.
 */
@Controller
@RequestMapping("/api")
public class IndexController {

    @Autowired
    private FreeMarkerUtil freeMarkerUtil;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    @ResponseBody
    public Object index() throws Exception {
        Map params = new HashMap<>();
        params.put("name", SecurityUtil.getCurrentUserName());
        String html = freeMarkerUtil.getStringFromTemplate("/", "index.ftl", params);
        return ResponseUtil.success(html);
    }

}
