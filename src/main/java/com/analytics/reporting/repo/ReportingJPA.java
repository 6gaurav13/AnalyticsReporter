package com.analytics.reporting.repo;

import com.analytics.reporting.entity.AnalyticsConsumer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportingJPA extends JpaRepository<AnalyticsConsumer,Long> {

}
