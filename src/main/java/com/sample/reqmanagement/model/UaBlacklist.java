package com.sample.reqmanagement.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "ua_blacklist")
@Entity
@Data
public class UaBlacklist {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ua", insertable = false, nullable = false)
    private String userAgent;

    
}