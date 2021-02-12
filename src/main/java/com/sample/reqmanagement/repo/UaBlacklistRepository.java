package com.sample.reqmanagement.repo;

import com.sample.reqmanagement.model.UaBlacklist;
import org.springframework.data.repository.CrudRepository;

public interface UaBlacklistRepository extends CrudRepository<UaBlacklist, String> {

    Boolean existsByUserAgent(String userAgent);
}