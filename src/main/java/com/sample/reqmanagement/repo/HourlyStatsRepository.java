package com.sample.reqmanagement.repo;

import com.sample.reqmanagement.model.Customer;
import com.sample.reqmanagement.model.HourlyStats;
import org.springframework.data.repository.CrudRepository;

public interface HourlyStatsRepository extends CrudRepository<HourlyStats, Integer> {

    HourlyStats findByCustomer(Customer customer);
}