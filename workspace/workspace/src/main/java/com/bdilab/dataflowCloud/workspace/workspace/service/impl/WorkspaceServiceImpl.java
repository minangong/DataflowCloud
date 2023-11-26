package com.bdilab.dataflowCloud.workspace.workspace.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bdilab.dataflowCloud.workspace.dag.utils.redis.RedisUtils;
import com.bdilab.dataflowCloud.workspace.exexute.service.DataSetService;
import com.bdilab.dataflowCloud.workspace.workspace.dao.WorkspaceMapper;
import com.bdilab.dataflowCloud.workspace.workspace.model.Workspace;
import com.bdilab.dataflowCloud.workspace.workspace.service.WorkspaceService;
import com.bdilab.dataflowCloud.workspace.workspace.utils.RandomIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Workspace Service Impl.
 *
 * @author wh
 * @date 2021/04/18
 */
@Slf4j
@Service
//@DS("mysql")
public class WorkspaceServiceImpl extends ServiceImpl<WorkspaceMapper, Workspace> implements WorkspaceService {

  @Resource
  WorkspaceMapper workspaceMapper;
  @Resource
  RedisUtils redisUtils;

  @Resource
  DataSetService dataSetService;


  @Override
  @Transactional
  public String insertWorkspace(String workspaceName, String workspaceDescription, int mark) {
    String workspaceId = RandomIdGenerator.uuid();

    Workspace workspace = new Workspace(workspaceId, workspaceName, workspaceDescription, null, null, null, mark);

    if (workspaceMapper.insert(workspace) == 1) {
      return workspaceId;
    }
    return "添加工作区失败";
  }

  @Override
  @Transactional
  public int updateWorkspace(String workspaceId, String newName, String newDescription) {
    UpdateWrapper<Workspace> updateWrapper = new UpdateWrapper<>();
    updateWrapper.eq("id", workspaceId);
    updateWrapper.set("workspace_name", newName);
    updateWrapper.set("workspace_description", newDescription);

    return workspaceMapper.update(null, updateWrapper);
  }

  @Override
  @Transactional
  public void deleteWorkspace(String workspaceId) {
    // 删除工作区
    workspaceMapper.deleteById(workspaceId);

    // 删除中间ck表
    dataSetService.clearDagViews(workspaceId);

    // 删除拓扑图
    redisUtils.del(workspaceId);

  }


}
