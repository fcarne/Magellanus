package com.unibg.magellanus.backend.user;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unibg.magellanus.backend.user.service.User;
import com.unibg.magellanus.backend.user.service.UserRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ContextConfiguration
@ActiveProfiles(profiles = {"dev", "test"})
public class UserMicroserviceIntegrationTest {

	private static final String TEST_USER_UID = "eXs3E6tEyrUTbz6LWqaspj5oCPW2";
	private static final String TEST_USER_EMAIL = "integration@test.com";
	private static User testUser;
	
	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	UserRepository repository;
	
	@BeforeAll
	public static void setup() {
		testUser = new User();
		testUser.setUid(TEST_USER_UID);
		testUser.setEmail(TEST_USER_EMAIL);
		Map<String, Object> prefs = new HashMap<>();
		prefs.put("pref_a", "A");
		testUser.setPreferences(prefs);
		
	}
	
	@BeforeEach
	public void setupEach() {
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
		User user = new User();
		user.setUid("NEW_USER");
		user.setEmail("new_user@test.com");
		repository.delete(user);

		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.post("/api/users/")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(this.mapper.writeValueAsString(user));

		mockMvc.perform(mockRequest)
				.andExpect(status().isNoContent())
				.andExpect(jsonPath("$").doesNotExist());
		
		assertNotNull(repository.findById(TEST_USER_UID));
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
	void delete_nonAuth_returnsUnauthorized() throws Exception {
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.delete("/api/users/me")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
		       .andExpect(status().isUnauthorized());
	}
	
	@Test
	@WithMockUser(username = TEST_USER_UID)
	void delete_ownAccount_returnsNoContent() throws Exception {
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.delete("/api/users/me")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);

		mockMvc.perform(mockRequest)
				.andExpect(status().isNoContent())
				.andExpect(jsonPath("$").doesNotExist());

		assertFalse(repository.existsById(TEST_USER_UID));
	}
	
	@Test
	@WithMockUser(username = TEST_USER_UID)
	void getPreferences_existent_returnsPrefs() throws Exception {
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
				.get("/api/users/me/preferences")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		
		TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};

		Map<String, Object> parsedResponse = mapper.readValue(result.getResponse().getContentAsByteArray(), typeRef);
		assertThat(parsedResponse, is(testUser.getPreferences()));
	}
	
	@Test
	@WithMockUser(username = TEST_USER_UID)
	void setPreferences_ownAccount_returnsNoContent() throws Exception {
		Map<String, Object> prefs = new HashMap<>();
		prefs.put("pref_b", "B");
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
			.patch("/api/users/me/preferences")
			.contentType(MediaType.APPLICATION_JSON)
			.content(this.mapper.writeValueAsString(prefs));
		
		mockMvc.perform(mockRequest)
				.andExpect(status().isNoContent())
				.andExpect(jsonPath("$").doesNotExist());

		Map<String, Object> storedPrefs = repository.findById(testUser.getUid()).get().getPreferences();
		assertThat(storedPrefs, Matchers.hasEntry(equalTo("pref_b"), equalTo("B")));
	}

}
