package com.petrmacek.cragdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication(scanBasePackages = {"com.petrmacek.cragdb"})
@EnableNeo4jRepositories
public class CragDbApplication {

	public static void main(String[] args) {
		SpringApplication.run(CragDbApplication.class, args);
	}

}
