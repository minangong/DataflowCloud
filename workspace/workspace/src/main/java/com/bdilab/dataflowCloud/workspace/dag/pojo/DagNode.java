package com.bdilab.dataflowCloud.workspace.dag.pojo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.bdilab.dataflowCloud.workspace.dag.dto.DagNodeInputDto;
import com.bdilab.dataflowCloud.workspace.dag.enums.DagNodeState;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * The node of dag.
 *
 * @author wh
 * @date 2021/11/12
 */
@Data
public class DagNode {
  /**
   * Unique identifier that cannot be repeated.
   */
  private String nodeId;

  private InputDataSlot[] inputDataSlots;

  /**
   * The list containing the ID of the subsequent nodes.
   */
  private List<OutputDataSlot> outputDataSlots;

  private DagNodeState nodeState;

  private String nodeDataResult;

  /**
   * The type of operator.
   */
  private String nodeType;

  /**
   * The description of the node job.
   */
  private Object nodeDescription;

  /**
   * For fastjson serialize.
   */
  protected DagNode() {
  }

  /**
   * Constructor.
   *
   * @param dagNodeInputDto dagNodeInputDto
   */
  public DagNode(DagNodeInputDto dagNodeInputDto) {
    boolean isReady = true;

    this.nodeId = dagNodeInputDto.getNodeId();
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

  @JSONField(serialize = false)
  public Integer getInputSlotSize() {
    return this.inputDataSlots.length;
  }

  @JSONField(serialize = false)
  public String getPreNodeId(int slotIndex) {
    return this.inputDataSlots[slotIndex].getPreNodeId();
  }

  @JSONField(serialize = false)
  public void setPreNodeId(int slotIndex, String preNodeId) {
    this.inputDataSlots[slotIndex].setPreNodeId(preNodeId);
  }







  @JSONField(serialize = false)
  public boolean isHeadNode() {
    InputDataSlot[] inputDataSlots = getInputDataSlots();
    for (InputDataSlot inputDataSlot : inputDataSlots) {
      if (!StringUtils.isEmpty(inputDataSlot.getPreNodeId())) {
        return false;
      }
    }
    return true;
  }

  @JSONField(serialize = false)
  public String getInputDataSource(int slotIndex) {
    return this.inputDataSlots[slotIndex].getDataSource();
  }


  /**
   * Set new dataSource.
   *
   * @param slotIndex slot index
   * @param dataSource new dataSource
   */
  @JSONField(serialize = false)
  public void setDataSource(int slotIndex, String dataSource) {
    this.inputDataSlots[slotIndex].setDataSource(dataSource);
    JSONObject nodeDescription = (JSONObject) this.nodeDescription;
    JSONArray decDataSource = nodeDescription.getJSONArray("dataSource");
    decDataSource.set(slotIndex, dataSource);
    nodeDescription.put("dataSource", decDataSource);
  }

  /**
   * Get all input dataSources.
   */
  @JSONField(serialize = false)
  public List<String> getInputDataSources() {
    List<String> dataSources = new ArrayList<>();
    for (int i = 0; i < getInputSlotSize(); i++) {
      dataSources.add(getInputDataSource(i));
    }
    return dataSources;
  }

  @JSONField(serialize = false)
  public void removeOutputSlot(OutputDataSlot removeOutputSlot) {
    this.outputDataSlots.remove(removeOutputSlot);
  }

  @JSONField(serialize = false)
  public void clearOutputSlot() {
    this.outputDataSlots.clear();
  }
}
