package com.bdilab.dataflowCloud.workspace.dag.controller;

import com.alibaba.fastjson.JSONObject;
import com.bdilab.dataflowCloud.workspace.dag.consts.WebConstants;
import com.bdilab.dataflowCloud.workspace.dag.service.WebSocketResolveService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * UniformController for all operator interface.
 *
 * @author Zunjing Chen
 * @date 2021-11-03
 */
@Slf4j
@RestController
@CrossOrigin
@Api(tags = "Linkage")
@RequestMapping(value = WebConstants.BASE_API_PATH + "/gluttony/")
public class DagController {

  @Autowired
  WebSocketResolveService webSocketResolveService;

  @PostMapping("/linkage")
  @ApiOperation(value = "联动接口")
  public void linkage(@RequestBody JSONObject requestData) {
    try {
      webSocketResolveService.resolve(requestData.toJSONString());
    }
    catch (Exception e) {
      log.error("Error in resolving linkage request:\n{}", e.getMessage());
      e.printStackTrace();
    }
  }

}
