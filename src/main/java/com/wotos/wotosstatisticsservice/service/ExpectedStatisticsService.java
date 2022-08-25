package com.wotos.wotosstatisticsservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wotos.wotosstatisticsservice.dao.ExpectedStatistics;
import com.wotos.wotosstatisticsservice.repo.ExpectedStatisticsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExpectedStatisticsService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();
    private final ExpectedStatisticsRepository expectedStatisticsRepository;

    @Value("${env.urls.expected_statistics}")
    private String EXPECTED_STATISTICS_URL;

    public ExpectedStatisticsService(
            ExpectedStatisticsRepository expectedStatisticsRepository
    ) {
        this.expectedStatisticsRepository = expectedStatisticsRepository;
    }

    public List<ExpectedStatistics> validateOrInitExpectedStatistics() {
        if (expectedStatisticsRepository.findAll().size() == 0) {
            List<ExpectedStatistics> expectedStatistics = fetchExpectedStatistics();
            expectedStatisticsRepository.saveAll(expectedStatistics);

            return expectedStatistics;
        } else {
            // validate data
            return null;
        }
    }

    public List<ExpectedStatistics> fetchExpectedStatistics() {

        String result = restTemplate.getForObject(EXPECTED_STATISTICS_URL, String.class);
        List<ExpectedStatistics> expectedStatistics = new ArrayList<>();

        try {
            JsonNode data = mapper.readTree(result).get("data");

            data.forEach(value -> {
                try {
                    ExpectedStatistics expectedStatistic = mapper.treeToValue(value, ExpectedStatistics.class);
                    expectedStatistics.add(expectedStatistic);
                } catch (JsonProcessingException e) {
                    System.out.println("Error parsing Expected Statistics values: " + e.getMessage());
                }
            });

            return expectedStatistics;
        } catch(IOException e) {
            System.out.println(e.getMessage());
            return null;
        }

    }

}
