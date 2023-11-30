package com.bdilab.dataflowCloud.workspace.dag.pojo;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bdilab.dataflowCloud.workspace.dag.consts.CommonConstants;
import com.bdilab.dataflowCloud.workspace.dag.enums.DagNodeState;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@AllArgsConstructor
@Slf4j
public class DagNodeBuilder {
    private String nodeId;
    private String nodeType;
    private Object nodeDescription;

    private boolean isFromTable;

    public DagNodeBuilder(){

    }

    public DagNodeBuilder nodeId(String nodeId){
        this.nodeId = nodeId;
        return this;
    }

    public DagNodeBuilder nodeType(String nodeType){
        this.nodeType = nodeType;
        return this;
    }

    public DagNodeBuilder nodeDesciption(Object nodeDescription){
        this.nodeDescription = nodeDescription;
        return this;
    }

    public DagNodeBuilder isFromTable(boolean isFromTable){
        this.isFromTable = isFromTable;
        return this;

    }

    public DagNode build(){
        DagNode dagNode = new DagNode();

        dagNode.setNodeId(this.nodeId);

        JSONArray dataSources =
                ((JSONObject) nodeDescription).getJSONArray("dataSource");
        dagNode.inputDataSlots = new InputDataSlot[dataSources.size()];


        for (int i = 0; i < dataSources.size(); i++) {
            dagNode.inputDataSlots[i] = new InputDataSlot(dataSources.getString(i));
        }
        dagNode.outputDataSlots = new ArrayList<>();
        dagNode.nodeType = nodeType;


        if(isFromTable){
            dagNode.nodeState = DagNodeState.ALWAYS_SUCCEED;
        }else{
            dagNode.nodeState = DagNodeState.WAIT;
        }

        String result = CommonConstants.DATABASE +"."+ CommonConstants.TEMP_TABLE_PREFIX + this.nodeId;
        dagNode.nodeDataResult = result;

        ((JSONObject)nodeDescription).put("nodeDataResult",result);

        dagNode.nodeDescription = nodeDescription;

        return dagNode;
    }

    public static DagNode convertNode(Object v) {
        if (v == null) {
            return null;
        }
        if (v instanceof DagNode) {
            return (DagNode) v;
        }
        else if (v instanceof JSONObject) {
            return ((JSONObject)v).toJavaObject(DagNode.class);
        }
        return JSON.parseObject(v.toString(), DagNode.class);
    }

}
