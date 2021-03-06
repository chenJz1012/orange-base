package com.orange.database.core.dao;

import com.orange.common.utils.TreeNode;
import com.orange.database.core.model.Role;
import com.orange.system.dto.HasRoleUserDto;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface RoleMapper extends Mapper<Role> {

    int insertRoleFunction(@Param("roleId") Integer roleId,@Param("functionId")  Integer functionId);

    List<Map> findRoleMatchUpFunctions();

    List<TreeNode> selectRoleTreeNodes(Role role);

    List<Role> findRoleList(Role role);

    List<Integer> findFunctionByRoleId(@Param("roleId") Integer roleId);

    int deleteFunctionByRoleId(@Param("roleId") Integer roleId);

    List<HasRoleUserDto> findHasRoleUserDtoList(HasRoleUserDto hasRoleUserDto);

    void deleteUserRoleRelateByRoleId(Integer id);

    List<Integer> findHasRoleUserIdsByRoleId(@Param("roleId") Integer roleId);

    List<Map> selectRoleUserCount(@Param("role")Role role);
}
