package com.unibg.magellanus.backend.itinerary;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

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
import com.unibg.magellanus.backend.itinerary.service.Itinerary;
import com.unibg.magellanus.backend.itinerary.service.ItineraryRepository;
import com.unibg.magellanus.backend.itinerary.service.POI;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ContextConfiguration
@ActiveProfiles(profiles = { "dev", "test" })
public class ItineraryMicroserviceIntegrationTest {

	private static final String TEST_USER_UID = "eXs3E6tEyrUTbz6LWqaspj5oCPW2";
	private static Itinerary testItinerary;
	private static List<Itinerary> testItineraryList;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper mapper;

	@Autowired
	ItineraryRepository repository;

	@BeforeAll
	public static void setup(@Autowired ItineraryRepository repository) {
		testItinerary = new Itinerary();
		testItinerary.setId("TEST_ID");
		testItinerary.setOwner(TEST_USER_UID);
		testItinerary.setName("test_name");
		testItinerary.setCompletionDate(null);
		
		Set<POI> set = testItinerary.getPOISet();
		set.add(new POI(41.158150, -75.379219));
		set.add(new POI(51.917500, 0.333340));
		set.add(new POI(53.224621, -4.197995));

		Itinerary i1 = new Itinerary();
		i1.setId("1");
		i1.setCompletionDate(LocalDate.now());
		i1.setOwner(TEST_USER_UID);

		Itinerary i2 = new Itinerary();
		i2.setId("2");
		i2.setCompletionDate(LocalDate.now());
		i2.setOwner(TEST_USER_UID);

		Itinerary i3 = new Itinerary();
		i3.setId("3");
		i3.setCompletionDate(null);
		i3.setOwner(TEST_USER_UID);

		Itinerary i4 = new Itinerary();
		i4.setId("4");
		i4.setCompletionDate(LocalDate.now());
		i4.setOwner("not_owner");

		Itinerary i5 = new Itinerary();
		i5.setId("5");
		i5.setCompletionDate(null);
		i5.setOwner("not_owner");
		
		Itinerary i6 = new Itinerary();
		i6.setId("6");
		i6.setCompletionDate(null);
		i6.setOwner(TEST_USER_UID);

		testItineraryList = List.of(i1, i2, i3, i4, i5, i6);
		repository.saveAll(testItineraryList);
	}

	@BeforeEach
	public void setupEach() {
		repository.save(testItinerary);
	}

	@AfterAll
	public static void teardown(@Autowired ItineraryRepository repository) {
		repository.deleteAll();
	}

	@Test
	void get_nonAuth_returnsUnauthorized() throws Exception {
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.get("/api/itineraries/1")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
		       .andExpect(status().isUnauthorized());

	}
	
	@Test
	@WithMockUser(username = TEST_USER_UID)
	void get_exists_returnsItinerary() throws Exception {
		
		MvcResult result = mockMvc.perform(
				MockMvcRequestBuilders.get("/api/itineraries/" + testItinerary.getId())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		
		String contentAsString = result.getResponse().getContentAsString();
		Itinerary response = mapper.readValue(contentAsString, Itinerary.class);
		assertEquals(testItinerary, response);
	}

	@Test
	@WithMockUser(username = TEST_USER_UID)
	void get_nonExistent_returnsNotFound() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/itineraries/NOT_PRESENT")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = TEST_USER_UID)
	void create_newItinerary_returnsCreatedItinerary() throws Exception {
		Itinerary i = new Itinerary();
		i.setName("NEW_TEST");
		
		MvcResult result = mockMvc.perform(
				MockMvcRequestBuilders.post("/api/itineraries/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.mapper.writeValueAsString(i)))
				.andExpect(status().isOk()).andReturn();
		
		String contentAsString = result.getResponse().getContentAsString();
		Itinerary response = mapper.readValue(contentAsString, Itinerary.class);
		assertNotNull(response.getId());
		assertEquals(TEST_USER_UID, response.getOwner());
		
		repository.deleteById(response.getId());
	}

	@Test
	@WithMockUser(username = TEST_USER_UID)
	void update_ownerIsIssuer_returnsNoContent() throws Exception {
		Set<POI> set = testItinerary.getPOISet();
		POI poi = new POI(55.506920,9.728400);
		set.add(poi);
				
		mockMvc.perform(
				MockMvcRequestBuilders.put("/api/itineraries/me/" + testItinerary.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.mapper.writeValueAsString(testItinerary)))
				.andExpect(status().isNoContent())
				.andExpect(jsonPath("$").doesNotExist());
		
		Set<POI> storedSet = repository.findById(testItinerary.getId()).get().getPOISet();
		assertTrue(storedSet.contains(poi));
	}
	
	@Test
	@WithMockUser(username = "NOT_OWNER")
	void update_ownerIsNotIssuer_returnsForbidden() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.put("/api/itineraries/me/" + testItinerary.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.mapper.writeValueAsString(testItinerary)))
				.andExpect(status().isForbidden());
	}
	
	@Test
	@WithMockUser(username = TEST_USER_UID)
	void delete_ownerIsIssuer_returnsNoContent() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.delete("/api/itineraries/me/" + testItinerary.getId())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent())
				.andExpect(jsonPath("$").doesNotExist());
		
		assertFalse(repository.existsById(testItinerary.getId()));
	}
	
	@Test
	@WithMockUser(username = "NOT_OWNER")
	void delete_ownerIsNotIssuer_returnsForbidden() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.delete("/api/itineraries/me/" + testItinerary.getId())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
		
		assertNotNull(repository.findById(testItinerary.getId()).get());
	}
	
	@Test
	@WithMockUser(username = TEST_USER_UID)
	void findMine_completed_returnsNotNullCompletionDate() throws Exception {
		
		MvcResult result = mockMvc.perform(
				MockMvcRequestBuilders.get("/api/itineraries/me?completed=true")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		
		String contentAsString = result.getResponse().getContentAsString();
		List<Itinerary> response = mapper.readValue(contentAsString, new TypeReference<List<Itinerary>>(){});
		assertEquals(2, response.size());
		assertThat( response, contains(
			    hasProperty("id", is("1")), 
			    hasProperty("id", is("2"))
			));
		response.stream().map(it -> it.getCompletionDate()).forEach(it -> assertNotNull(it));
	}
	
	@Test
	@WithMockUser(username = TEST_USER_UID)
	void findMine_notCompleted_returnsNullCompletionDate() throws Exception {
		
		MvcResult result = mockMvc.perform(
				MockMvcRequestBuilders.get("/api/itineraries/me?completed=false")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		
		String contentAsString = result.getResponse().getContentAsString();
		List<Itinerary> response = mapper.readValue(contentAsString, new TypeReference<List<Itinerary>>(){});
		assertEquals(3, response.size());
		assertThat( response, contains( 
			    hasProperty("id", is("3")),
			    hasProperty("id", is("6")),
			    hasProperty("id", is(testItinerary.getId()))
			));
		response.stream().map(it -> it.getCompletionDate()).forEach(it -> assertNull(it));
	}
	
	@Test
	@WithMockUser(username = TEST_USER_UID)
	void findMine_all_returnsAll() throws Exception {
		
		MvcResult result = mockMvc.perform(
				MockMvcRequestBuilders.get("/api/itineraries/me")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		
		String contentAsString = result.getResponse().getContentAsString();
		List<Itinerary> response = mapper.readValue(contentAsString, new TypeReference<List<Itinerary>>(){});
		assertEquals(5, response.size());
		assertThat( response, contains(
			    hasProperty("id", is("1")),
			    hasProperty("id", is("2")),
			    hasProperty("id", is("3")),
			    hasProperty("id", is("6")),
			    hasProperty("id", is(testItinerary.getId()))
			));
		response.stream().map(it -> it.getOwner()).forEach(it -> assertEquals(TEST_USER_UID, it));
	}

}
