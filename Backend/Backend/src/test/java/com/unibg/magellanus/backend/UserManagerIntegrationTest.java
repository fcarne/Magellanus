package com.unibg.magellanus.backend;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unibg.magellanus.backend.user.UserRepository;
import com.unibg.magellanus.backend.user.auth.TokenFilter;
import com.unibg.magellanus.backend.user.model.User;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")

class UserManagerIntegrationTest {

	static User testUser;

	String testUserToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjM1MDZmMzc1MjI0N2ZjZjk0Y2JlNWQyZDZiNTlmYThhMmJhYjFlYzIiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20vbWFnZWxsYW51cy1wYWMiLCJhdWQiOiJtYWdlbGxhbnVzLXBhYyIsImF1dGhfdGltZSI6MTY0MTU5NjcyNywidXNlcl9pZCI6ImVYczNFNnRFeXJVVGJ6NkxXcWFzcGo1b0NQVzIiLCJzdWIiOiJlWHMzRTZ0RXlyVVRiejZMV3Fhc3BqNW9DUFcyIiwiaWF0IjoxNjQxNTk2NzI3LCJleHAiOjE2NDE2MDAzMjcsImVtYWlsIjoiaW50ZWdyYXRpb25AdGVzdC5jb20iLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsImZpcmViYXNlIjp7ImlkZW50aXRpZXMiOnsiZW1haWwiOlsiaW50ZWdyYXRpb25AdGVzdC5jb20iXX0sInNpZ25faW5fcHJvdmlkZXIiOiJwYXNzd29yZCJ9fQ.ax4LqHCHj6eDWwbs_qLd5z7HBaMgkGU_9ino8fJEKZRYE7vNbKa7a5otrlF_X7yFLdPJlPNCLEMiVR7rcdt1Unr58QRcAfu16EhPpZHTqYqRpbbI4ak7bmi_BpZbbYRCMTzxe7eXi3NPambfNg7HBLJqzd0bt-E-KDbwMPKkBaQk9J_EByCY35wP4Tb4oPYj-OJC8d2Hb2_HtDyPo6CYgBi8RUGnPFygagriOr32XzWIRs9GGcCEYfwynQ5lvUxZLNuRU3Eon27rzlWqJayRANjA4eRctgVfUaGhWqsMf3P_DBFr9KVqMTyKkLD29KGEQhpCrCwWiERElVrF2x93qQ";
	String otherToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjM1MDZmMzc1MjI0N2ZjZjk0Y2JlNWQyZDZiNTlmYThhMmJhYjFlYzIiLCJ0eXAiOiJKV1QifQ.eyJuYW1lIjoiRmVyZGluYW5kbyBNYWdlbGxhbm8iLCJwaWN0dXJlIjoiaHR0cHM6Ly9saDMuZ29vZ2xldXNlcmNvbnRlbnQuY29tL2EvQUFUWEFKd3dJOGZ0Mk5WNjQ2M3BTOUhEVkZRVDFqZUVUVHI3NG11LUF4aXU9czk2LWMiLCJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20vbWFnZWxsYW51cy1wYWMiLCJhdWQiOiJtYWdlbGxhbnVzLXBhYyIsImF1dGhfdGltZSI6MTY0MTU5NjU2NywidXNlcl9pZCI6ImpKVmxidkM5VUVaUE1ZM1NWSkJZSHlISXBwbDEiLCJzdWIiOiJqSlZsYnZDOVVFWlBNWTNTVkpCWUh5SElwcGwxIiwiaWF0IjoxNjQxNTk2NTY3LCJleHAiOjE2NDE2MDAxNjcsImVtYWlsIjoicGFjLm1hZ2VsbGFudXNAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImZpcmViYXNlIjp7ImlkZW50aXRpZXMiOnsiZ29vZ2xlLmNvbSI6WyIxMDk0Nzg0NzMyMzM0OTgyMjMwNDciXSwiZW1haWwiOlsicGFjLm1hZ2VsbGFudXNAZ21haWwuY29tIl19LCJzaWduX2luX3Byb3ZpZGVyIjoiZ29vZ2xlLmNvbSJ9fQ.NJ292XyIukSX2vsco-l4gB50qYoeIBi4q02Fv1QCzdVlW5zEGwF3eXMQ-1bp-2UwIw5DLNx9-iggxA7hzG7PIp-C8BLRl_KYml1MTlObDnaTFigHxhcCIJGYII1sFHNbfa3CEmhYmxOzB4ToKCL-Jep2Qa6e8sDuqP8CHgo-Np8KElWyORtHU53BZ5xKVX7W89HxZe4FvlhYLZ_vxiiyq8IT4UKednFpzwkHNkK6vVa3vSrbVxKnSsXJvvjSeoUFFqXbCoxZ1P5upcmysc2DHeZfdKegQuG-zGZTtdR5Gqw4SfO3VxI8_lxmvR-ZiDWFv3E2iD4p5s7oVwPj2RgE-w";
	
	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	UserRepository repository;
	
	@BeforeEach
	public void setup() {
		testUser = new User("eXs3E6tEyrUTbz6LWqaspj5oCPW2", "integration@test.com");
		Map<String, Object> prefs = new HashMap<>();
		prefs.put("pref_a", "A");
		testUser.setPreferences(prefs);
		repository.save(testUser);
	}
	
	@AfterAll
	public static void teardown(@Autowired UserRepository repository) {
		repository.deleteAll();
	}

	@Test
	void checkIfExists_exists_returnsOk() throws Exception {	
		mockMvc.perform(MockMvcRequestBuilders
	            .head("/api/users/" + testUser.getUid())
	            .contentType(MediaType.APPLICATION_JSON))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$").doesNotExist());
	}

	@Test
	void checkIfExists_nonExistent_returnsNotFound() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
	            .head("/api/users/NOT_PRESENT")
	            .contentType(MediaType.APPLICATION_JSON))
	            .andExpect(status().isNotFound())
	            .andExpect(jsonPath("$").doesNotExist());
	}

	@Test
	void signUp_newUser_returnsNoContent() throws Exception {
		User user = new User("NEW_USER", "new_user@test.com");
		repository.delete(user);

		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/users/")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(this.mapper.writeValueAsString(user));

		mockMvc.perform(mockRequest)
				.andExpect(status().isNoContent())
				.andExpect(jsonPath("$").doesNotExist());
		
	}

	@Test
	void signUp_alreadySignedUser_returnsUnprocessable() throws Exception {
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/users/")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(this.mapper.writeValueAsString(testUser));

		mockMvc.perform(mockRequest)
				.andExpect(status().isUnprocessableEntity());
	}
	
	@Test
	void delete_nonAuth_returnsForbidden() throws Exception {
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.delete("/api/users/" + testUser.getUid())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);

		mockMvc.perform(mockRequest)
				.andExpect(status().isForbidden());

	}
	
	@Test
	void delete_ownAccount_returnsNoContent() throws Exception {
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.delete("/api/users/" + testUser.getUid())
				.header(TokenFilter.AUTHORIZATION_HEADER, TokenFilter.TOKEN_HEADER + testUserToken)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);

		mockMvc.perform(mockRequest)
				.andExpect(status().isNoContent())
				.andExpect(jsonPath("$").doesNotExist());

	}
	
	@Test
	void delete_otherAccount_returnsForbidden() throws Exception {
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.delete("/api/users/" + testUser.getUid())
				.header(TokenFilter.AUTHORIZATION_HEADER, TokenFilter.TOKEN_HEADER + otherToken)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);

		mockMvc.perform(mockRequest)
				.andExpect(status().isForbidden());

	}
	
	@Test
	void getPreferences_existent_returnsPrefs() throws Exception {
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
				.get("/api/users/" + testUser.getUid() + "/preferences")
				.header(TokenFilter.AUTHORIZATION_HEADER, TokenFilter.TOKEN_HEADER + testUserToken)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		
		TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};

		Map<String, Object> parsedResponse = mapper.readValue(result.getResponse().getContentAsByteArray(), typeRef);
		assertThat(parsedResponse, is(testUser.getPreferences()));
	}
	
	@Test
	void setPreferences_ownAccount_returnsNoContent() throws Exception {
		Map<String, Object> prefs = new HashMap<>();
		prefs.put("pref_b", "B");
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
			.patch("/api/users/" + testUser.getUid() + "/preferences")
			.header(TokenFilter.AUTHORIZATION_HEADER, TokenFilter.TOKEN_HEADER + testUserToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(this.mapper.writeValueAsString(prefs));
		mockMvc.perform(mockRequest)
				.andExpect(status().isNoContent())
				.andExpect(jsonPath("$").doesNotExist());

		Map<String, Object> storedPrefs = repository.findById(testUser.getUid()).get().getPreferences();
		assertThat(storedPrefs, Matchers.hasEntry(equalTo("pref_b"), equalTo("B")));
	}
	
	@Test
	void setPreferences_otherAccount_returnsForbidden() throws Exception {
		Map<String, Object> prefs = new HashMap<>();
		prefs.put("pref_b", "B");
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
			.patch("/api/users/" + testUser.getUid() + "/preferences")
			.header(TokenFilter.AUTHORIZATION_HEADER, TokenFilter.TOKEN_HEADER + otherToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(this.mapper.writeValueAsString(prefs));
		mockMvc.perform(mockRequest)
				.andExpect(status().isForbidden());
	}

}
