package com.bdilab.dataflowCloud.workspace.execute.service;



public interface DataSetService {






    void createAlwaysSuccessView(String tableName, String viewName);

    void clearDagViews(String workspaceId);

    void clearDagNodeView(String viewName);

}
