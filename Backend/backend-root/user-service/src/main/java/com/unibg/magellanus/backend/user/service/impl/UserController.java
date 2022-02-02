package com.unibg.magellanus.backend.user.service.impl;

import java.util.Map;
import java.util.NoSuchElementException;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.unibg.magellanus.backend.user.service.User;
import com.unibg.magellanus.backend.user.service.UserAccountAPI;
import com.unibg.magellanus.backend.user.service.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/")
@SecurityRequirement(name = "bearer-key")
public class UserController implements UserAccountAPI {

	UserService service;

	@Autowired
	public UserController(UserService service) {
		this.service = service;
	}

	@Override
	@GetMapping("{uid}")
	public User getUser(@PathVariable String uid) {
		try {
			return service.get(uid);
		} catch (NoSuchElementException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@Override
	@PostMapping
	public ResponseEntity<Void> signUp(@RequestBody User user) {
		try {
			service.signUp(user);
			return ResponseEntity.noContent().build();
		} catch (IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
		}
	}

	@Override
	@DeleteMapping("me")
	public ResponseEntity<Void> deleteMe() {
		String uid = SecurityContextHolder.getContext().getAuthentication().getName();
		service.delete(uid);
		return ResponseEntity.noContent().build();
	}

	@Override
	@PatchMapping("me/preferences")
	public ResponseEntity<Void> updateMyPreferences(@RequestBody Map<String, Object> preferences) {
		String uid = SecurityContextHolder.getContext().getAuthentication().getName();
		Map<String, Object> sanitized = preferences.entrySet().stream().filter(t -> !t.getKey().contains("."))
				.collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
		try {
			service.updatePreferences(uid, sanitized);
			return ResponseEntity.noContent().build();
		} catch (NoSuchElementException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@Override
	@GetMapping("me/preferences")
	public Map<String, Object> getMyPreferences() {
		String uid = SecurityContextHolder.getContext().getAuthentication().getName();
		try {
			Map<String, Object> map = service.getPreferences(uid);
			return map;
		} catch (NoSuchElementException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}
}
