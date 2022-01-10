package com.unibg.magellanus.backend.user.auth;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.unibg.magellanus.backend.user.User;

@Component
public class TokenFilter<T> extends OncePerRequestFilter {

	Logger logger = LoggerFactory.getLogger(TokenFilter.class);

	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String TOKEN_HEADER = "Bearer ";

	private final TokenVerifier<T> verifier;

	@Autowired
	public TokenFilter(TokenVerifier<T> verifier) {
		this.verifier = verifier;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String tokenString = extractToken(request);
				
		T decodedToken = null;
		try {
			if (tokenString != null && !tokenString.equalsIgnoreCase("undefined"))
				decodedToken = verifier.verifyToken(tokenString);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Filter Exception:: ", e.getLocalizedMessage());
		}
		User user = verifier.tokenToUser(decodedToken);
		if (user != null) {
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user,
					decodedToken, null);
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
				
		filterChain.doFilter(request, response);
	}

	private String extractToken(HttpServletRequest request) {
		String authorization = request.getHeader(AUTHORIZATION_HEADER);
		if (StringUtils.hasText(authorization) && authorization.startsWith(TOKEN_HEADER))
			return authorization.substring(TOKEN_HEADER.length());
		else
			return null;
	}

}
