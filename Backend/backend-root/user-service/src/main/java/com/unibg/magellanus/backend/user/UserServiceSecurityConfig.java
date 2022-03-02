package com.unibg.magellanus.backend.user;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


/**
 * Configura la security e i filtri per il <code>user-service</code>.
 * 
 * @since 0.1
 *
 */

@Configuration
public class UserServiceSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
		.formLogin().disable()
		.httpBasic().disable()
		.authorizeRequests()
		.antMatchers(HttpMethod.POST, "/").permitAll() // creazione utente, non serve avere un token
		.antMatchers(HttpMethod.HEAD, "/*").permitAll() // controllo di esistenza utente, non serve avere un token
		.mvcMatchers(HttpMethod.GET, "/v3/*").permitAll() // documentazione api
		.anyRequest().authenticated()
		.and().oauth2ResourceServer().jwt(); // setta il tipo di token utilizzato
	}
}