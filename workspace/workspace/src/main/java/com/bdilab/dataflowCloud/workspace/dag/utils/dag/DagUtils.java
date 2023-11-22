package com.bdilab.dataflowCloud.workspace.dag.utils.dag;


import com.bdilab.dataflowCloud.workspace.dag.consts.CommonConstants;

/**
 * Dag Utils.
 *
 * @author wh
 * @date 2021/04/18
 */
public class DagUtils {


  public static final String DATABASE = "dataflow";

  /**
   * Clickhouse table prefix.
   */
  public static final String TEMP_TABLE_PREFIX = "temp_";

  public static final String CPL_TEMP_TABLE_PREFIX = CommonConstants.DATABASE + "." + CommonConstants.TEMP_TABLE_PREFIX;
  public static String getTempTableName(String dagNodeId) {
    return CPL_TEMP_TABLE_PREFIX + dagNodeId;
  }


}
