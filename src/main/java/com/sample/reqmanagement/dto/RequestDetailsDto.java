package com.sample.reqmanagement.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter @Setter
public class RequestDetailsDto extends BaseModel {
    Integer customerID;
    Integer tagID;
    String userID;
    String remoteIP;
    Timestamp timestamp;
}
