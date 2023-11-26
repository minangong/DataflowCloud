package com.bdilab.dataflowCloud.workspace.workspace.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.bdilab.dataflowCloud.workspace.dag.consts.WebConstants;
import com.bdilab.dataflowCloud.workspace.workspace.common.response.HttpCode;
import com.bdilab.dataflowCloud.workspace.workspace.common.response.HttpResponse;
import com.bdilab.dataflowCloud.workspace.workspace.model.Workspace;
import com.bdilab.dataflowCloud.workspace.workspace.service.WorkspaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Temp.
 */
@Slf4j
@RestController
@CrossOrigin
@Api(tags = "workspace")
@RequestMapping(value = WebConstants.BASE_API_PATH + "/workspace/")
public class WorkspaceController {
  @Resource
  private WorkspaceService workspaceService;



  @PostMapping("/addWorkspace")
  @ApiOperation(value = "添加工作区")
  public HttpResponse addWorkspace(
      @RequestParam String workspaceName,
      @RequestParam String workspaceDescription,
      @RequestParam int mark
  ) {
    return new HttpResponse(HttpCode.OK, workspaceService
        .insertWorkspace(workspaceName, workspaceDescription, mark));
  }

  @PostMapping("/updateWorkspace")
  @ApiOperation(value = "更新工作区")
  public HttpResponse updateWorkspace(
      @RequestParam String workspaceId,
      @RequestParam String newName,
      @RequestParam String newDescription
  ) {
    return new HttpResponse(HttpCode.OK, workspaceService
        .updateWorkspace(workspaceId,newName,newDescription
        ));
  }

  @GetMapping("/getWorkspaceById/{workspaceId}")
  @ApiOperation(value = "根据id查看工作区")
  public HttpResponse getWorkspaceById(@PathVariable String workspaceId) {
    return new HttpResponse(HttpCode.OK, workspaceService.getById(workspaceId));
  }
  @PostMapping("/getWorkspaces")
  @ApiOperation(value = "根据idList查看工作区列表")
  public HttpResponse getWorkspaces(@RequestBody JSONObject jsonData) {
    JSONObject requestBody = JSONObject.parseObject(jsonData.toJSONString());
    JSONArray workspaceIds = requestBody.getJSONArray("workspaceIds");
    List<Workspace> workspaces = new ArrayList<>();
    if (workspaceIds.isEmpty()) {
      LambdaQueryWrapper<Workspace> lambdaQueryWrapper = new LambdaQueryWrapper<>();
      lambdaQueryWrapper.eq(Workspace::getMark, 0);
      return new HttpResponse(HttpCode.OK, workspaceService.list(lambdaQueryWrapper));
    }
    for (Object workspaceId : workspaceIds) {
      workspaces.add(workspaceService.getById((Serializable) workspaceId));
    }
    return new HttpResponse(HttpCode.OK, workspaces);
  }

  @DeleteMapping("/deleteWorkspace")
  @ApiOperation(value = "删除工作区")
  public HttpResponse deleteWorkspace(
      @RequestParam String workspaceId
  ) {
    workspaceService.deleteWorkspace(workspaceId);
    return new HttpResponse(HttpCode.OK, null);
  }



//  @GetMapping("/getInputDatasetsByWorkspaceId")
//  @ApiOperation(value = "查看当前工作区对应的输入数据集")
//  public HttpResponse getInputDatasetsByWorkspaceId(@RequestParam String workspaceId) {
//    return new HttpResponse(HttpCode.OK, workspaceDatasetService
//        .getInputDatasetsByWorkspaceId(workspaceId));
//  }
//
//  @GetMapping("/getOutputDatasetsByWorkspaceId")
//  @ApiOperation(value = "查看当前工作区对应的输出数据集")
//  public HttpResponse getOutputDatasetsByWorkspaceId(@RequestParam String workspaceId) {
//    return new HttpResponse(HttpCode.OK, workspaceDatasetService
//        .getOutputDatasetsByWorkspaceId(workspaceId));
//  }
//
//  @PostMapping("/addDataSets")
//  @ApiOperation(value = "添加数据集")
//  public HttpResponse addDataSets(@RequestBody JSONObject idData) {
//    return new HttpResponse(
//        HttpCode.OK,
//        workspaceDatasetService.save(idData.toJSONString())
//    );
//  }
//
//  @DeleteMapping("/deleteDataSet")
//  @ApiOperation(value = "删除数据集")
//  public HttpResponse deleteDataSet(@RequestParam String workspaceId, @RequestParam String datasetId) {
//    return new HttpResponse(
//        HttpCode.OK,
//        workspaceDatasetService.remove(workspaceId, datasetId)
//    );
//  }
}
