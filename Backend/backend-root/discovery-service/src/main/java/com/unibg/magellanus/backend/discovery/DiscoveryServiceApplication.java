package com.unibg.magellanus.backend.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Si occupa di attivare e gestire il microservizio di discovery tramite un
 * Eureka server. 
 * 
 * Una volta attivato, è disponibile un endpoint all'indirizzo
 * <code>localhost:8761</code> dove poter visualizzare le istanze collegate e
 * gestite da Eureka, oltre che avere report statistici.
 * 
 * @since 0.2
 */
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiscoveryServiceApplication.class, args);
	}

}
