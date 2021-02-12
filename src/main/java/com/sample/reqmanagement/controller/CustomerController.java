package com.sample.reqmanagement.controller;

import com.sample.reqmanagement.dto.RequestDetailsDto;
import com.sample.reqmanagement.dto.ResponseDetailsDto;
import com.sample.reqmanagement.service.ICustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/request-management")
public class CustomerController {
    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    ICustomerService iCustomerService;

    private String methodprfx = "CustomerController>>>";

    @RequestMapping(value = "/insert-customer-request",consumes = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.POST)
    public List<ResponseDetailsDto> insertCustomerRequest(@RequestBody RequestDetailsDto requestDetailsDto){
        log.info(methodprfx+"insertCustomerRequest");
        List<ResponseDetailsDto> responseDetailsDtoList = iCustomerService.saveRequestDetails(requestDetailsDto);
        log.info("Returning  status "+responseDetailsDtoList.toString());
        return responseDetailsDtoList;
    }
}

