package com.bdilab.dataflowCloud.workspace.workspace.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.bdilab.dataflowCloud.workspace.workspace.model.Workspace;

/**
 * Workspace Service.
 *
 * @author wh
 * @date 2021/04/18
 */
public interface WorkspaceService extends IService<Workspace> {

  /**
   * 添加工作区
   * @param workspaceName 工作区名称
   * @return ResourceNodeRespVO
   */
  String insertWorkspace(String workspaceName, String workspaceDescription, int mark);

  /**
   * 修改工作区
   * @param workspaceId 工作区id
   * @param newName 新名称
   * @return
   */
  int updateWorkspace(String workspaceId, String newName, String newDescription);

  /**
   * 删除工作区
   * @param workspaceId 工作区id
   */
  void deleteWorkspace(String workspaceId);

}
