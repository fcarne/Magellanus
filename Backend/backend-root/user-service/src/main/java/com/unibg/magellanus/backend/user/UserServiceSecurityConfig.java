package com.unibg.magellanus.backend.user;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class UserServiceSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
		.formLogin().disable()
		.httpBasic().disable()
		.authorizeRequests()
		.antMatchers(HttpMethod.POST, "/api/users/").permitAll()
		.antMatchers(HttpMethod.GET, "/api/users/*").permitAll()
		.antMatchers(HttpMethod.HEAD, "/api/users/*").permitAll()
		.antMatchers("/api/users/**").authenticated()
		.anyRequest().permitAll()
		.and().oauth2ResourceServer().jwt();
	}
}