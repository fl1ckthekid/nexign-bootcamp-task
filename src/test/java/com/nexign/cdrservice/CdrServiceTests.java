package com.nexign.cdrservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
public class CdrServiceTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGenerateCdrRecords() throws Exception {
        mockMvc.perform(post("/api/udr/generate?count=50"))
                .andExpect(status().isOk())
                .andExpect(content().string("Сгенерировано 50 CDR-записей."));
    }

    @Test
    void testGetUdrByMsisdn() throws Exception {
        mockMvc.perform(get("/api/udr/79991112233?month=2025-02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msisdn").value("79991112233"))
                .andExpect(jsonPath("$.incomingCall.totalTime", notNullValue()))
                .andExpect(jsonPath("$.outcomingCall.totalTime", notNullValue()));
    }

    @Test
    void testGetAllUdrs() throws Exception {
        mockMvc.perform(get("/api/udr/all?month=2025-02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetUdrWithoutMonth() throws Exception {
        mockMvc.perform(get("/api/udr/79991112233"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msisdn").value("79991112233"));
    }

    @Test
    void testInvalidMsisdn() throws Exception {
        mockMvc.perform(get("/api/udr/invalid_number"))
                .andExpect(status().isOk())  // Возможно, API вернет 200 с пустым JSON
                .andExpect(jsonPath("$.msisdn").value("invalid_number"))
                .andExpect(jsonPath("$.incomingCall.totalTime", is("00:00:00")))
                .andExpect(jsonPath("$.outcomingCall.totalTime", is("00:00:00")));
    }

    @Test
    void testEmptyUdrResponse() throws Exception {
        mockMvc.perform(get("/api/udr/79992223344?month=2024-01"))  // Номер без звонков
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msisdn").value("79992223344"))
                .andExpect(jsonPath("$.incomingCall.totalTime", is("00:00:00")))
                .andExpect(jsonPath("$.outcomingCall.totalTime", is("00:00:00")));
    }
}
