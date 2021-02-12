package com.sample.reqmanagement.mapper;

import com.sample.reqmanagement.dto.ResponseDetailsDto;
import com.sample.reqmanagement.model.HourlyStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class DetailMapper {
    private static final Logger log = LoggerFactory.getLogger(DetailMapper.class);

    public static ResponseDetailsDto toResponseDto(HourlyStats hourlyStats) {
        ResponseDetailsDto responseDto = new ResponseDetailsDto();
        responseDto.setId(hourlyStats.getId());
        responseDto.setCustomerId(hourlyStats.getCustomer().getId());
        responseDto.setTime(hourlyStats.getTime());
        responseDto.setRequestCount(hourlyStats.getRequestCount());
        responseDto.setInvalidCount(hourlyStats.getInvalidCount());
        return responseDto;
    }

    public static List<ResponseDetailsDto> toResponseDtoList(List<HourlyStats> source) {

        return source
                .stream()
                .map(element -> toResponseDto(element))
                .collect(Collectors.toList());
    }
}
