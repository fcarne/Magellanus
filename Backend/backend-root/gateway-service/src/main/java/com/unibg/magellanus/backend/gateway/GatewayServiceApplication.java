package com.unibg.magellanus.backend.gateway;

import java.util.ArrayList;
import java.util.List;

import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.SwaggerUiConfigParameters;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

/**
 * Attiva l'API Gateway che si occupa di fare rerouting delle richieste http in
 * arrivo all'indirizzo <code>localhost:8080/</code>. Le route definite sono
 * disponibili nel file application.yml.
 * 
 * Per semplicità la documentazione di tutte le API sono reperibili tramite
 * questo gateway all'indirizzo <code>localhost:8080/swagger-ui.html</code>
 * 
 * @since 0.2
 */
@SpringBootApplication
@EnableEurekaClient
public class GatewayServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayServiceApplication.class, args);
	}

	/**
	 * Crea la lista di API documentate raggiungibili da questo endpoint. Codice
	 * preso dai <a href=
	 * "https://github.com/springdoc/springdoc-openapi-demos/blob/master/springdoc-openapi-microservices/gateway-service/src/main/java/org/springdoc/demo/services/gateway/GatewayApplication.java">sample</a>
	 * di Springdoc.
	 * 
	 * @param swaggerUiConfigParameters i parametri di configurazione di Swagger
	 * @param locator                   le route definite dal gateway
	 * @return la lista di API documentate raggiungibili dal gateway
	 */
	@Bean
	@Lazy(false)
	public List<GroupedOpenApi> apis(SwaggerUiConfigParameters swaggerUiConfigParameters,
			RouteDefinitionLocator locator) {
		List<GroupedOpenApi> groups = new ArrayList<>();
		List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();
		for (RouteDefinition definition : definitions) {
			System.out.println("id: " + definition.getId() + "  " + definition.getUri().toString());
		}
		definitions.stream().filter(routeDefinition -> routeDefinition.getId().matches(".*-service"))
				.forEach(routeDefinition -> {
					String name = routeDefinition.getId();
					swaggerUiConfigParameters.addGroup(name);
					GroupedOpenApi.builder().pathsToMatch("/" + name + "/**").group(name).build();
				});
		return groups;
	}
}
