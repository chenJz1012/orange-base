package com.orange.base.security.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.orange.base.basedao.service.impl.BaseService;
import com.orange.base.common.TreeNode;
import com.orange.base.security.dao.FunctionMapper;
import com.orange.base.security.model.Function;
import com.orange.base.security.service.FunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 工程：os-app 创建人 : ChenGJ 创建时间： 2015/9/4 说明：
 */
@Service("functionService")
public class FunctionServiceImpl extends BaseService<Function> implements FunctionService {

    @Autowired
    private FunctionMapper functionMapper;

    @Override
    public int insertFunction(Function function) {
        return getMapper().insertSelective(function);
    }

    @Override
    public int updateFunction(Function function) {
        return getMapper().updateByPrimaryKeySelective(function);
    }

    @Override
    public Function findFunctionById(int id) {
        return getMapper().selectByPrimaryKey(id);
    }

    @Override
    public int deleteFunctionById(int id) {
        return getMapper().deleteByPrimaryKey(id);
    }

    @Override
    public List<TreeNode> getFunctionTreeNodes(Function function) {
        return functionMapper.selectFunctionTreeNodes(function);
    }

    @Override
    public PageInfo<Function> findFunctionList(int pageNum, int pageSize, Function function) {
        PageHelper.startPage(pageNum, pageSize);
        List<Function> list = functionMapper.findFunctionList(function);
        PageInfo<Function> page = new PageInfo<Function>(list);
        return page;
    }
}