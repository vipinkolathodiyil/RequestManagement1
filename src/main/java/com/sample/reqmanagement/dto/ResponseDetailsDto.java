package com.sample.reqmanagement.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class ResponseDetailsDto {
    Integer id;
    Integer customerId;
    Long invalidCount;
    Long requestCount;
    Timestamp time;
}
