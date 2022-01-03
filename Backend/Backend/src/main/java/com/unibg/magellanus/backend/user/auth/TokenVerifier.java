package com.unibg.magellanus.backend.user.auth;

import com.unibg.magellanus.backend.user.model.User;

public interface TokenVerifier<T> {

	public T verifyToken(String tokenString) throws Exception;

	public User tokenToUser(T token);
}
