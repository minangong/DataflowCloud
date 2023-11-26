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
 * description: 工作区数据集中间表.
 *
 * @author zhb
 * @Date: 2022/5/16 23:40
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "workspace_dataset")
public class WorkspaceDataset {
  @TableId(value = "id", type = IdType.ASSIGN_UUID)
  private String id;

  @TableField(value = "workspace_id")
  private String workspaceId;

  @TableField(value = "input_dataset")
  private String inputDataset;

  @TableField(value = "output_dataset")
  private String outputDataset;


  @JsonFormat(pattern = CommonConstants.DATE_FORMAT, timezone = "GMT+8")
  @TableField("create_time")
  private String createTime;

  @JsonFormat(pattern = CommonConstants.DATE_FORMAT, timezone = "GMT+8")
  @TableField("update_time")
  private String updateTime;
}
