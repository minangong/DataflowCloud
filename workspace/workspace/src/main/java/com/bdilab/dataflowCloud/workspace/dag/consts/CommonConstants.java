package com.bdilab.dataflowCloud.workspace.dag.consts;

/**
 * Common Constants.
 *
 * @author wh
 * @version 1.0
 * @date 2021/09/12
 */
public class CommonConstants {

  /**
   * The date format.
   */
  public static final String DATE_FORMAT = "yyyy-MM-dd";
  public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
  public static final String DATETIME64_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

  /**
   * Standard date format.
   */
  public static final String STANDARD_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
  public static final String STANDARD_DATETIME_FORMAT2 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
  public static final String STANDARD_DATETIME_FORMAT3 = "yyyy-MM-dd'T'HH:mm:ss";

  /**
   * The data type.
   */
  public static final String NUMERIC_NAME = "numeric";
  public static final String STRING_NAME = "string";
  public static final String DATE_NAME = "date";

  /**
   * Clickhouse database.
   */
  public static final String DATABASE = "dataflow";

  /**
   * Clickhouse table prefix.
   */
  public static final String TEMP_TABLE_PREFIX = "temp_";


  public static final String TABLE_PREFIX =
      CommonConstants.DATABASE + "." + CommonConstants.TEMP_TABLE_PREFIX;

  /**
   * 建立webSocket连接时的通行码前缀.
   */
  public static final String WORKSPACE_PREFIX = "workspace-";
}
