package com.unibg.magellanus.backend.user.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.google.firebase.auth.FirebaseToken;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	TokenFilter<FirebaseToken> tokenFilter;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
			.formLogin().disable()
			.httpBasic().disable()
			.authorizeRequests()
			.antMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
			.antMatchers(HttpMethod.POST, "/api/users/").permitAll()
			.antMatchers(HttpMethod.HEAD, "/api/users/*").permitAll()
			.antMatchers("/api/**").authenticated()
			.and().addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class)
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}
}
