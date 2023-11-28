package com.bdilab.dataflowCloud.operator.dto.jobdescription;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobDescription {
    public String[] dataSource;
    public String nodeDataResult;
}
