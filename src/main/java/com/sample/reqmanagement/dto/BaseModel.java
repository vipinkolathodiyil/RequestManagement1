package com.sample.reqmanagement.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseModel {

    private static final Logger logger = LoggerFactory.getLogger(BaseModel.class);

    @Override
    public String toString() {
        String resp = null;
        ObjectMapper mapper = new ObjectMapper();
        try{
            resp = mapper.writeValueAsString(this);
        }
        catch(JsonProcessingException ex){
            logger.warn("Exception while trying to convert object to Json", ex);
        }
        return resp;
    }
}
