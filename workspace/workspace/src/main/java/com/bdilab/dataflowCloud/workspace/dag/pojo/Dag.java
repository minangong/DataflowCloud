package com.bdilab.dataflowCloud.workspace.dag.pojo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bdilab.dataflowCloud.workspace.dag.consts.CommonConstants;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Dag pojo.
 * You can use it to operate DAG graphs.
 * Each Dag.class instance can contain multiple Dags.
 *
 * @author wh
 * @date 2021/4/8
 */
@Slf4j
@Data
public class Dag {
  public final DagStateProxy  dagStateProxy  = new DagStateProxy(this);
  private Map<String, DagNode> dagMap = new HashMap<>();

  private String id;

  public Dag() {
  }

  public Dag(Map<Object, Object> dagMap) {
    this.dagMap = new HashMap<>();
    if (dagMap == null) {
      return;
    }
    // 依赖 FastJson 的 autotype 工作, 通常情况下序列化出来并不一定是 DagNode 类型
    for (Map.Entry<Object,Object> e: dagMap.entrySet()) {
      DagNode value = DagNodeBuilder.convertNode(e.getValue());
      this.dagMap.put((String) e.getKey(), value);
    }
  }



  /**
   * Get node.
   *
   * @param nodeId node ID
   */
  public DagNode getDagNode(String nodeId) {
    return dagMap.get(nodeId);
  }

  /**
   * Gets the next nodes of this node.
   *
   * @param nodeId node ID
   * @return list of dag node
   */
  public List<DagNode> getNextNodes(String nodeId) {
    DagNode node = getDagNode(nodeId);
    if (node == null) {
      return Collections.emptyList();
    }
    List<DagNode> nextNodes = new ArrayList<>();
    for (OutputDataSlot outputDataSlot : node.getOutputDataSlots()) {
      nextNodes.add(dagMap.get(outputDataSlot.getNextNodeId()));
    }
    return nextNodes;
  }

  /**
   * Gets the preceding node of this node.
   *
   * @param nodeId node ID
   * @return list of dag node
   */
  public List<DagNode> getPreNodes(String nodeId) {
    DagNode node = getDagNode(nodeId);
    List<DagNode> preNodes = new ArrayList<>();
    for (InputDataSlot inputDataSlot : node.getInputDataSlots()) {
      if (!StringUtils.isEmpty(inputDataSlot.getPreNodeId())) {
        preNodes.add(dagMap.get(inputDataSlot.getPreNodeId()));
      }
    }
    return preNodes;
  }

  public List<DagNode> getHeadNodes() {
    return dagMap.values().stream().filter(DagNode::isHeadNode).collect(Collectors.toList());
  }

  /**
   * Is head node or not.
   *
   * @param nodeId node ID
   */
  public boolean isHeadNode(String nodeId) {
    return getDagNode(nodeId).isHeadNode();
  }

  public Integer size() {
    return dagMap.size();
  }

}
