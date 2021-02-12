package com.sample.reqmanagement.service;

import com.sample.reqmanagement.dto.RequestDetailsDto;
import com.sample.reqmanagement.dto.ResponseDetailsDto;

import java.util.List;

public interface ICustomerService {
    List<ResponseDetailsDto> saveRequestDetails(RequestDetailsDto requestDetailsDto);
}
