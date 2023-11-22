package com.bdilab.dataflowCloud.workspace.dag.pojo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bdilab.dataflow.common.enums.OperatorOutputTypeEnum;
import com.bdilab.dataflow.core.store.dag.consts.DagConstants;
import com.bdilab.dataflow.core.store.utils.DagUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
public class DagManipulateProxy {
  private final Dag dag;

  public DagManipulateProxy(Dag dag) {
    this.dag = dag;
  }

  /**
   * Add a node to the dag.
   *
   * @param dagNode dag node
   */
  public void addNode(DagNode dagNode) {
    dag.getDagMap().put(dagNode.getNodeId(), dagNode);
  }

  /**
   * Add an edge to the dag.
   * Insert together. To prevent a insertion is successful, the other is a failure。
   *
   * @param preNodeId  ID of preceding node of the edge
   * @param nextNodeId ID of subsequent node of the edge
   * @param slotIndex  Slot index of the next node
   * @param dag
   */
  public void addEdge(String preNodeId, String nextNodeId, Integer slotIndex, Dag dag) {
    DagNode preNode = dag.getDagNode(preNodeId);
    DagNode nextNode = dag.getDagNode(nextNodeId);

    preNode.getOutputDataSlots().add(new OutputDataSlot(nextNodeId, slotIndex));

    // preNode 是 filter 或者 chart
    if (DagUtils.isFilterTypeNode(preNode)) {
      nextNode.getFilterId(slotIndex).add(preNodeId);
      // 自动填充数据集, todo eblk后面去掉了这个功能要用户手动填充！（后续版本可以删去，要与前端商量）
      String fillDataSource = preNode.getInputDataSource(0);
      if (StringUtils.isEmpty(nextNode.getInputDataSource(slotIndex))
          && preNode.getInputSlotSize() == 1
          && !StringUtils.isEmpty(fillDataSource)) {
        String copyToTableName = DagUtils.getTempInputTableName(nextNodeId);
        nextNode.setDataSource(slotIndex, copyToTableName);
        dag.getUpdateNodeTableRecord().setCopyTableName(fillDataSource, copyToTableName);
      }
    } else {
      // TODO NullPointerException
      if (!StringUtils.isEmpty(nextNode.getPreNodeId(slotIndex))) {
        // 本数据槽已经被连了，需要替换
        DagNode oldPreNode = dag.getDagMap().get(nextNode.getPreNodeId(slotIndex));
        oldPreNode.removeOutputSlot(new OutputDataSlot(nextNodeId, slotIndex));
      }

      if (dag.isTempInputAndLast(nextNode.getInputDataSource(slotIndex))) {
        dag.getUpdateNodeTableRecord().addDeleteInputTableName(nextNode.getInputDataSource(slotIndex));
      }
      nextNode.setPreNodeId(slotIndex, preNodeId);
      nextNode.setDataSource(slotIndex, DagUtils.getTempTableName(preNodeId));
    }
    dag.getDagMap().put(preNodeId, preNode);
    dag.getDagMap().put(nextNodeId, nextNode);
  }

  /**
   * Remove node from the dag.
   *
   * @param deletedNodeId the ID of node that will be deleted
   * @param dag
   */
  public void removeNode(String deletedNodeId, Dag dag) {
    DagNode deletedNode = dag.getDagNode(deletedNodeId);
    if (deletedNode == null) {
      return;
    }
    for (int i = 0;  i < deletedNode.getInputDataSlots().length; i++) {
      //删除前节点的next信息
      InputDataSlot inputDataSlot = deletedNode.getInputDataSlots()[i];
      String preNodeId = inputDataSlot.getPreNodeId();
      List<String> filterIds = inputDataSlot.getFilterId();
      OutputDataSlot deletedSlot = new OutputDataSlot(deletedNodeId, i);
      if (!StringUtils.isEmpty(preNodeId)) {
        dag.getDagMap().get(preNodeId).getOutputDataSlots().remove(deletedSlot);
      }
      for (String filterId : filterIds) {
        dag.getDagMap().get(filterId).getOutputDataSlots().remove(deletedSlot);
      }
    }
    if (DagUtils.isFilterTypeNode(deletedNode)) {
      //本节点为filter
      for (OutputDataSlot outputDataSlot : deletedNode.getOutputDataSlots()) {
        //删除后节点的filter信息
        DagNode nextNode = dag.getDagMap().get(outputDataSlot.getNextNodeId());
        nextNode.getFilterId(outputDataSlot.getNextSlotIndex()).remove(deletedNodeId);
        nextNode.getEdgeTypeMap(outputDataSlot.getNextSlotIndex()).remove(deletedNodeId);
      }
    } else {
      //本节点为table
      dag.getUpdateNodeTableRecord().addDeleteTableName(
          DagUtils.getTempTableName(deletedNodeId));
      if (!deletedNode.getOutputDataSlots().isEmpty()) {
        String copyFromTableName = DagUtils.getTempTableName(deletedNodeId);
        String copyToTableName = DagUtils.getTempInputTableName(deletedNodeId);
        for (OutputDataSlot outputDataSlot : deletedNode.getOutputDataSlots()) {
          //删除后节点的table信息
          DagNode nextNode = dag.getDagMap().get(outputDataSlot.getNextNodeId());
          nextNode.setPreNodeId(outputDataSlot.getNextSlotIndex(), null);
          nextNode.getEdgeTypeMap(outputDataSlot.getNextSlotIndex()).remove(deletedNodeId);
          nextNode.setDataSource(outputDataSlot.getNextSlotIndex(), copyToTableName);
        }
        dag.getUpdateNodeTableRecord().setCopyTableName(copyFromTableName, copyToTableName);
      }
    }

    //删除输入
    for (InputDataSlot inputDataSlot : deletedNode.getInputDataSlots()) {
      if (StringUtils.isEmpty(inputDataSlot.getPreNodeId())) {
        if (dag.isTempInputAndLast(inputDataSlot.getDataSource())) {
          dag.getUpdateNodeTableRecord().addDeleteInputTableName(inputDataSlot.getDataSource());
        }
      }
    }
    dag.getDagMap().remove(deletedNodeId);
  }

  /**
   * Remove edge from the dag.
   *
   * @param preNodeId  the ID of preceding node
   * @param nextNodeId the ID of subsequent node
   * @param slotIndex  Slot index of the next node
   * @param dag
   */
  public void removeEdge(String preNodeId, String nextNodeId, Integer slotIndex, Dag dag) {
    DagNode preNode = dag.getDagNode(preNodeId);
    DagNode nextNode = dag.getDagNode(nextNodeId);
    preNode.getOutputDataSlots().remove(new OutputDataSlot(nextNodeId, slotIndex));

    if (DagUtils.isFilterTypeNode(preNode)) {
      //filter边
      nextNode.getFilterId(slotIndex).remove(preNodeId);
      nextNode.getEdgeTypeMap(slotIndex).remove(preNodeId);
    } else {
      //table边
      String copyFromTableName = DagUtils.getTempTableName(preNodeId);
      String copyToTableName = DagUtils.getTempInputTableName(preNodeId);
      dag.getUpdateNodeTableRecord().setCopyTableName(copyFromTableName, copyToTableName);
      nextNode.setPreNodeId(slotIndex, null);
      nextNode.getEdgeTypeMap(slotIndex).remove(preNodeId);
      nextNode.setDataSource(slotIndex, copyToTableName);
    }
    dag.getDagMap().put(preNodeId, preNode);
    dag.getDagMap().put(nextNodeId, nextNode);

    /**
     * TODO Reset nodeDescription after edge is removed,
     * otherwise there will be conflict between new edge and outdated nodeDescription.
     * nextNode.setNodeDescription(resetDescription);
     **/
  }

  /**
   * Updating node description.
   *
   * @param nodeId          new node
   * @param nodeDescription node description
   * @param dag
   */
  public void updateNode(String nodeId, Object nodeDescription, Dag dag) {
    DagNode node = dag.getDagNode(nodeId);
    JSONObject newNodeDescription = (JSONObject) nodeDescription;
    JSONArray newDataSources = newNodeDescription.getJSONArray("dataSource");
    JSONArray oldDataSources = ((JSONObject) node.getNodeDescription()).getJSONArray("dataSource");
    if (newDataSources.size()  != oldDataSources.size()) {
      throw new RuntimeException("Input [dataSource] size error !");
    }

    node.setNodeDescription(newNodeDescription);

    //1
//    newNodeDescription.put("dataSource", oldDataSources);
//    node.setNodeDescription(newNodeDescription);

    //2   todo 等前端改完，把1删了，换成这个2，2具有更新数据源功能,记得测试
      if (!newDataSources.equals(oldDataSources)) {
        for (int i = 0; i < newDataSources.size(); i++) {
          String oldDataSource = oldDataSources.getString(i);
          String newDataSource = newDataSources.getString(i);

          String preNodeId = node.getPreNodeId(i);
          boolean noPreNode = ! StringUtils.isEmpty(preNodeId);
          boolean nonEmptyAndUpdated = ! StringUtils.isEmpty(newDataSource);

          // newDataSource is valid only if not empty and not equals to oldDataSource.
          if (nonEmptyAndUpdated) {
            if (! noPreNode) {

              if (preNodeId!=null){
                // 删边
                DagNode preNode = (DagNode) dag.getDagMap().get(node.getPreNodeId(i));
                preNode.removeOutputSlot(new OutputDataSlot(nodeId, i));
                node.setPreNodeId(i, null);
                node.getEdgeTypeMap(i).remove(node.getPreNodeId(i));
                String preId = preNode.getNodeId(), preType = preNode.getNodeType();
                String id = node.getNodeId(), type = node.getNodeType();
                log.debug("Deleted edge[({}/{})=>({}/{})]", preType, preId, type, id);
              }
            } else {
              if (dag.isTempInputAndLast(oldDataSource)) {
                dag.getUpdateNodeTableRecord().addDeleteInputTableName(oldDataSource);
              }
            }
            node.setDataSource(i, newDataSource);
          }
          // If not, we should use oldDataSource instead.
          else {
            node.setDataSource(i, oldDataSource);
          }
        }
      }

  }

  /**
   * Update edge.
   *
   * @param preNodeId  preNode Id
   * @param nextNodeId nextNode Id
   * @param slotIndex  slot index
   * @param edgeType   edge type
   * @param dag
   */
  public void updateEdge(String preNodeId,
                         String nextNodeId,
                         Integer slotIndex,
                         String edgeType, Dag dag
  ) {
    DagNode nextNode = dag.getDagNode(nextNodeId);
    DagNode preNode = dag.getDagNode(preNodeId);
    switch (edgeType) {
      case DagConstants.DEFAULT_LINE:
        break;
      case DagConstants.DASHED_LINE:
//        if (!OperatorOutputTypeEnum.isFilterOutput(preNode.getNodeType())) {
        if (!DagUtils.isFilterTypeNode(preNode)) {
          throw new RuntimeException("For dashed edge, the output node must be of type Filter !");
        }
        break;
      case DagConstants.BRUSH_LINE:
        if (!OperatorOutputTypeEnum.isChart(preNode.getNodeType())
            || !OperatorOutputTypeEnum.isChart(nextNode.getNodeType())) {
          throw new RuntimeException(
              "For brush edge, both of output and input node must be Chart !");
        }
        break;
      default:
        throw new RuntimeException("Error edge type !");
    }
    nextNode.getEdgeTypeMap(slotIndex).put(preNodeId, edgeType);
  }
}