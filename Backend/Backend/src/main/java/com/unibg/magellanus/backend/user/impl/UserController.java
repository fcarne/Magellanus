package com.unibg.magellanus.backend.user.impl;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.unibg.magellanus.backend.user.UserAccountAPI;
import com.unibg.magellanus.backend.user.UserService;
import com.unibg.magellanus.backend.user.model.User;

@RestController()
@RequestMapping("users")
public class UserController implements UserAccountAPI {

	UserService service;

	@Autowired
	public UserController(UserService service) {
		this.service = service;
	}

	@Override
	@RequestMapping(method = RequestMethod.HEAD, value = "{uid}")
	public ResponseEntity<Void> checkIfExists(@PathVariable String uid) {
		if (service.checkIfExists(uid))
			return ResponseEntity.ok().build();
		else
			return ResponseEntity.notFound().build();
	}

	@Override
	@PostMapping
	public ResponseEntity<Void> signUp(@RequestBody User user) {
		System.out.println(user.getUid());
		System.out.println(user.getEmail());

		service.signUp(user);
		return ResponseEntity.noContent().build();
	}

	@Override
	@DeleteMapping("{uid}")
	public ResponseEntity<Void> delete(@PathVariable String uid) {
		if (!getUserFromContext().getUid().equals(uid))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,
					"Sorry, but you can't delete other user accounts!!!");
		service.delete(uid);
		return ResponseEntity.noContent().build();
	}

	@Override
	@PatchMapping("{uid}/preferences")
	public ResponseEntity<Void> updatePreferences(@PathVariable String uid,
			@RequestBody Map<String, Object> preferences) {
		if (!getUserFromContext().getUid().equals(uid))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sorry, but you can't set other user preferences");
		
		Map<String, Object> sanitized = preferences.entrySet().stream().filter(t -> !t.getKey().contains("."))
				.collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
		service.updatePreferences(uid, sanitized);
		return ResponseEntity.noContent().build();
	}

	@Override
	@GetMapping("{uid}/preferences")
	public Map<String, Object> getPreferences(@PathVariable String uid) {
		if (!getUserFromContext().getUid().equals(uid))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sorry, but you can't get other user preferences");

		Map<String, Object> map = service.getPreferences(uid);
		return map;
	}

	private User getUserFromContext() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (User.class.isAssignableFrom(principal.getClass()))
			return (User) principal;
		else
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
					"User is not logged with credentials (weird, security filter should have already unauthorized)");
	}

}
