package com.orange.base.core.api;

import com.orange.base.common.utils.ResponseUtil;
import com.orange.base.common.utils.Result;
import com.orange.base.security.SecurityConstant;
import com.orange.base.security.exception.AuBzConstant;
import com.orange.base.security.exception.AuthBusinessException;
import com.orange.base.security.service.FunctionService;
import com.orange.base.security.service.UserService;
import com.orange.base.security.utils.SecurityUtil;
import com.orange.base.security.vo.FunctionVO;
import com.orange.base.tools.freemarker.FreeMarkerUtil;
import com.orange.base.tools.redis.RedisCache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenguojun on 8/23/16.
 */
@Controller
@RequestMapping("/api/index")
public class IndexController {

    @Autowired
    UserService userService;

    @Autowired
    FunctionService functionService;

    @Autowired
    RedisCache redisCache;

    @Autowired
    private FreeMarkerUtil freeMarkerUtil;

    @RequestMapping(value = "/current", method = RequestMethod.GET)
    @ResponseBody
    public Result myFunction() {
        String currentLoginName = SecurityUtil.getCurrentUserName();
        if (StringUtils.isEmpty(currentLoginName)) {
            throw new AuthBusinessException(AuBzConstant.IS_NOT_LOGIN);
        }
        List<FunctionVO> function = (List<FunctionVO>) redisCache
                .get(SecurityConstant.FUNCTION_CACHE_PREFIX + currentLoginName);
        if (function == null) {
            function = userService.findUserFunctionByLoginName(currentLoginName);
            redisCache.set(SecurityConstant.FUNCTION_CACHE_PREFIX + currentLoginName, function);
        }
        return ResponseUtil.success(function);
    }
}
