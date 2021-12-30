package com.unibg.magellanus.backend.usermanager;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
	Long deleteByEmail(String email);

	List<User> findByEmail(String email);
}
