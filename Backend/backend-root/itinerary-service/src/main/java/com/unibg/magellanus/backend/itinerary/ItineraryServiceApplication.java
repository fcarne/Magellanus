package com.unibg.magellanus.backend.itinerary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

/**
 * Microservizio relativo alla gestione degli itinerari. Offre funzionalità di
 * creazione, modifica e rimozione degli itinerari.
 * 
 * @since 0.2
 *
 */
@SpringBootApplication
@EnableEurekaClient
@OpenAPIDefinition(info = @Info(title = "Itinerary API", version = "v1.0", description = "Documentation Itinerary API v1.0"))
@SecurityScheme(name = "bearer-key", scheme = "bearer", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER, bearerFormat = "JWT")
public class ItineraryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItineraryServiceApplication.class, args);
	}

}
