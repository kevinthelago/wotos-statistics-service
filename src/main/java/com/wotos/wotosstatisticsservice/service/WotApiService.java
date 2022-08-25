package com.wotos.wotosstatisticsservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wotos.wotosstatisticsservice.dto.PlayerPersonalData;
import com.wotos.wotosstatisticsservice.dto.TankStatistics;
import com.wotos.wotosstatisticsservice.util.CalculateStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class WotApiService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private CalculateStatistics calculateStatistics;

    @Value("${env.urls.wot_api}")
    private String WOT_API;
    @Value("${env.app_id}")
    private String APP_ID;
    @Value("${url.tank_statistics}")
    private String PLAYER_TANK_STATISTICS_URL;
    @Value("${url.player_info}")
    private String PLAYER_INFO_URL;

    public PlayerPersonalData fetchPlayerPersonalData(int playerID) {
        String result = restTemplate.getForObject(PLAYER_INFO_URL + APP_ID + "&account_id=" + playerID, String.class);

        try {
            JsonNode data = mapper.readTree(result).get("data").get(String.valueOf(playerID));
            PlayerPersonalData playerPersonalData = mapper.treeToValue(data, PlayerPersonalData.class);

            return playerPersonalData;
        } catch (IOException e) {
            System.out.println("Error parsing fetched player info: " + e.getMessage());
            return null;
        }
    }

    public List<TankStatistics> fetchAllTankStatisticsByPlayerID(int playerID) {
        String result = restTemplate.getForObject(PLAYER_TANK_STATISTICS_URL + APP_ID + "&account_id=" + playerID, String.class);
        List<TankStatistics> tankStats = new ArrayList<>();

        try {
            // Check for each tanks difference of battles more than 100
            JsonNode data = mapper.readTree(result).get("data").get(String.valueOf(playerID));

            data.forEach(value -> {
                try {
                    tankStats.add(mapper.treeToValue(value, TankStatistics.class));
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

    public TankStatistics fetchTankStatisticsByPlayerAndTankID(int playerID, int tankID) {
        String result = restTemplate.getForObject(PLAYER_TANK_STATISTICS_URL + APP_ID + "&account_id=" + playerID + "&tank_id=" + tankID, String.class);

        try {
            JsonNode data = mapper.readTree(result).get("data").get(String.valueOf(playerID)).get(0);
            TankStatistics tankStats = mapper.treeToValue(data, TankStatistics.class);

            return tankStats;
        } catch (IOException e) {
            System.out.println("Error processing tank statistics with tankID: " + tankID + " and playerID: " + playerID + " " + e.getMessage());
            return null;
        }

    }

}
