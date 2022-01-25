package com.unibg.magellanus.backend.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient
public class GatewayServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayServiceApplication.class, args);
	}
	
	@Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/api/users/**")
                        .uri("lb://USER-SERVICE"))
                .route(r -> r.path("/api/itineraries/**")
                        .uri("lb://ITINERARY-SERVICE"))
                .route(r -> r.path("/api/routes/**")
                        .uri("lb://ROUTE-SERVICE"))
                .build();
    }
}
