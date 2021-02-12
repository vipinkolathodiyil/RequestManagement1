package com.sample.reqmanagement.repo;

import com.sample.reqmanagement.model.IpBlacklist;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IpBlacklistRepository extends CrudRepository<IpBlacklist, Integer> {
    Boolean existsByIp(Integer ip);
}