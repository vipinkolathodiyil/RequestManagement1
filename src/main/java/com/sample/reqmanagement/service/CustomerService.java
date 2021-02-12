package com.sample.reqmanagement.service;

import com.sample.reqmanagement.amqp.RequestManagementPublisher;
import com.sample.reqmanagement.dto.RequestDetailsDto;
import com.sample.reqmanagement.dto.ResponseDetailsDto;
import com.sample.reqmanagement.mapper.DetailMapper;
import com.sample.reqmanagement.model.Customer;
import com.sample.reqmanagement.model.HourlyStats;
import com.sample.reqmanagement.repo.CustomerRepository;
import com.sample.reqmanagement.repo.HourlyStatsRepository;
import com.sample.reqmanagement.repo.IpBlacklistRepository;
import com.sample.reqmanagement.repo.UaBlacklistRepository;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustomerService implements ICustomerService {
    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    private String QUEUE_CONTENT_TYPE = "application/json";
    private String DEFAULT_CHARSET = "UTF-8";
    Timestamp requestsTime;
    DateTime lastModified = new DateTime(System.currentTimeMillis());

    HashMap<Integer, HourlyStats> customerHourlyStatsMap = new HashMap<>();

    @Autowired
    RequestManagementPublisher requestManagementPublisher;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    IpBlacklistRepository ipBlacklistRepository;

    @Autowired
    UaBlacklistRepository uaBlacklistRepository;

    @Autowired
    HourlyStatsRepository hourlyStatsRepository;

    private Message getRoutingMessage(RequestDetailsDto requestDetailsDto) throws UnsupportedEncodingException {
        MessageProperties props = new MessageProperties();
        props.setContentType(QUEUE_CONTENT_TYPE);
        Message message = new Message(requestDetailsDto.toString().getBytes(DEFAULT_CHARSET), props);
        return message;
    }

    @Override
    public List<ResponseDetailsDto> saveRequestDetails(RequestDetailsDto requestDetailsDto) {
        //String status = "Invalid Request";
        List<ResponseDetailsDto> responseDetailsDtoList = null;
        /*
        //For testing

        Timestamp requestTime = new Timestamp(System.currentTimeMillis());
        requestDetailsDto.setTimestamp(requestTime);
        requestsTime = requestTime;
        log.info("---------------" + requestsTime);
        */
        Boolean isValid = validRequest(requestDetailsDto);
        if (isValid) {
            Message message = null;
            try {
                message = getRoutingMessage(requestDetailsDto);
                requestManagementPublisher.publishMessageForProcessing(message);
                responseDetailsDtoList = consumingFromQueue(requestDetailsDto);
            } catch (UnsupportedEncodingException e) {
                log.error("Error on building the Message for Publishing" + e);
            }
        } 
        return responseDetailsDtoList;
    }

    public Boolean validRequest(RequestDetailsDto requestDetailsDto) {
        Boolean isValid = Boolean.FALSE;

        if (requestDetailsDto.getCustomerID() != null) {
            Customer customer = customerRepository.findByIdAndActive(requestDetailsDto.getCustomerID(), 1);
            HourlyStats hourlyStats = null;
            if (customer != null) {
                log.info("Found an active customer details, ID - " + customer.getId());
                hourlyStats = hourlyStatsRepository.findByCustomer(customer);
                if (hourlyStats == null && customerHourlyStatsMap.get(customer.getId()) == null) {
                    log.info("Not Existing in Map or DB!");
                    hourlyStats = new HourlyStats();
                    hourlyStats.setId(null);
                    hourlyStats.setTime(requestDetailsDto.getTimestamp());
                    hourlyStats.setCustomer(customer);
                    hourlyStats.setRequestCount(1L);
                    hourlyStats.setInvalidCount(0L);

                } else if (customerHourlyStatsMap.get(customer.getId()) != null) {
                    log.info("Existing in Map; but not in DB  || Existing in Map & DB ");
                    if (hourlyStats == null) {
                        hourlyStats = new HourlyStats();
                        hourlyStats.setId(null);
                        hourlyStats.setCustomer(customer);
                    }
                    hourlyStats.setTime(requestDetailsDto.getTimestamp());
                    hourlyStats.setRequestCount(customerHourlyStatsMap.get(customer.getId()).getRequestCount() + 1);

                } else {
                    log.info("Not Existing in Map; but in DB!");
                    hourlyStats.setRequestCount(hourlyStats.getRequestCount() + 1);
                    hourlyStats.setTime(requestDetailsDto.getTimestamp());
                }

                log.info("<<<<<<< Request count " + hourlyStats.getRequestCount());
                if (requestDetailsDto.getCustomerID() != null && requestDetailsDto.getTagID() != null && requestDetailsDto.getRemoteIP() != null &&
                        requestDetailsDto.getUserID() != null && requestDetailsDto.getTimestamp() != null) {
                    log.info("Not missing any fields!");
                    String input = requestDetailsDto.getRemoteIP().replace(".", "");
                    Integer ip = Integer.parseInt(input);
                    log.info("INTEGER IP - " + ip);
                    if (!ipBlacklistRepository.existsByIp(ip)) {
                        log.info("Valid IP (Not in blacklist)");
                        if (!uaBlacklistRepository.existsByUserAgent(requestDetailsDto.getUserID())) {
                            log.info("Valid User Agent (Given userId Not in uablacklist)");
                            isValid = Boolean.TRUE;
                            if (customerHourlyStatsMap.get(customer.getId()) != null) {
                                log.info("invalid req count before - " + customerHourlyStatsMap.get(customer.getId()).getInvalidCount());
                                hourlyStats.setInvalidCount(customerHourlyStatsMap.get(customer.getId()).getInvalidCount());
                                log.info("invalid req count after - " + hourlyStats.getInvalidCount());
                            } else {
                                hourlyStats.setInvalidCount(hourlyStats.getInvalidCount());
                                log.info("invalid req count after - " + hourlyStats.getInvalidCount());
                            }

                        } else {
                            log.info("Invalid UA ");
                            if (customerHourlyStatsMap.get(customer.getId()) != null) {
                                log.info("invalid req count before - " + customerHourlyStatsMap.get(customer.getId()).getInvalidCount());
                                hourlyStats.setInvalidCount(customerHourlyStatsMap.get(customer.getId()).getInvalidCount() + 1);
                                log.info("invalid req count after - " + hourlyStats.getInvalidCount());
                            } else {
                                hourlyStats.setInvalidCount(hourlyStats.getInvalidCount() + 1);
                            }
                        }
                    } else {
                        log.info("Invalid IP ");
                        if (customerHourlyStatsMap.get(customer.getId()) != null) {
                            log.info("invalid req count before - " + customerHourlyStatsMap.get(customer.getId()).getInvalidCount());
                            hourlyStats.setInvalidCount(customerHourlyStatsMap.get(customer.getId()).getInvalidCount() + 1);
                            log.info("invalid req count after - " + hourlyStats.getInvalidCount());
                        } else {
                            hourlyStats.setInvalidCount(hourlyStats.getInvalidCount() + 1);
                        }
                    }
                } else {
                    if (customerHourlyStatsMap.get(customer.getId()) != null) {
                        log.info("invalid req count before - " + customerHourlyStatsMap.get(customer.getId()).getInvalidCount());
                        hourlyStats.setInvalidCount(customerHourlyStatsMap.get(customer.getId()).getInvalidCount() + 1);
                        log.info("invalid req count after - " + hourlyStats.getInvalidCount());
                    } else {
                        hourlyStats.setInvalidCount(hourlyStats.getInvalidCount() + 1);
                    }
                }
                if (customerHourlyStatsMap.containsKey(customer.getId())) {
                    customerHourlyStatsMap.replace(customer.getId(), hourlyStats);
                } else {
                    customerHourlyStatsMap.put(customer.getId(), hourlyStats);
                }
                if (timeIntervalCheck(requestDetailsDto.getTimestamp())) {
                    List<HourlyStats> hourlyStatsList = (List) hourlyStatsRepository.findAll();
                    for (HourlyStats hourlyStatus : hourlyStatsList) {
                        for (Map.Entry m : customerHourlyStatsMap.entrySet()) {
                            log.info("Key - " + m.getKey());
                            HourlyStats hourlyStatsMapValue = (HourlyStats) m.getValue();
                            if (hourlyStatsMapValue.getCustomer() == hourlyStatus.getCustomer()) {
                                hourlyStatus.setRequestCount(hourlyStatsMapValue.getRequestCount());
                                hourlyStatus.setInvalidCount(hourlyStatsMapValue.getInvalidCount());
                                hourlyStatsRepository.save(hourlyStats);
                                log.info("removing key - " + m.getKey());
                                customerHourlyStatsMap.remove(m.getKey());
                            }
                        }
                    }
                    if (!customerHourlyStatsMap.isEmpty()) {
                        log.info("-------not empty map-------");
                        for (Map.Entry m : customerHourlyStatsMap.entrySet()) {
                            log.info("Key - " + m.getKey());
                            HourlyStats hourlyStatsMapValue = (HourlyStats) m.getValue();
                            hourlyStatsRepository.save(hourlyStatsMapValue);
                            log.info("removing key - " + m.getKey());
                            customerHourlyStatsMap.remove(m.getKey());
                        }
                    }
                    lastModified = new DateTime(System.currentTimeMillis());
                }

            } else {
                log.info("No such customer found!");
            }
        } else {
            log.info("No customer ID received!");
        }
        return isValid;
    }

    private Boolean timeIntervalCheck(Timestamp requestPassingTime) {
        Boolean timeToModify = Boolean.FALSE;
        DateTime requestPassingDTime = new DateTime(requestPassingTime.getTime());
        log.info("Request Passing Time : " + requestPassingDTime);
        Period p = new Period(lastModified, requestPassingDTime);
        int hours = p.getHours();
        if (hours >= 1) {
            log.info("greater than 1hr");
            timeToModify = true;
        }
        return timeToModify;
    }

    public List<ResponseDetailsDto> consumingFromQueue(RequestDetailsDto requestDetailsDto) {

        System.out.println("Message : " + requestDetailsDto);
        List<ResponseDetailsDto> responseDetailsDtoList = null;
        try {
            log.info("### consuming Details ");
            List<HourlyStats> hourlyStatsList = (List)hourlyStatsRepository.findAll();
             if(hourlyStatsList.size()>0){
                 responseDetailsDtoList = DetailMapper.toResponseDtoList(hourlyStatsList);
            }
        } catch (Exception e) {
            log.error("Exception occured while retrieving details", e);
        }
        return responseDetailsDtoList;
    }
    
}