package com.wotos.wotosstatisticsservice.controller;

import com.wotos.wotosstatisticsservice.dto.PlayerTrendResponse;
import com.wotos.wotosstatisticsservice.dto.TrendPoint;
import com.wotos.wotosstatisticsservice.exception.GlobalExceptionHandler;
import com.wotos.wotosstatisticsservice.service.PlayerStatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web-layer tests for the player WN8 trend endpoint, driven by a standalone
 * {@link MockMvc} (no Spring context, security, or database). Covers the happy
 * path, an empty series, and an invalid {@code bucket} value.
 */
@ExtendWith(MockitoExtension.class)
public class PlayerStatisticsControllerTrendTest {

    @Mock
    private PlayerStatisticsService playerStatisticsService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        PlayerStatisticsController controller = new PlayerStatisticsController(playerStatisticsService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void returnsTrendPointsForTheHappyPath() throws Exception {
        PlayerTrendResponse response = new PlayerTrendResponse(1, "day", List.of(
                new TrendPoint("2026-03-01T00:00:00Z", 1500f, 100),
                new TrendPoint("2026-03-02T00:00:00Z", 1550f, 110)
        ));
        when(playerStatisticsService.getPlayerWn8Trend(eq(1), any(), any(), eq("day"))).thenReturn(response);

        mockMvc.perform(get("/api/stats/players/1/trend").param("bucket", "day"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1))
                .andExpect(jsonPath("$.bucket").value("day"))
                .andExpect(jsonPath("$.points.length()").value(2))
                .andExpect(jsonPath("$.points[0].t").value("2026-03-01T00:00:00Z"))
                .andExpect(jsonPath("$.points[0].wn8").value(1500.0))
                .andExpect(jsonPath("$.points[0].battles").value(100));
    }

    @Test
    public void returnsEmptyPointsWhenNoSnapshotsExist() throws Exception {
        PlayerTrendResponse response = new PlayerTrendResponse(2, "day", List.of());
        when(playerStatisticsService.getPlayerWn8Trend(eq(2), any(), any(), eq("day"))).thenReturn(response);

        mockMvc.perform(get("/api/stats/players/2/trend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(2))
                .andExpect(jsonPath("$.points.length()").value(0));
    }

    @Test
    public void returnsBadRequestForAnInvalidBucket() throws Exception {
        when(playerStatisticsService.getPlayerWn8Trend(eq(3), any(), any(), eq("bogus")))
                .thenThrow(new IllegalArgumentException("Invalid bucket 'bogus'; expected 'day' or 'week'"));

        mockMvc.perform(get("/api/stats/players/3/trend").param("bucket", "bogus"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Invalid bucket")));
    }
}
