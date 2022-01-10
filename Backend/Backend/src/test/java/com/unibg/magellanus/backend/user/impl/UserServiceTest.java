package com.unibg.magellanus.backend.user.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.unibg.magellanus.backend.user.User;
import com.unibg.magellanus.backend.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	User user;

	@Mock
	UserRepository repository;

	@InjectMocks
	UserServiceImpl service;

	@BeforeEach
	void setupUser() {
		user = new User("1", "test@test.com");
	}

	@Test
	void checkIfExists_exists_returnsTrue() {
		when(repository.existsById("1")).thenReturn(true);

		boolean exists = service.checkIfExists("1");
		assertTrue(exists);
	}

	@Test
	void checkIfExists_notExists_returnsFalse() {
		when(repository.existsById("2")).thenReturn(false);

		boolean exists = service.checkIfExists("2");
		assertFalse(exists);
	}

	@Test
	void signUp_nonExistent_returnsUser() {
		when(repository.findById(user.getUid())).thenReturn(Optional.empty());
		when(repository.save(user)).thenReturn(new User(user.getUid(), user.getEmail()));

		User newUser = service.signUp(user);
		assertEquals(newUser, user);
	}

	@Test
	void signUp_alreadyExistent_throwsIllegalArgument() {
		when(repository.findById(user.getUid())).thenReturn(Optional.of(user));
		assertThrows(IllegalArgumentException.class, () -> service.signUp(user));
	}

	@Test
	void testUpdatePreferences() {
		Map<String, Object> prefs = new HashMap<>();
		prefs.put("A", 1);
		prefs.put("B", 1);
		
		when(repository.findById(user.getUid())).thenReturn(Optional.of(user));
		when(repository.save(user)).thenReturn(user);
		
		User newUser = service.updatePreferences(user.getUid(), prefs);
		assertThat(newUser.getPreferences(), is(prefs));
		
	}

	@Test
	void setPreferences_nonExistent_throwsNoSuchElement() {
		when(repository.findById(user.getUid())).thenReturn(Optional.empty());
		assertThrows(NoSuchElementException.class, () -> service.updatePreferences(user.getUid(), new HashMap<>()));
	}
	
	@Test
	void getPreferences_existent_returnsPreferences() {
		Map<String, Object> prefs = new HashMap<>();
		prefs.put("A", 1);
		prefs.put("B", 1);
		user.setPreferences(prefs);
		when(repository.findById(user.getUid())).thenReturn(Optional.of(user));

		Map<String, Object> userPrefs = service.getPreferences(user.getUid());

		assertThat(userPrefs, is(prefs));
	}

	@Test
	void getPreferences_nonExistent_throwsNoSuchElement() {
		when(repository.findById(user.getUid())).thenReturn(Optional.empty());
		assertThrows(NoSuchElementException.class, () -> service.getPreferences(user.getUid()));
	}

}
