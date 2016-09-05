package com.orange.base.core.api.sys;

import com.github.pagehelper.PageInfo;
import com.orange.base.common.TreeNode;
import com.orange.base.common.utils.PageConvertUtil;
import com.orange.base.common.utils.Result;
import com.orange.base.security.SecurityConstant;
import com.orange.base.security.exception.AuthBusinessException;
import com.orange.base.security.model.Function;
import com.orange.base.security.service.FunctionService;
import com.orange.base.security.service.UserService;
import com.orange.base.security.utils.SecurityUtil;
import com.orange.base.tools.redis.RedisCache;
import com.orange.base.common.utils.ResponseUtil;
import com.orange.base.security.exception.AuBzConstant;
import com.orange.base.security.vo.FunctionVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by cgj on 2016/4/9.
 */
@Controller
@RequestMapping("/api/sys/function")
public class FunctionController {

    @Autowired
    UserService userService;

    @Autowired
    FunctionService functionService;

    @Autowired
    RedisCache redisCache;

    @RequestMapping(value = "/pageList", method = RequestMethod.GET)
    @ResponseBody
    public Result functions(Function function,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "15") int pageSize) {
        PageInfo<Function> pageInfo = functionService.findFunctionList(pageNum, pageSize, function);
        return ResponseUtil.success(PageConvertUtil.grid(pageInfo));
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ResponseBody
    public Result insertFunction(Function function) {
        int result = functionService.insertFunction(function);
        return ResponseUtil.success();
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public Result updateFunction(Function function) {
        functionService.updateFunction(function);
        return ResponseUtil.success();
    }

    @RequestMapping(value = "/load/{functionId}", method = RequestMethod.GET)
    @ResponseBody
    public Result loadFunction(@PathVariable(value = "functionId") Integer functionId) {
        Function function = functionService.findFunctionById(functionId);
        return ResponseUtil.success(function);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public Result delete(@RequestParam(value = "functionId") Integer functionId) {
        functionService.deleteFunctionById(functionId);
        return ResponseUtil.success();
    }

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

    @RequestMapping(value = "/treeNodes", method = RequestMethod.POST)
    @ResponseBody
    public Object treeNodes(Function function) {
        List<TreeNode> list = functionService.getFunctionTreeNodes(function);
        return list;
    }

}
