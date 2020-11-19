package com.wotos.wotosstatisticsservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wotos.wotosstatisticsservice.dao.ExpectedStatisticsRepository;
import com.wotos.wotosstatisticsservice.dao.PlayerStatisticsRepository;
import com.wotos.wotosstatisticsservice.jsonao.TankStats;
import com.wotos.wotosstatisticsservice.model.ExpectedStatistics;
import com.wotos.wotosstatisticsservice.model.PlayerStatistics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StatsService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();
    private final HttpHeaders httpHeaders = new HttpHeaders();

    private final PlayerStatisticsRepository playerStatisticsRepository;
    private final ExpectedStatisticsRepository expectedStatisticsRepository;

    @Value("${env.app_id}")
    private String APP_ID;
    private final String EXPECTED_STATISTICS_URI = "https://static.modxvm.com/wn8-data-exp/json/wn8exp.json";
    private String PLAYER_STATISTICS_BY_TANK = "https://api.worldoftanks.com/wot/tanks/stats/?application_id=";

    public StatsService(
            PlayerStatisticsRepository playerStatisticsRepository,
            ExpectedStatisticsRepository expectedStatisticsRepository
    ) {
        this.playerStatisticsRepository = playerStatisticsRepository;
        this.expectedStatisticsRepository = expectedStatisticsRepository;
        mapper.configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false
        );
        httpHeaders.add("Language", "");
        initializeExpectedStatistics();
    }

    public Optional<PlayerStatistics> getPlayerStatistics(int playerID) {
        Optional<PlayerStatistics> playerStatistics = playerStatisticsRepository.findById(playerID);

        if (playerStatistics.isPresent()) {
            // Check for statistics age and add new/current stats to db
            return playerStatisticsRepository.findById(playerID);
        } else {
            fetchPlayerStatisticsByTank(playerID);
            return calculatePlayerStatistics(playerID);
        }

    }

    private Optional<PlayerStatistics> calculatePlayerStatistics(int playerID) {
        return null;
    }

    private List<TankStats> fetchPlayerStatisticsByTank(int playerID) {
        String result = restTemplate.getForObject(PLAYER_STATISTICS_BY_TANK + APP_ID + "&account_id=" + playerID, String.class);
        List<TankStats> tankStats = new ArrayList<>();

        try {
            JsonNode data = mapper.readTree(result).get("data").get(String.valueOf(playerID));

            data.forEach(value -> {
                try {
                    tankStats.add(mapper.treeToValue(value, TankStats.class));
                } catch (JsonProcessingException e) {
                    System.out.println("Error processing tank statistics JSON: " + e.getMessage());
                }
            });

            return tankStats;
        } catch (IOException e) {
            System.out.println("Error parsing fetched player data: " + e.getMessage());
            return null;
        }
    }

    public List<ExpectedStatistics> fetchExpectedStatistics() {

        String result = restTemplate.getForObject(EXPECTED_STATISTICS_URI, String.class);
        List<ExpectedStatistics> expectedStatistics = new ArrayList<>();

        try {
            JsonNode data = mapper.readTree(result).get("data");

            data.forEach(value -> {
                try {
                    ExpectedStatistics expectedStatistic = mapper.treeToValue(value, ExpectedStatistics.class);
                    expectedStatistic.setIDNum(value.get("IDNum").asInt());
                    expectedStatistics.add(expectedStatistic);
                } catch (JsonProcessingException e) {
                    System.out.println("Error fetching Expected Statistics values: " + e.getMessage());
                }
            });

            return expectedStatistics;
        } catch(IOException e) {
            System.out.println(e.getMessage());
            return null;
        }

    }

    private void initializeExpectedStatistics() {
        if (expectedStatisticsRepository.findAll().size() == 0) {
            expectedStatisticsRepository.saveAll(fetchExpectedStatistics());
        }
    }

    private void updateExpectedStatistics(List<ExpectedStatistics> newExpectedStatistics) {

    }

}
