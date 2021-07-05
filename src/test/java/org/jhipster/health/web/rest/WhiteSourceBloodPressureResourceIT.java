package org.jhipster.health.web.rest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jhipster.health.TwentyOnePointsApp;
import org.jhipster.health.domain.BloodPressure;
import org.jhipster.health.repository.BloodPressureRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the BloodPressureResource/updateBloodPressure method.
 *
 * @see BloodPressureResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TwentyOnePointsApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class WhiteSourceBloodPressureResourceIT {

    private static final ZonedDateTime DEFAULT_TIMESTAMP = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);

    private static final Integer DEFAULT_SYSTOLIC = 1;
    private static final Integer UPDATED_SYSTOLIC = 2;

    private static final Integer DEFAULT_DIASTOLIC = 1;
    private static final Integer UPDATED_DIASTOLIC = 2;

    @Autowired
    private BloodPressureRepository bloodPressureRepository;

    @Autowired
    private MockMvc restBloodPressureMockMvc;

    private BloodPressure bloodPressure;
    private Long bloodPressureId;

    public static BloodPressure createEntity() {
        BloodPressure bloodPressure = new BloodPressure()
            .timestamp(DEFAULT_TIMESTAMP)
            .systolic(DEFAULT_SYSTOLIC)
            .diastolic(DEFAULT_DIASTOLIC);
        return bloodPressure;
    }

    @Transactional
    public Long createBloodPressure() throws Exception {
        int databaseSizeBeforeCreate = bloodPressureRepository.findAll().size();

        // Create the BloodPressure
        restBloodPressureMockMvc.perform(post("/api/blood-pressures")
            .with(user("user"))
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bloodPressure)))
            .andExpect(status().isCreated());

        // Validate the BloodPressure in the database
        List<BloodPressure> bloodPressureList = bloodPressureRepository.findAll();
        assertThat(bloodPressureList).hasSize(databaseSizeBeforeCreate + 1);
        BloodPressure testBloodPressure = bloodPressureList.get(bloodPressureList.size() - 1);
        assertThat(testBloodPressure.getTimestamp()).isEqualTo(DEFAULT_TIMESTAMP);
        assertThat(testBloodPressure.getSystolic()).isEqualTo(DEFAULT_SYSTOLIC);
        assertThat(testBloodPressure.getDiastolic()).isEqualTo(DEFAULT_DIASTOLIC);

        return testBloodPressure.getId();
    }

    @Before
    public void initTest() throws Exception {
        bloodPressure = createEntity();
        bloodPressureId = createBloodPressure();
    }

    @Test
    @Transactional
    public void updateBloodPressureWithXMLPayloadParseWithXif() throws Exception {

        String xmlPayload =  "<bloodPressure>" +
            "<bloodPressureId>"+ bloodPressureId +"</bloodPressureId>" +
            "<bloodPressureTimestamp>" + DEFAULT_TIMESTAMP + "</bloodPressureTimestamp>" +
            "<bloodPressureSystolic>"+ UPDATED_SYSTOLIC +"</bloodPressureSystolic>" +
            "<bloodPressureDiastolic>"+ UPDATED_DIASTOLIC + "</bloodPressureDiastolic>" +
            "<user null=\"true\"/></bloodPressure>";

        // Update the BloodPressure
        MvcResult result = restBloodPressureMockMvc.perform(put("/api/blood-pressures-xml/xif")
            .with(user("user"))
            .contentType("application/xml")
            .content(xmlPayload))
            .andExpect(status().isOk())
            .andReturn();

        JsonObject jsonObject = new JsonParser().parse(result.getResponse().getContentAsString()).getAsJsonObject();
        assertThat(jsonObject.get("systolic").getAsInt()).isEqualTo(UPDATED_SYSTOLIC);
        assertThat(jsonObject.get("diastolic").getAsInt()).isEqualTo(UPDATED_DIASTOLIC);
    }

    @Test
    @Transactional
    public void updateBloodPressureWithXMLPayloadParseWithDbf() throws Exception {

        String xmlPayload =  "<bloodPressure>" +
            "<bloodPressureId>"+ bloodPressureId +"</bloodPressureId>" +
            "<bloodPressureTimestamp>" + DEFAULT_TIMESTAMP + "</bloodPressureTimestamp>" +
            "<bloodPressureSystolic>"+ UPDATED_SYSTOLIC +"</bloodPressureSystolic>" +
            "<bloodPressureDiastolic>"+ UPDATED_DIASTOLIC + "</bloodPressureDiastolic>" +
            "<user null=\"true\"/></bloodPressure>";

        // Update the BloodPressure
        MvcResult result = restBloodPressureMockMvc.perform(put("/api/blood-pressures-xml/dbf")
            .with(user("user"))
            .contentType("application/xml")
            .content(xmlPayload))
            .andExpect(status().isOk())
            .andReturn();

        JsonObject jsonObject = new JsonParser().parse(result.getResponse().getContentAsString()).getAsJsonObject();
        assertThat(jsonObject.get("systolic").getAsInt()).isEqualTo(UPDATED_SYSTOLIC);
        assertThat(jsonObject.get("diastolic").getAsInt()).isEqualTo(UPDATED_DIASTOLIC);
    }
}
