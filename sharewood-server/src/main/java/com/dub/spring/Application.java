package com.dub.spring;


import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@SpringBootApplication
public class Application {

	private static final Logger LOG = LoggerFactory.getLogger(Application.class);

	private final Integer connectionPoolSize;

	@Autowired
	public Application(
	@Value("${spring.datasource.maximum-pool-size:10}")
	Integer connectionPoolSize
			) {
		this.connectionPoolSize = connectionPoolSize;
	}

	@Bean
	public Scheduler jdbcScheduler() {
		LOG.info("Creates a jdbcScheduler with connectionPoolSize = " + connectionPoolSize);
		return Schedulers.fromExecutor(Executors.newFixedThreadPool(connectionPoolSize));
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);

		String postgresqlUri = ctx.getEnvironment().getProperty("spring.datasource.url");
		LOG.info("Connected to MySQL: " + postgresqlUri);
	}

}