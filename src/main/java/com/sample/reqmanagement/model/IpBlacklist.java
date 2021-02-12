package com.sample.reqmanagement.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "ip_blacklist")
@Entity
@Data
public class IpBlacklist {
    @Id
    @Column(name = "ip", insertable = false, nullable = false)
    private Integer ip;

    
}