package com.bdilab.dataflowCloud.workspace.exexute.service.impl;

import com.bdilab.dataflowCloud.clickhouseClient.ClickhouseManagerClient;
import com.bdilab.dataflowCloud.workspace.dag.pojo.Dag;
import com.bdilab.dataflowCloud.workspace.dag.pojo.DagNode;
import com.bdilab.dataflowCloud.workspace.dag.service.DagService;
import com.bdilab.dataflowCloud.workspace.exexute.service.DataSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class DataSetServiceImpl implements DataSetService {
    @Autowired
    ClickhouseManagerClient clickhouseManagerClient;

    @Autowired
    DagService dagService;
    @Override
    public void clearDagViews(String workspaceId) {
        List<String> viewNames = new ArrayList<>();

        Dag dag = dagService.getDag(workspaceId);

        for (Map.Entry<String,DagNode> entry: dag.getDagMap().entrySet()) {
            viewNames.add(entry.getValue().getNodeDataResult());
        }
        clickhouseManagerClient.batchDeleteViews(viewNames);
    }

    @Override
    public void clearDagNodeView(String viewName) {
        List<String> viewNames = new ArrayList<>();
        viewNames.add(viewName);
        clickhouseManagerClient.batchDeleteViews(viewNames);
    }
}
