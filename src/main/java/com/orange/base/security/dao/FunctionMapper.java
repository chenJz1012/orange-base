package com.orange.base.security.dao;

import com.orange.base.common.TreeNode;
import com.orange.base.security.model.Function;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface FunctionMapper extends Mapper<Function> {

    public List<TreeNode> selectFunctionTreeNodes(Function function);

    List<Function> findFunctionList(Function function);
}
