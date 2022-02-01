package com.unibg.magellanus.backend.route;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class RouteServiceSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
		.formLogin().disable()
		.httpBasic().disable()
		.authorizeRequests()
		.anyRequest().authenticated()
		.and().oauth2ResourceServer().jwt();
	}
}