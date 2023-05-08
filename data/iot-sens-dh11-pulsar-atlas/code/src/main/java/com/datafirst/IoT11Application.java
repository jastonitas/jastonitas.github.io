package com.datafirst;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class IoT11Application {

	@Autowired
	private SerialDataProducer producer;

	public static void main(String[] args) {
		SpringApplication.run(IoT11Application.class, args);
	}

	@Bean
	ApplicationRunner runner() {
		return (args) -> producer.readAndProduce();
	}
}