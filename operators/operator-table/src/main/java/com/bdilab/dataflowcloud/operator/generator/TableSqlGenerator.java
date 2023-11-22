package com.bdilab.dataflowcloud.operator.generator;

import com.bdilab.dataflowCloud.operator.utils.SqlParseUtils;
import com.bdilab.dataflowcloud.operator.dto.jobdescription.TableDescription;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * generate SQL.

 * @author: gluttony team
 * @create: 2021-09-18
 */
public class TableSqlGenerator  {
  private TableDescription tableDescription;
  String[] project;
  String[] groups;

  public TableSqlGenerator(TableDescription tableDescription) {
    this.tableDescription = tableDescription;
  }

  public String project() {
    project = tableDescription.getProject();
    if (project == null || project.length == 0) {
      return "SELECT * ";
    }
    return "SELECT " + SqlParseUtils.combineWithSeparator(project, ",");

  }

  public String filter() {
    String filter = tableDescription.getFilter();
    if (StringUtils.isEmpty(filter)) {
      return "";
    }
    return " WHERE " + filter;
  }

  public String group() {
    groups = tableDescription.getGroup();
    if (groups == null || groups.length == 0) {
      return "";
    }
    return " GROUP BY " + SqlParseUtils.combineWithSeparator(groups, ",");
  }


  public String generate() {
    return null;
  }

  public String generateLimit(String saveTableName) {
    int page = tableDescription.getPage();
    int offset = (page - 1) * 100;
    return "SELECT * FROM " + saveTableName + " LIMIT " + offset + ", 100";
  }

  public String generateDataSourceSql() throws Exception {
    String projectStr = project();
    String groupStr = group();

    // SQL syntax check
    if (projectStr.contains("*")) {
      projectStr = "SELECT * ";
      groupStr = "";
    }

    if (groups != null && groups.length > 0 && groupStr.length() > 0) {
      StringBuilder sb = new StringBuilder();
      // remove attributes that are not in 'groups' but in 'project'
      Set<String> set = new HashSet<>();
      for (int i = 0; i < this.groups.length; i++) {
        set.add(this.groups[i]);
      }
      for (int i = 0; i < this.project.length; i++) {
        if (set.contains(this.project[i]) || this.project[i].contains("(")) {
          sb.append(this.project[i]).append(",");
        }
      }
      projectStr = "SELECT " + sb.substring(0, sb.length() - 1);
    }

    if (groups == null || groups.length == 0) {
      StringBuilder sb = new StringBuilder();
      if (projectStr.contains("(")) {
        for (int i = 0; i < this.project.length; i++) {
          if (this.project[i].contains("(")) {
            sb.append(this.project[i]).append(",");
          }
        }
        projectStr = "SELECT " + sb.substring(0, sb.length() - 1);
      }
    }

    return projectStr + datasource(0) + filter() + groupStr;
  }

  public String selectCount(String saveTableName) {
    return "SELECT COUNT(*) FROM " + saveTableName;
  }

  public String datasource(int slotIndex) throws Exception {
    // todo Check datasource
    if (tableDescription.getDataSource() == null) {
      throw new Exception();
    }
    return " FROM " + tableDescription.getDataSource()[slotIndex];
  }
}
