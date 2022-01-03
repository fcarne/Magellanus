package com.unibg.magellanus.backend.user;

import org.springframework.data.repository.CrudRepository;

import com.unibg.magellanus.backend.user.model.User;

public interface UserRepository extends CrudRepository<User, String> {
}
