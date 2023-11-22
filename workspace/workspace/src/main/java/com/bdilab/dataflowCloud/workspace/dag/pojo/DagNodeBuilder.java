package com.bdilab.dataflowCloud.workspace.dag.pojo;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bdilab.dataflowCloud.workspace.dag.enums.DagNodeState;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

public class DagNodeBuilder {
    private String nodeId;
    private String nodeType;
    private Object nodeDescription;

    public DagNodeBuilder(String nodeId,String nodeType, Object nodeDescription){


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

    public DagNode build(){
        DagNode dagNode = new DagNode();

        boolean isReady = true;

        dagNode.setNodeId(this.nodeId);
        JSONArray dataSources =
                ((JSONObject) dagNodeInputDto.getNodeDescription()).getJSONArray("dataSource");
        this.inputDataSlots = new InputDataSlot[dataSources.size()];


        for (int i = 0; i < dataSources.size(); i++) {
            this.inputDataSlots[i] = new InputDataSlot(dataSources.getString(i));
            if(StringUtils.isEmpty(dataSources.getString(i))) {
                isReady = false;
            }
        }
        this.outputDataSlots = new ArrayList<>();
        this.nodeType = dagNodeInputDto.getNodeType();
        this.nodeDescription = dagNodeInputDto.getNodeDescription();

        if(isReady){
            this.nodeState = DagNodeState.READY;
        }else{
            this.nodeState = DagNodeState.WAIT;
        }

        this.nodeDataResult = new String();

    }

}
