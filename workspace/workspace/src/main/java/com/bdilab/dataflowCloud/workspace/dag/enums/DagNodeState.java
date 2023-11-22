package com.bdilab.dataflowCloud.workspace.dag.enums;

public enum DagNodeState{
    WAIT("wait"),
    READY("ready"),
    SUCCEED("succeed"),
    FAILED("failed");


    private final String dagNodeState;

    private DagNodeState(String dagNodeState) {
        this.dagNodeState = dagNodeState;
    }


}
