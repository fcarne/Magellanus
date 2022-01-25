package com.unibg.magellanus.backend.route.service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/routes")
public class RouteTestController {

	@GetMapping("/message")
	public String test() {
		return "Route Service is up";
	}
}