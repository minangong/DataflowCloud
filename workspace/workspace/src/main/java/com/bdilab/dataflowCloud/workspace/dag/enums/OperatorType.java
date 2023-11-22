package com.bdilab.dataflowCloud.workspace.dag.enums;

public enum OperatorType {
    Immediate("immediate"),
    Delayed("Delayed");

    private String operatorType;

    OperatorType(String operatorType) {
        this.operatorType = operatorType;
    }
}
