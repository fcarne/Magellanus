package com.unibg.magellanus.backend.route;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unibg.magellanus.backend.route.service.Route;
import com.unibg.magellanus.backend.route.service.RouteRepository;
import com.unibg.magellanus.backend.route.service.RoutedPOI;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ContextConfiguration
@ActiveProfiles(profiles = { "dev", "test" })
public class RouteMicroserviceIntegrationTest {

	private static final String TEST_USER_UID = "eXs3E6tEyrUTbz6LWqaspj5oCPW2";
	private static Route testRoute;
	
	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper mapper;

	@Autowired
	RouteRepository repository;
		
	@BeforeAll
	public static void setup(@Autowired RouteRepository repository) {
		testRoute = new Route();
		testRoute.setId("TEST_ID");
		testRoute.setItineraryId("test_itineray_id");
		testRoute.setOwner(TEST_USER_UID);
		
		List<RoutedPOI> list = testRoute.getRoute();
		list.add(new RoutedPOI(41.158150, -75.379219));
		list.add(new RoutedPOI(51.917500, 0.333340));
		list.add(new RoutedPOI(53.224621, -4.197995));
	}

	@BeforeEach
	public void setupEach() {
		repository.save(testRoute);
	}

	@AfterAll
	public static void teardown(@Autowired RouteRepository repository) {
		repository.deleteAll();
	}

	@Test
	void get_nonAuth_returnsUnauthorized() throws Exception {
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
				.get("/1")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
		       .andExpect(status().isUnauthorized());

	}
	
	@Test
	@WithMockUser(username = TEST_USER_UID)
	void get_exists_returnsItinerary() throws Exception {
		
		MvcResult result = mockMvc.perform(
				MockMvcRequestBuilders.get("/" + testRoute.getId())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		
		String contentAsString = result.getResponse().getContentAsString();
		Route response = mapper.readValue(contentAsString, Route.class);
		assertEquals(testRoute, response);
	}

	@Test
	@WithMockUser(username = TEST_USER_UID)
	void get_nonExistent_returnsNotFound() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.get("/NOT_PRESENT")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = TEST_USER_UID)
	void getByItinerary_exists_returnsItinerary() throws Exception {
		
		MvcResult result = mockMvc.perform(
				MockMvcRequestBuilders.get("/itinerary/" + testRoute.getItineraryId())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		
		String contentAsString = result.getResponse().getContentAsString();
		Route response = mapper.readValue(contentAsString, Route.class);
		assertEquals(testRoute, response);
	}

	@Test
	@WithMockUser(username = TEST_USER_UID)
	void getByItinerary_nonExistent_returnsNotFound() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.get("/itinerary/NOT_PRESENT")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	
	@Test
	@WithMockUser(username = TEST_USER_UID)
	void create_itineraryExists_returnsCreatedRoute() throws Exception {		
		Route r = new Route();
		r.setItineraryId("test_itinerary_id");
		
		MvcResult result = mockMvc.perform(
				MockMvcRequestBuilders.post("/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.mapper.writeValueAsString(r)))
				.andExpect(status().isOk()).andReturn();
		
		String contentAsString = result.getResponse().getContentAsString();
		Route response = mapper.readValue(contentAsString, Route.class);
		
		//mockServer.verify();
		assertNotNull(response.getId());
		assertEquals(TEST_USER_UID, response.getOwner());
		repository.deleteById(response.getId());
	}

	@Test
	@WithMockUser(username = TEST_USER_UID)
	void update_ownerIsIssuer_returnsNoContent() throws Exception {
		List<RoutedPOI> list = testRoute.getRoute();
		RoutedPOI poi = new RoutedPOI(55.506920,9.728400);
		list.add(poi);
				
		mockMvc.perform(
				MockMvcRequestBuilders.put("/me/" + testRoute.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.mapper.writeValueAsString(testRoute)))
				.andExpect(status().isNoContent())
				.andExpect(jsonPath("$").doesNotExist());
		
		List<RoutedPOI> storedRoute = repository.findById(testRoute.getId()).get().getRoute();
		assertTrue(storedRoute.contains(poi));
	}
	
	@Test
	@WithMockUser(username = "NOT_OWNER")
	void update_ownerIsNotIssuer_returnsForbidden() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.put("/me/" + testRoute.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.mapper.writeValueAsString(testRoute)))
				.andExpect(status().isForbidden());
	}
	
	@Test
	@WithMockUser(username = TEST_USER_UID)
	void delete_ownerIsIssuer_returnsNoContent() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.delete("/me/" + testRoute.getId())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent())
				.andExpect(jsonPath("$").doesNotExist());
		
		assertFalse(repository.existsById(testRoute.getId()));
	}
	
	@Test
	@WithMockUser(username = "NOT_OWNER")
	void delete_ownerIsNotIssuer_returnsForbidden() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.delete("/me/" + testRoute.getId())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
		
		assertNotNull(repository.findById(testRoute.getId()).get());
	}
	
	@Test
	@WithMockUser(username = TEST_USER_UID)
	void autoGenerate_itineraryExists_returnsCreatedRoute() throws Exception {
		Route r = new Route();
		r.setItineraryId("test_itinerary_id");
		
		List<RoutedPOI> list = r.getRoute();
		list.add(new RoutedPOI(41.158150, -75.379219));
		list.add(new RoutedPOI(51.917500, 0.333340));
		list.add(new RoutedPOI(53.224621, -4.197995));
		
		MvcResult result = mockMvc.perform(
				MockMvcRequestBuilders.post("/auto")
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.mapper.writeValueAsString(r)))
				.andExpect(status().isOk()).andReturn();
		
		String contentAsString = result.getResponse().getContentAsString();
		Route response = mapper.readValue(contentAsString, Route.class);
		
		assertNotNull(response.getId());
		assertEquals(TEST_USER_UID, response.getOwner());
		repository.deleteById(response.getId());
	}
	
}
