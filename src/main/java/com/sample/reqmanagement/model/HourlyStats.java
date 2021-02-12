package com.sample.reqmanagement.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Table(name = "hourly_stats")
@Entity
@Data
public class HourlyStats {
    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name="customer_id")
    private Customer customer;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, nullable = false)
    private Integer id;

    @Column(name = "invalid_count", nullable = false)
    private Long invalidCount = 0L;

    @Column(name = "request_count", nullable = false)
    private Long requestCount = 0L;

    @Column(name = "time", nullable = false)
    private Timestamp time;

    
}