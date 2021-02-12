package com.sample.reqmanagement.repo;

import com.sample.reqmanagement.model.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Integer> {

    Customer findByIdAndActive(Integer id,Integer active);

}