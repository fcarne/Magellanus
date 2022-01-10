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
import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuthException;
import com.unibg.magellanus.backend.user.User;
import com.unibg.magellanus.backend.user.UserRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")

class UserManagerIntegrationTest {

	private static final String TEST_USER_UID = "eXs3E6tEyrUTbz6LWqaspj5oCPW2";
	private static final String TEST_USER_EMAIL = "integration@test.com";
	private static final String OTHER_USER_UID = "MEeRCiI4nUS3Az98vcnznDwTpzR2";
	private static final String OTHER_USER_EMAIL = "android.test@test.com";
	private static User testUser;
	
	@Autowired
	WebApplicationContext wac;
	
	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	UserRepository repository;
	
	@BeforeAll
	public static void setup() throws FirebaseAuthException {
		testUser = new User(TEST_USER_UID, TEST_USER_EMAIL);
		Map<String, Object> prefs = new HashMap<>();
		prefs.put("pref_a", "A");
		testUser.setPreferences(prefs);
		
	}
	
	@BeforeEach
	public void setupEach() throws FirebaseAuthException {
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
	@WithMockCustomUser(uid=TEST_USER_UID, email = TEST_USER_EMAIL)
	void delete_ownAccount_returnsNoContent() throws Exception {
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.delete("/api/users/" + testUser.getUid())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);

		mockMvc.perform(mockRequest)
				.andExpect(status().isNoContent())
				.andExpect(jsonPath("$").doesNotExist());

	}
	
	@Test
	@WithMockCustomUser(uid=OTHER_USER_UID, email = OTHER_USER_EMAIL)
	void delete_otherAccount_returnsForbidden() throws Exception {
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.delete("/api/users/" + testUser.getUid())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);

		mockMvc.perform(mockRequest)
				.andExpect(status().isForbidden());

	}
	
	@Test
	@WithMockCustomUser(uid=TEST_USER_UID, email = TEST_USER_EMAIL)
	void getPreferences_existent_returnsPrefs() throws Exception {
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
				.get("/api/users/" + testUser.getUid() + "/preferences")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		
		TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};

		Map<String, Object> parsedResponse = mapper.readValue(result.getResponse().getContentAsByteArray(), typeRef);
		assertThat(parsedResponse, is(testUser.getPreferences()));
	}
	
	@Test
	@WithMockCustomUser(uid=TEST_USER_UID, email = TEST_USER_EMAIL)
	void setPreferences_ownAccount_returnsNoContent() throws Exception {
		Map<String, Object> prefs = new HashMap<>();
		prefs.put("pref_b", "B");
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
			.patch("/api/users/" + testUser.getUid() + "/preferences")
			.contentType(MediaType.APPLICATION_JSON)
			.content(this.mapper.writeValueAsString(prefs));
		mockMvc.perform(mockRequest)
				.andExpect(status().isNoContent())
				.andExpect(jsonPath("$").doesNotExist());

		Map<String, Object> storedPrefs = repository.findById(testUser.getUid()).get().getPreferences();
		assertThat(storedPrefs, Matchers.hasEntry(equalTo("pref_b"), equalTo("B")));
	}
	
	@Test
	@WithMockCustomUser(uid=OTHER_USER_UID, email = OTHER_USER_EMAIL)
	void setPreferences_otherAccount_returnsForbidden() throws Exception {
		Map<String, Object> prefs = new HashMap<>();
		prefs.put("pref_b", "B");
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
			.patch("/api/users/" + testUser.getUid() + "/preferences")
			.contentType(MediaType.APPLICATION_JSON)
			.content(this.mapper.writeValueAsString(prefs));
		mockMvc.perform(mockRequest)
				.andExpect(status().isForbidden());
	}

}
