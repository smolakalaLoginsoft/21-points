package org.jhipster.health.web.rest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minidev.json.JSONObject;
import org.jhipster.health.TwentyOnePointsApp;
import org.jhipster.health.domain.Points;
import org.jhipster.health.repository.PointsRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the PointsResource/updatePoints method.
 *
 * @see PointsResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TwentyOnePointsApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class WhiteSourcePointsResourceIT {

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final Integer DEFAULT_EXERCISE = 1;
    private static final Integer DEFAULT_MEALS = 1;
    private static final Integer DEFAULT_ALCOHOL = 1;
    private static final String DEFAULT_NOTES = "AAAAAAAAAA";

    @Autowired
    private PointsRepository pointsRepository;

    @Autowired
    private MockMvc restPointsMockMvc;

    private Points points;
    private String authorizationHeader;
    private Long pointId;

    @LocalServerPort
    private int port;

    /**
     * Create an entity for this test.
     *
     */
    public static Points createEntity() {
        Points points = new Points()
            .date(DEFAULT_DATE)
            .exercise(DEFAULT_EXERCISE)
            .meals(DEFAULT_MEALS)
            .alcohol(DEFAULT_ALCOHOL)
            .notes(DEFAULT_NOTES);
        return points;
    }

    public void setAuthorizationHeader() {
        TestRestTemplate restTemplate = new TestRestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject loginParams = new JSONObject();
        loginParams.put("username", "admin");
        loginParams.put("password", "admin");

        HttpEntity<String> entity = new HttpEntity<String>(loginParams.toJSONString(), headers);

        ResponseEntity<String> response = restTemplate.exchange(
            createUrlWithPort("/api/authenticate"), HttpMethod.POST, entity, String.class);

        assertThat(response).isNotNull();

        authorizationHeader = response.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

        assertThat(authorizationHeader.contains("Bearer")).isTrue();
    }

    @Transactional
    public Long createPointWithMockMvc() throws Exception {
        int databaseSizeBeforeCreate = pointsRepository.findAll().size();

        // Create the Points
        restPointsMockMvc.perform(post("/api/points")
            .with(user("user"))
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(points)))
            .andExpect(status().isCreated());

        // Validate the Points in the database
        List<Points> pointsList = pointsRepository.findAll();
        assertThat(pointsList).hasSize(databaseSizeBeforeCreate + 1);
        Points testPoints = pointsList.get(pointsList.size() - 1);
        assertThat(testPoints.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testPoints.getExercise()).isEqualTo(DEFAULT_EXERCISE);
        assertThat(testPoints.getMeals()).isEqualTo(DEFAULT_MEALS);
        assertThat(testPoints.getAlcohol()).isEqualTo(DEFAULT_ALCOHOL);
        assertThat(testPoints.getNotes()).isEqualTo(DEFAULT_NOTES);

        return testPoints.getId();
    }

    @Before
    public void initTest() throws Exception {
        points = createEntity();
        setAuthorizationHeader();
        pointId = createPointWithMockMvc();
    }

    @Test
    @Transactional
    public void updatePointWithMockMvc() throws Exception {
        // Set newly created pointId
        points.setId(pointId);
        // Update notes
        points.setNotes("updatedWithMockMvc");

        // Create the Points
        MvcResult result = restPointsMockMvc.perform(put("/api/points")
            .with(user("user"))
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(points)))
            .andExpect(status().isOk())
            .andReturn();

        JsonObject jsonObject = new JsonParser().parse(result.getResponse().getContentAsString()).getAsJsonObject();
        assertThat(jsonObject.get("notes").getAsString()).isEqualTo("updatedWithMockMvc");
    }

    @Test
    @Transactional
    public void updatePointWithRestTemplate() throws Exception {
        TestRestTemplate restTemplate = new TestRestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authorizationHeader);

        JSONObject pointParams = new JSONObject();
        pointParams.put("id", "" + pointId);
        pointParams.put("date", "" + DEFAULT_DATE);
        pointParams.put("exercise", DEFAULT_EXERCISE);
        pointParams.put("meals", DEFAULT_MEALS);
        pointParams.put("alcohol", DEFAULT_ALCOHOL);
        pointParams.put("notes", "updated");
        pointParams.put("user", null);

        HttpEntity<String> entity = new HttpEntity<String>(pointParams.toJSONString(), headers);

        ResponseEntity<String> response = restTemplate.exchange(
            createUrlWithPort("/api/points"), HttpMethod.PUT, entity, String.class);

        assertThat(response).isNotNull();

        JsonObject jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
        assertThat(jsonObject.get("notes").getAsString()).isEqualTo("updated");
    }

    private String createUrlWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
