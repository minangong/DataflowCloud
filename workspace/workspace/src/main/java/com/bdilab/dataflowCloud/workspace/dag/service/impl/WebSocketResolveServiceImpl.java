package com.bdilab.dataflowCloud.workspace.dag.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bdilab.dataflowCloud.workspace.dag.pojo.Dag;
import com.bdilab.dataflowCloud.workspace.dag.pojo.DagNodeBuilder;
import com.bdilab.dataflowCloud.workspace.dag.service.DagService;
import com.bdilab.dataflowCloud.workspace.dag.service.WebSocketResolveService;
import com.bdilab.dataflowCloud.workspace.workspace.service.WorkspaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Service
@Slf4j
public class WebSocketResolveServiceImpl implements WebSocketResolveService {

    @Autowired
    WorkspaceService workspaceService;

    @Autowired
    DagService dagService;

    @Override
    public void resolve(String jsonString) {
        JSONObject requestBody = JSONObject.parseObject(jsonString);

        String dagType = requestBody.getString("dagType");
        if (!parseLinkage(dagType)) {
            return ;
        }
        String workspaceId = requestBody.getString("workspaceId");
        String operatorType;
        String operatorId;
        JSONObject desc;
//        JSONArray dataSources = Objects.isNull(desc) ? null : desc.getJSONArray("dataSource");
        // do nothing for requests except `updateNode` to workspaces with mark-2
        switch (dagType) {
            case "addNode":
                operatorType = requestBody.getString("operatorType");
                operatorId = (String) requestBody.getOrDefault("operatorId", "");
                desc = requestBody.getJSONObject(operatorType + "Description");
                boolean isFromTable = requestBody.getBoolean("isFromTable");
                DagNodeBuilder dagNodeBuilder = new DagNodeBuilder()
                        .nodeId(operatorId)
                        .nodeDesciption(desc)
                        .nodeType(operatorType)
                        .isFromTable(isFromTable);
                dagService.addNode(workspaceId, dagNodeBuilder);
                break;
            case "updateNode":
                operatorType = requestBody.getString("operatorType");
                operatorId = (String) requestBody.getOrDefault("operatorId", "");
                desc = requestBody.getJSONObject(operatorType + "Description");
                dagService.updateNode(workspaceId, operatorId, desc);
                break;
            case "removeNode":
                operatorId = (String) requestBody.getOrDefault("operatorId", "");
                dagService.removeNode(workspaceId, operatorId);
                break;
            case "addEdge":
                String addPreNodeId = requestBody.getString("preNodeId");
                String addNextNodeId = requestBody.getString("nextNodeId");
                String addSlotIndex = requestBody.getString("slotIndex");
                dagService.addEdge(workspaceId, addPreNodeId, addNextNodeId, Integer.valueOf(addSlotIndex));
                break;
            case "removeEdge":
                String rmPreNodeId = requestBody.getString("preNodeId");
                String rmNextNodeId = requestBody.getString("nextNodeId");
                Integer rmSlotIndex = requestBody.getInteger("slotIndex");
                dagService.removeEdge(workspaceId, rmPreNodeId, rmNextNodeId, rmSlotIndex);
                break;
            default: return;
        }
    }

    private boolean parseLinkage(String dagType) {
        return "addNode".equals(dagType) || "removeNode".equals(dagType) || "updateNode".equals(dagType) ||
                "addEdge".equals(dagType) || "removeEdge".equals(dagType) || "updateEdge".equals(dagType);
    }
}
