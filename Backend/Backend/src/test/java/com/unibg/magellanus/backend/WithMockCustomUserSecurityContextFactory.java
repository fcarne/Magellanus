package com.unibg.magellanus.backend;

import static org.mockito.ArgumentMatchers.anyCollection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.unibg.magellanus.backend.user.model.User;

public class WithMockCustomUserSecurityContextFactory
implements WithSecurityContextFactory<WithMockCustomUser> {
@Override
public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
	SecurityContext context = SecurityContextHolder.createEmptyContext();

	User principal =
		new User(customUser.uid(), customUser.email());
	Authentication auth =
		new UsernamePasswordAuthenticationToken(principal, "password", anyCollection());
	context.setAuthentication(auth);
	return context;
}
}
