package com.unibg.magellanus.backend.itinerary;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Configura la security e i filtri per l'<code>itinerary-service</code>.
 * 
 * @since 0.2
 *
 */
@Configuration
public class ItineraryServiceSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
		.formLogin().disable()
		.httpBasic().disable()
		.authorizeRequests()
		.mvcMatchers(HttpMethod.GET, "/v3/*").permitAll() // documentazione api
		.anyRequest().authenticated()
		.and().oauth2ResourceServer().jwt(); // setta il tipo di token utilizzato
	}
}