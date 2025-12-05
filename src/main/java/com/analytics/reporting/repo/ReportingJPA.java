package com.analytics.reporting.repo;

import com.analytics.reporting.entity.AnalyticsConsumer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportingJPA extends JpaRepository<AnalyticsConsumer,Long> {

    //Returns total_users for the app
    long countByAppId(String appId);

    //Returns unique user for the app
    @Query("Select Count(distinct a.userId) from AnalyticsConsumer a where a.appId=:appId")
    long countUniqueUsers(String appId);

    //will be used to find each app's login via "mobile" and via "desktop"
    long countByAppIdAndDeviceType(String appId, String deviceType);

    //used to find number of users per event
    @Query("Select a.eventType,count(a) from AnalyticsConsumer a where a.appId=:appId group by a.eventType")
    List<Object[]> countUsersPerEvent(String appId);
}
