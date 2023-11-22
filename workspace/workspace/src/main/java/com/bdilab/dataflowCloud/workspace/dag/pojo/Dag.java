package com.bdilab.dataflowCloud.workspace.dag.pojo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bdilab.dataflowCloud.workspace.dag.consts.CommonConstants;
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
public class Dag {
  public final DagManipulateProxy dagManipulateProxy = new DagManipulateProxy(this);
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
      DagNode value = convertNode(e.getValue());
      this.dagMap.put((String) e.getKey(), value);
    }
  }

  private static DagNode convertNode(Object v) {
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


  /**
   * Get this dag list.
   *
   * @return all nodes as Map
   */
  public Map<String, DagNode> getDagMap() {
    return dagMap;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public DagManipulateProxy getDagManipulateProxy() {
    return dagManipulateProxy;
  }


  /**
   * Get this dag list.
   *
   * @return all nodes as List
   */
  public List<DagNode> getDagList() {
    List<DagNode> dagNodeList = new ArrayList<>();
    dagMap.forEach((k, v) -> dagNodeList.add(v));
    return dagNodeList;
  }



  public Integer size() {
    return dagMap.size();
  }



  /**
   * Size of connected sub-dag of a DAG.
   */
  public Integer sizeOfFlow() {
    int size = 0;
    Set<String> isFlag = new HashSet<>();
    for (Map.Entry<String, DagNode> e : dagMap.entrySet()) {
      String nodeId = e.getKey();
      DagNode node = e.getValue();
      if (isFlag.contains(nodeId)) {
        continue;
      }
//      System.out.println("nodeId:"+nodeId+",size"+size);
      size++;
      // bfs
      Queue<DagNode> queue = new LinkedList<>();
      queue.add(node);
      isFlag.add(nodeId);

      while (!queue.isEmpty()) {
        String nowId = queue.poll().getNodeId();
        for (DagNode preNode : getPreNodes(nowId)) {
          if (!isFlag.contains(preNode.getNodeId())) {
            isFlag.add(preNode.getNodeId());
            queue.add(preNode);
          }
        }
        for (DagNode filterNode : getFilterNodes(nowId)) {
          if (!isFlag.contains(filterNode.getNodeId())) {
            isFlag.add(filterNode.getNodeId());
            queue.add(filterNode);
          }
        }
        for (DagNode nextNode : getNextNodes(nodeId)) {
//          System.out.println("nextNode:"+nextNode);
          if (!isFlag.contains(nextNode.getNodeId())) {
            isFlag.add(nextNode.getNodeId());
            queue.add(nextNode);
          }
        }
      }
//      System.out.println("isFlag:"+isFlag);
    }
    return size;
  }


  /**
   * find all node by dataSource.
   */
  public List<DagNode> findByDataSource(String dataSource) {
    List<DagNode> result = new ArrayList<>();
    for (DagNode dagNode : getDagList()) {
      for (String inputDataSource : dagNode.getInputDataSources()) {
        if (inputDataSource.equals(dataSource)) {
          result.add(dagNode);
          break;
        }
      }
    }
    return result;
  }

  /**
   * 检测是否有其他节点引用了临时表。
   *
   * @param inputTableName datasSource 表示的表名。
   * @return
   */
  public boolean isTempInputAndLast(String inputTableName) {
    String[] parts = inputTableName.split("\\.");
    if (!StringUtils.isEmpty(inputTableName) && parts.length > 1
        && parts[1].startsWith(CommonConstants.TEMP_INPUT_TABLE_PREFIX)) {
      int sum = 0;
      for (Map.Entry<String, DagNode> e : dagMap.entrySet()) {
        List<String> inputDataSources = e.getValue().getInputDataSources();
        for (int i = 0; i < inputDataSources.size(); i++) {
          if (inputTableName.equals(inputDataSources.get(i))) {
            sum++;
            if (sum > 1) {
              return false;
            }
          }
        }
      }
      return true;
    }
    else {
      return false;
    }
  }
}
