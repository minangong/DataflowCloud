package com.bdilab.dataflowCloud.workspace.workspace.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bdilab.dataflowCloud.workspace.dag.consts.CommonConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Workspace model.
 *
 * @author wh
 * @date 2021/04/18
 */


@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "workspace")
public class Workspace {
  @TableId(value = "id", type = IdType.ASSIGN_UUID)
  private String id;

  @TableField("workspace_name")
  private String workspaceName;

  @TableField("workspace_description")
  private String workspaceDescription;

  @TableField("user_id")
  private String userId;

  @JsonFormat(pattern = CommonConstants.DATE_FORMAT,timezone="GMT+8")
  @TableField("update_time")
  private String updateTime;

  @JsonFormat(pattern = CommonConstants.DATE_FORMAT,timezone="GMT+8")
  @TableField("create_time")
  private String createTime;

  // 工作区来源的标志位：
  // 0: 用户自己创建的工作区
  // 1: 用于 UDO 测试的临时工作区
  // 2: 用于仪表板内部实现的工作区
  @TableField("mark")
  private int mark;
}
