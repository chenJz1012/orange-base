package com.orange.base.security.service;

import com.github.pagehelper.PageInfo;
import com.orange.base.basedao.service.IService;
import com.orange.base.common.TreeNode;
import com.orange.base.security.model.Function;

import java.util.List;

/**
 * 工程：os-app 创建人 : ChenGJ 创建时间： 2015/9/4 说明：
 */
public interface FunctionService extends IService<Function> {

    int insertFunction(Function function);

    int updateFunction(Function function);

    Function findFunctionById(int id);

    int deleteFunctionById(int id);

    List<TreeNode> getFunctionTreeNodes(Function function);

    PageInfo<Function> findFunctionList(int pageNum, int pageSize, Function function);
}
