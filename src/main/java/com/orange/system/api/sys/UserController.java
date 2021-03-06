package com.orange.system.api.sys;

import com.github.pagehelper.PageInfo;
import com.orange.common.utils.PageConvertUtil;
import com.orange.database.core.model.User;
import com.orange.security.exception.AuthBusinessException;
import com.orange.security.service.UserService;
import com.orange.security.utils.SecurityUtil;
import com.orange.common.utils.ResponseUtil;
import com.orange.common.utils.Result;
import com.orange.security.exception.AuBzConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by cgj on 2016/4/9.
 */
@Controller
@RequestMapping("/api/sys/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/pageList", method = RequestMethod.GET)
    @ResponseBody
    public Result users(User user, @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "15") int pageSize) {
        PageInfo<User> pageInfo = userService.findUserList(pageNum, pageSize, user);
        return ResponseUtil.success(PageConvertUtil.grid(pageInfo));
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ResponseBody
    public Result insertUser(@Valid User user, BindingResult result) {
        if (result.hasErrors()) {
            logger.debug(result.toString());
            return ResponseUtil.error(result);
        }
        if (userService.findExistUser(user) > 0) {
            throw new AuthBusinessException(AuBzConstant.LOGIN_NAME_EXIST);
        }
        userService.insertUser(user);
        return ResponseUtil.success();
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public Result updateUser(User user) {
        if (userService.findExistUser(user) > 0) {
            throw new AuthBusinessException(AuBzConstant.LOGIN_NAME_EXIST);
        }
        userService.updateUser(user);
        return ResponseUtil.success();
    }

    @RequestMapping(value = "/load/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public Result loadUser(@PathVariable(value = "userId") int userId) {
        User user = userService.findUserById(userId);
        List roles = userService.findUserRoleByUserId(userId);
        if (roles != null) user.setRoles(roles);
        return ResponseUtil.success(user);
    }

    @RequestMapping(value = "/lock/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public Result lock(@PathVariable(value = "userId") int userId) {
        if (SecurityUtil.getCurrentUserId() == userId) {
            throw new AuthBusinessException(AuBzConstant.CANNOT_CHANGE_CURRENT_USER);
        }
        int result = userService.updateLockStatusByUserId(userId, false);
        if (result > 0) {
            return ResponseUtil.success();
        } else {
            return ResponseUtil.error("操作未成功。");
        }
    }

    @RequestMapping(value = "/unLock/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public Result unLock(@PathVariable(value = "userId") int userId) {
        if (SecurityUtil.getCurrentUserId() == userId) {
            throw new AuthBusinessException(AuBzConstant.CANNOT_CHANGE_CURRENT_USER);
        }
        int result = userService.updateLockStatusByUserId(userId, true);
        if (result > 0) {
            return ResponseUtil.success();
        } else {
            return ResponseUtil.error("操作未成功。");
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public Result delUser(@RequestParam(value = "userId") int userId) {
        if (SecurityUtil.getCurrentUserId() == userId) {
            throw new AuthBusinessException(AuBzConstant.CANNOT_CHANGE_CURRENT_USER);
        }
        userService.deleteUser(userId);
        return ResponseUtil.success();
    }

    @RequestMapping(value = "/roles/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public Result userRoles(@PathVariable(value = "userId") int userId) {
        List roles = userService.findUserRoleByUserId(userId);
        return ResponseUtil.success(roles);
    }
}
