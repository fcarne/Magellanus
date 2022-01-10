package com.unibg.magellanus.backend.user.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import com.unibg.magellanus.backend.user.User;

@ExtendWith(MockitoExtension.class)
public class TokenFilterTest {

	@Mock
	TokenVerifier<String> verifier;

	@InjectMocks
	TokenFilter<String> filter;

	@AfterEach
	public void setupSecurityContext() {
		SecurityContextHolder.getContext().setAuthentication(null);
	}
	
	@Test
	public void doFilterInternal_noAuth_nullAuth() throws IOException, ServletException {
		MockHttpServletRequest req = new MockHttpServletRequest();
		MockHttpServletResponse res = new MockHttpServletResponse();
		MockFilterChain chain = new MockFilterChain();

		filter.doFilterInternal(req, res, chain);

		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}
	
	@Test
	public void doFilterInternal_auth_UserAuth() throws Exception {
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.addHeader(TokenFilter.AUTHORIZATION_HEADER, TokenFilter.TOKEN_HEADER + "mock_token");
		MockHttpServletResponse res = new MockHttpServletResponse();
		MockFilterChain chain = new MockFilterChain();

		User user = new User("mock", "mock@test.com"); 
		
		when(verifier.verifyToken(any(String.class))).thenReturn(new String("authenticated!"));
		when(verifier.tokenToUser(any(String.class))).thenReturn(user);

		filter.doFilterInternal(req, res, chain);

		assertEquals(SecurityContextHolder.getContext().getAuthentication().getPrincipal(), user);
		assertEquals(SecurityContextHolder.getContext().getAuthentication().getCredentials(), "authenticated!");
	}

}
