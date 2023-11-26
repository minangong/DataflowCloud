package com.bdilab.dataflowCloud.workspace.dag.enums;

public enum DagNodeState{
    WAIT("wait"),
    READY("ready"),
    SUCCEED("succeed"),
    FAILED("failed"),
    ALWAYS_SUCCEED("always_succeed");   //数据源 永远succeed


    private final String dagNodeState;

    private DagNodeState(String dagNodeState) {
        this.dagNodeState = dagNodeState;
    }

    public boolean isSuccess(){
        return this == SUCCEED || this == ALWAYS_SUCCEED;
    }

    public boolean isReady(){
        return this == READY;
    }

}
