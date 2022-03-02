package com.unibg.magellanus.backend.user.service;

import org.springframework.data.repository.CrudRepository;

/**
 * Repository che si interfaccia con le istanze di MongoDB Atlas. Le opzioni di
 * connessione sono specificate nel file application-dev.yml.
 * 
 * L'oggetto concreto viene costruito da Spring.
 * 
 * @since 0.1
 *
 */
public interface UserRepository extends CrudRepository<User, String> {
}
