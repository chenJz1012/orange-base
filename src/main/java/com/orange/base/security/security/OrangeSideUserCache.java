package com.orange.base.security.security;

import com.orange.base.common.IBasicCache;
import com.orange.base.security.SecurityConstant;
import com.orange.base.security.service.UserService;
import com.orange.base.tools.redis.RedisCache;
import com.orange.base.security.vo.FunctionVO;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Created by cgj on 2016/4/13.
 */
public class OrangeSideUserCache implements UserCache, InitializingBean {

    private IBasicCache<String, UserDetails> cache = new OrangeSideNullCache<String, UserDetails>();

    private RedisCache redisCache;

    private UserService userService;

    public void setRedisCache(RedisCache redisCache) {
        this.redisCache = redisCache;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(cache);
    }

    @Override
    public UserDetails getUserFromCache(String username) {
        UserDetails userDetails = null;
        try {
            userDetails = (UserDetails) cache.get(SecurityConstant.USER_CACHE_PREFIX + username);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return userDetails == null ? null : userDetails;
        }

    }

    @Override
    public void putUserInCache(UserDetails user) {
        cache.set(SecurityConstant.USER_CACHE_PREFIX + user.getUsername(), user);
        List<FunctionVO> function = userService.findUserFunctionByLoginName(user.getUsername());
        redisCache.set(SecurityConstant.FUNCTION_CACHE_PREFIX + user.getUsername(), function);
    }

    @Override
    public void removeUserFromCache(String username) {
        cache.del(SecurityConstant.USER_CACHE_PREFIX + username);
        redisCache.del(SecurityConstant.FUNCTION_CACHE_PREFIX + username);
    }

    public void setCache(IBasicCache<String, UserDetails> cache) {
        this.cache = cache;
    }

    public void removeUserFromCacheByUserId(Integer userId) {
        String loginName = userService.findLoginNameByUserId(userId);
        if (loginName != null) {
            removeUserFromCache(loginName);
        }
    }
}
