package com.unibg.magellanus.backend.usermanager.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unibg.magellanus.backend.usermanager.User;
import com.unibg.magellanus.backend.usermanager.UserAccountAPI;
import com.unibg.magellanus.backend.usermanager.UserService;

@RestController()
@RequestMapping("user")
public class UserController implements UserAccountAPI {

	UserService service;

	@Autowired
	public UserController(UserService service) {
		this.service = service;
	}

	@Override
	@PostMapping("signup")
	public ResponseEntity<String> signUp(@RequestBody User user) {
		System.out.println(user.getId());
		System.out.println(user.getEmail());
		if (service.signUp(user) != null)
			return ResponseEntity.ok().build();
		else
			return ResponseEntity.unprocessableEntity().body("Could not create user: already created!");
	}

	@Override
	@PostMapping("preferences")
	public ResponseEntity<String> updatePreferences(@RequestBody Map<String, Object> preferences) {
		User updatedUser = service.updatePreferences(preferences);
		if (updatedUser != null)
			return ResponseEntity.ok().build();
		else
			return ResponseEntity.badRequest().body("Could not update user preferences");
	}

	@Override
	@GetMapping("preferences")
	public Map<String, Object> getPreferences() {
		return service.getPreferences();

	}

	@Override
	@DeleteMapping("user")
	public ResponseEntity<String> delete() {
		service.deleteUser();
		return ResponseEntity.ok().build();
	}

}
