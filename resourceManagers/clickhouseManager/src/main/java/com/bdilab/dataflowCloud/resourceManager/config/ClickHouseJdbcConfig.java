package com.bdilab.dataflowCloud.resourceManager.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.clickhouse.ClickHouseDataSource;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * ClickHouse JdbcTemplate.
 *
 * @author wh
 * @date 2022/04/28
 */
@Configuration
public class ClickHouseJdbcConfig {
  @Autowired
  JdbcTemplate jdbcTemplate;

  @Bean(name = "ClickhouseDataSource")
  @Qualifier("primaryDataSource")
  @ConfigurationProperties(prefix = "spring.datasource")
  public DataSource primaryDataSource() {
    return DataSourceBuilder.create().build();
  }


  @Bean(name = "clickHouseJdbcTemplate")
  public JdbcTemplate getClickHouseJdbcTemplate(@Qualifier("primaryDataSource") DataSource dataSource){
    return new JdbcTemplate(dataSource);
  }
}
