package com.unibg.magellanus.backend.itinerary;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class ItineraryServiceSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
		.formLogin().disable()
		.httpBasic().disable()
		.authorizeRequests()
		.mvcMatchers(HttpMethod.GET, "/v3/api-docs").permitAll()
		.anyRequest().authenticated()
		.and().oauth2ResourceServer().jwt();
	}
}