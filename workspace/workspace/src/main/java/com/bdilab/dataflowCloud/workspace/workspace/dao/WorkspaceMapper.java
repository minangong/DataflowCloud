package com.bdilab.dataflowCloud.workspace.workspace.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bdilab.dataflowCloud.workspace.workspace.model.Workspace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * Workspace mapper.
 *
 * @author wh
 * @date 2021/04/18
 */
@Mapper
public interface WorkspaceMapper extends BaseMapper<Workspace> {
  @Select("select id from workspace where #{workspaceName} = workspace_name")
  Long selectIdByWorkspaceName(String workspaceName);
}
