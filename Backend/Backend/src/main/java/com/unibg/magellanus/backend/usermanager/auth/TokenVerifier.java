package com.unibg.magellanus.backend.usermanager.auth;

import com.unibg.magellanus.backend.usermanager.User;

public interface TokenVerifier<T> {

	public T verifyToken(String tokenString) throws Exception;

	public User tokenToUser(T token);
}
