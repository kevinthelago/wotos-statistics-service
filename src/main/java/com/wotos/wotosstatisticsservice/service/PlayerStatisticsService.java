package com.wotos.wotosstatisticsservice.service;

import com.sun.istack.NotNull;
import com.wotos.wotosstatisticsservice.dao.PlayerStatistics;
import com.wotos.wotosstatisticsservice.dao.PlayerStatisticsSnapshot;
import com.wotos.wotosstatisticsservice.dao.StatisticsSnapshot;
import com.wotos.wotosstatisticsservice.repo.PlayerStatisticsRepository;
import com.wotos.wotosstatisticsservice.repo.PlayerStatisticsSnapshotsRepository;
import com.wotos.wotosstatisticsservice.repo.VehicleStatisticsSnapshotsRepository;
import com.wotos.wotosstatisticsservice.util.feign.wot.WotAccountsFeignClient;
import com.wotos.wotosstatisticsservice.util.model.wot.player.WotPlayerDetails;
import com.wotos.wotosstatisticsservice.util.model.wot.statistics.WotPlayerStatistics;
import com.wotos.wotosstatisticsservice.util.model.wot.statistics.WotStatistics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class PlayerStatisticsService {

    @Value("${env.app_id}")
    private String APP_ID;
    @Value("${env.snapshot_rate}")
    private Integer SNAPSHOT_RATE;
    @Value("${env.recent_wn8_timestamp}")
    private Long RECENT_WN8_TIMESTAMP;

    private final WotAccountsFeignClient wotAccountsFeignClient;

    private final VehicleStatisticsSnapshotsRepository vehicleStatisticsSnapshotsRepository;
    private final PlayerStatisticsSnapshotsRepository playerStatisticsSnapshotsRepository;
    private final PlayerStatisticsRepository playerStatisticsRepository;

    public PlayerStatisticsService(
            WotAccountsFeignClient wotAccountsFeignClient,

            VehicleStatisticsSnapshotsRepository vehicleStatisticsSnapshotsRepository,
            PlayerStatisticsSnapshotsRepository playerStatisticsSnapshotsRepository,
            PlayerStatisticsRepository playerStatisticsRepository
    ) {
        this.wotAccountsFeignClient = wotAccountsFeignClient;

        this.vehicleStatisticsSnapshotsRepository = vehicleStatisticsSnapshotsRepository;
        this.playerStatisticsSnapshotsRepository = playerStatisticsSnapshotsRepository;
        this.playerStatisticsRepository = playerStatisticsRepository;
    }

    public Map<Integer, PlayerStatistics> get(Integer[] accountIds) {
        Map<Integer, PlayerStatistics> playerStatisticsMap = new HashMap<>();

        for (Integer accountId : accountIds) {
            PlayerStatistics playerStatistics = playerStatisticsRepository.findById(accountId).get();
            playerStatisticsMap.put(accountId, playerStatistics);
        }

        return playerStatisticsMap;
    }

    public Map<Integer, PlayerStatistics> create(Integer[] accountIds) {
        Map<Integer, WotPlayerDetails> wotPlayerDetailsMap = fetchWotPlayerDetails(accountIds);
        Map<Integer, PlayerStatistics> playerStatisticsMap = new HashMap<>();

        for (Integer accountId : accountIds) {

            WotPlayerDetails wotPlayerDetails = wotPlayerDetailsMap.get(accountId);
            PlayerStatistics playerStatistics = buildPlayerStatistics(wotPlayerDetails);
            playerStatisticsRepository.save(playerStatistics);
            playerStatisticsMap.put(accountId, playerStatistics);

        }

        return playerStatisticsMap;
    }

//        CLAN("clan"),
//    ALL("all"),
//    REGULAR_TEAM("regular_team"),
//    COMPANY("company"),
//    STRONGHOLD_SKIRMISH("stronghold_skirmish"),
//    HISTORICAL("historical"),
//    TEAM("team"),
//    RANDOM("random"),
//    EPIC("epic"),
//    FALLOUT("fallout"),
//    RANKED_BATTLES("ranked_battles");

    private PlayerStatistics buildPlayerStatistics(WotPlayerDetails wotPlayerDetails) {
        PlayerStatistics playerStatistics = new PlayerStatistics();

        playerStatistics.setPlayerStatisticsId(wotPlayerDetails.getAccountId());
        playerStatistics.setClan(Arrays.asList(calculateStatisticsSnapshot(wotPlayerDetails.getAccountId(), wotPlayerDetails.getStatistics().getClan())));
        playerStatistics.setAll(Arrays.asList(calculateStatisticsSnapshot(wotPlayerDetails.getAccountId(), wotPlayerDetails.getStatistics().getAll())));
        playerStatistics.setRegularTeam(Arrays.asList(calculateStatisticsSnapshot(wotPlayerDetails.getAccountId(), wotPlayerDetails.getStatistics().getRegularTeam())));
        playerStatistics.setCompany(Arrays.asList(calculateStatisticsSnapshot(wotPlayerDetails.getAccountId(), wotPlayerDetails.getStatistics().getCompany())));
        playerStatistics.setStrongholdSkirmish(Arrays.asList(calculateStatisticsSnapshot(wotPlayerDetails.getAccountId(), wotPlayerDetails.getStatistics().getStrongholdSkirmish())));
        playerStatistics.setStrongholdDefense(Arrays.asList(calculateStatisticsSnapshot(wotPlayerDetails.getAccountId(), wotPlayerDetails.getStatistics().getStrongholdDefense())));
        playerStatistics.setHistorical(Arrays.asList(calculateStatisticsSnapshot(wotPlayerDetails.getAccountId(), wotPlayerDetails.getStatistics().getHistorical())));
        playerStatistics.setTeam(Arrays.asList(calculateStatisticsSnapshot(wotPlayerDetails.getAccountId(), wotPlayerDetails.getStatistics().getTeam())));
        playerStatistics.setRandom(Arrays.asList(calculateStatisticsSnapshot(wotPlayerDetails.getAccountId(), wotPlayerDetails.getStatistics().getRandom())));
        playerStatistics.setEpic(Arrays.asList(calculateStatisticsSnapshot(wotPlayerDetails.getAccountId(), wotPlayerDetails.getStatistics().getEpic())));
        playerStatistics.setFallout(Arrays.asList(calculateStatisticsSnapshot(wotPlayerDetails.getAccountId(), wotPlayerDetails.getStatistics().getFallout())));
//        playerStatistics.getRankedBattles().add(calculateStatisticsSnapshot(wotPlayerDetails.getAccountId(), wotPlayerDetails.getStatistics().getRankedBattles()));

        return playerStatistics;
    }

    private StatisticsSnapshot calculateStatisticsSnapshot(
            @NotNull Integer accountId, @NotNull WotStatistics wotStatistics
    ) {
        if (wotStatistics.getBattles() > 0) {
            float wins = wotStatistics.getWins();
            float battles = wotStatistics.getBattles();
            float survivedBattles = wotStatistics.getSurvivedBattles();
            float frags = wotStatistics.getFrags();
            float spotted = wotStatistics.getSpotted();
            float damage = wotStatistics.getDamageDealt();
            float damageTaken = wotStatistics.getDamageReceived();
            float dropperCapturePoints = wotStatistics.getDroppedCapturePoints();
            float xp = wotStatistics.getXp();
            float hits = wotStatistics.getHits();
            float shots = wotStatistics.getShots();
            float stunAssistedDamage = wotStatistics.getStunAssistedDamage();
            float capturePoints = wotStatistics.getCapturePoints();

            float winLossRatio = wins / battles;
            float deaths = battles - survivedBattles == 0 ? 1 : battles - survivedBattles;
            float hitMissRatio = shots > 0 ? hits / shots : 0;
            float killDeathRatio = frags / deaths;
            float averageKillsPerGame = frags / battles;
            float averageSpottingPerGame = spotted / battles;
            float averageDamagePerGame = damage / battles;
            float averageExperiencePerGame = xp / battles;
            float averageDamageReceivedPerGame = damageTaken / battles;
            float averageShotsPerGame = shots / battles;
            float averageStunAssistedDamage = stunAssistedDamage / battles;
            float averageCapturePointsPerGame = capturePoints / battles;
            float averageDroppedCapturePoints = dropperCapturePoints / battles;

            return buildStatisticsSnapshot(
                    (int) battles, (int) survivedBattles, killDeathRatio, hitMissRatio,
                    winLossRatio,  averageExperiencePerGame, averageDamagePerGame,
                    averageKillsPerGame, averageDamageReceivedPerGame, averageShotsPerGame,
                    averageStunAssistedDamage, averageCapturePointsPerGame,
                    averageDroppedCapturePoints, averageSpottingPerGame
            );
        }
        return null;
    }

    private static StatisticsSnapshot buildStatisticsSnapshot(
            Integer totalBattles, Integer survivedBattles, Float killDeathRatio,
            Float hitMissRatio, Float winLossRatio,
            Float averageExperience, Float averageDamage, Float averageKills,
            Float averageDamageReceived, Float averageShots,
            Float averageStunAssistedDamage, Float averageCapturePoints,
            Float averageDroppedCapturePoints, Float averageSpottingPerGame
    ) {
        StatisticsSnapshot statisticsSnapshot = new StatisticsSnapshot();

        statisticsSnapshot.setCreateTimestamp(Instant.now().getEpochSecond());
        statisticsSnapshot.setTotalBattles(totalBattles);
        statisticsSnapshot.setSurvivedBattles(survivedBattles);
        statisticsSnapshot.setKillDeathRatio(killDeathRatio);
        statisticsSnapshot.setHitMissRatio(hitMissRatio);
        statisticsSnapshot.setWinLossRatio(winLossRatio);
        statisticsSnapshot.setAverageWn8(0f);
        statisticsSnapshot.setAverageExperience(averageExperience);
        statisticsSnapshot.setAverageDamage(averageDamage);
        statisticsSnapshot.setAverageKills(averageKills);
        statisticsSnapshot.setAverageDamageReceived(averageDamageReceived);
        statisticsSnapshot.setAverageShots(averageShots);
        statisticsSnapshot.setAverageStunAssistedDamage(averageStunAssistedDamage);
        statisticsSnapshot.setAverageCapturePoints(averageCapturePoints);
        statisticsSnapshot.setAverageDroppedCapturePoints(averageDroppedCapturePoints);
        statisticsSnapshot.setAverageSpotting(averageSpottingPerGame);

        return statisticsSnapshot;
    }

    public Map<Integer, Map<String, List<PlayerStatisticsSnapshot>>> getPlayerStatisticsSnapshotsMap(Integer[] accountIds, String[] gameModes) {
        return playerStatisticsSnapshotsRepository.getPlayerStatisticsMap(accountIds, gameModes);
    }

    public Map<Integer, Map<String, PlayerStatisticsSnapshot>> createPlayerStatisticsSnapshotsByAccountIds(Integer[] accountIds) {
        Map<Integer, Map<String, PlayerStatisticsSnapshot>> playerStatisticsSnapshotsMap = new HashMap<>();
        Map<Integer, WotPlayerDetails> wotPlayerDetailsMap = fetchWotPlayerDetails(accountIds);

        wotPlayerDetailsMap.forEach((accountId, WotPlayerDetails) -> {
            WotPlayerDetails wotPlayerDetails = wotPlayerDetailsMap.get(accountId);
            Map<String, WotStatistics> wotStatisticsByGameModeMap = buildWotStatisticsByGameModeMap(wotPlayerDetails.getStatistics());
            Map<String, PlayerStatisticsSnapshot> playerStatisticsSnapshotsMapByGameMode = new HashMap<>();

            wotStatisticsByGameModeMap.forEach((gameMode, wotStatisticsByGameMode) -> {
                if (wotStatisticsByGameMode != null) {
                    Integer maxBattles = playerStatisticsSnapshotsRepository.findHighestTotalBattlesByAccountIdAndGameMode(accountId, gameMode).orElse(0);
                    Float totalAverageWn8 = vehicleStatisticsSnapshotsRepository.averageAverageWn8ByGameModeAndAccountId(accountId, gameMode).orElse(0f);
                    Float recentAverageWn8 = vehicleStatisticsSnapshotsRepository.averageRecentAverageWn8ByGameModeAndAccountId(accountId, gameMode, RECENT_WN8_TIMESTAMP).orElse(0f);

                    if (wotStatisticsByGameMode.getBattles() - maxBattles > SNAPSHOT_RATE) {
                        PlayerStatisticsSnapshot playerStatisticsSnapshot = calculatePlayerStatisticsSnapshot(accountId, gameMode, totalAverageWn8, recentAverageWn8, wotStatisticsByGameMode);

                        playerStatisticsSnapshotsMapByGameMode.put(gameMode, playerStatisticsSnapshot);
                        playerStatisticsSnapshotsRepository.save(playerStatisticsSnapshot);
                    }
                }
            });

            playerStatisticsSnapshotsMap.put(accountId, playerStatisticsSnapshotsMapByGameMode);
        });

//        for (Integer accountId : accountIds) {
//            WotPlayerDetails wotPlayerDetails = wotPlayerDetailsMap.get(accountId);
//            Map<String, WotStatistics> wotStatisticsByGameModeMap = buildWotStatisticsByGameModeMap(wotPlayerDetails.getStatistics());
//            Map<String, PlayerStatisticsSnapshot> playerStatisticsSnapshotsMapByGameMode = new HashMap<>();
//
//            wotStatisticsByGameModeMap.forEach((gameMode, wotStatisticsByGameMode) -> {
//                if (wotStatisticsByGameMode != null) {
//                    Integer maxBattles = playerStatisticsSnapshotsRepository.findHighestTotalBattlesByAccountIdAndGameMode(accountId, gameMode).orElse(0);
//                    Float totalAverageWn8 = vehicleStatisticsSnapshotsRepository.averageAverageWn8ByGameModeAndAccountId(accountId, gameMode).orElse(0f);
//                    Float recentAverageWn8 = vehicleStatisticsSnapshotsRepository.averageRecentAverageWn8ByGameModeAndAccountId(accountId, gameMode, RECENT_WN8_TIMESTAMP).orElse(0f);
//
//                    if (wotStatisticsByGameMode.getBattles() - maxBattles > SNAPSHOT_RATE) {
//                        PlayerStatisticsSnapshot playerStatisticsSnapshot = calculatePlayerStatisticsSnapshot(accountId, gameMode, totalAverageWn8, recentAverageWn8, wotStatisticsByGameMode);
//
//                        playerStatisticsSnapshotsMapByGameMode.put(gameMode, playerStatisticsSnapshot);
//                        playerStatisticsSnapshotsRepository.save(playerStatisticsSnapshot);
//                    }
//                }
//            });
//
//            playerStatisticsSnapshotsMap.put(accountId, playerStatisticsSnapshotsMapByGameMode);
//        }

        return playerStatisticsSnapshotsMap;
    }

    private static Map<String, WotStatistics> buildWotStatisticsByGameModeMap(WotPlayerStatistics wotPlayerStatistics) {
        Map<String, WotStatistics> playerStatisticsByGameModeMap = new HashMap<>();

        playerStatisticsByGameModeMap.put("regular_team", wotPlayerStatistics.getRegularTeam());
        playerStatisticsByGameModeMap.put("stronghold_skirmish", wotPlayerStatistics.getStrongholdSkirmish());
        playerStatisticsByGameModeMap.put("stronghold_defense", wotPlayerStatistics.getStrongholdDefense());
        playerStatisticsByGameModeMap.put("clan", wotPlayerStatistics.getClan());
        playerStatisticsByGameModeMap.put("all", wotPlayerStatistics.getAll());
        playerStatisticsByGameModeMap.put("company", wotPlayerStatistics.getCompany());
        playerStatisticsByGameModeMap.put("team", wotPlayerStatistics.getTeam());
        playerStatisticsByGameModeMap.put("epic", wotPlayerStatistics.getEpic());
        playerStatisticsByGameModeMap.put("fallout", wotPlayerStatistics.getFallout());
        playerStatisticsByGameModeMap.put("random", wotPlayerStatistics.getRandom());
        playerStatisticsByGameModeMap.put("ranked_battles", wotPlayerStatistics.getRankedBattles());

        return playerStatisticsByGameModeMap;
    }

    private static PlayerStatisticsSnapshot calculatePlayerStatisticsSnapshot(
            @NotNull Integer accountId, @NotNull String gameMode, @NotNull Float totalAverageWn8,
            @NotNull Float recentAverageWn8, @NotNull WotStatistics wotStatistics
    ) {
        float wins = wotStatistics.getWins();
        float battles = wotStatistics.getBattles();
        float survivedBattles = wotStatistics.getSurvivedBattles();
        float frags = wotStatistics.getFrags();
        float spotted = wotStatistics.getSpotted();
        float damage = wotStatistics.getDamageDealt();
        float damageTaken = wotStatistics.getDamageReceived();
        float dropperCapturePoints = wotStatistics.getDroppedCapturePoints();
        float xp = wotStatistics.getXp();
        float hits = wotStatistics.getHits();
        float shots = wotStatistics.getShots();
        float stunAssistedDamage = wotStatistics.getStunAssistedDamage();
        float capturePoints = wotStatistics.getCapturePoints();

        float winLossRatio = wins / battles;
        float deaths = battles - survivedBattles == 0 ? 1 : battles - survivedBattles;
        float hitMissRatio = shots > 0 ? hits / shots : 0;
        float killDeathRatio = frags / deaths;
        float averageKillsPerGame = frags / battles;
        float averageSpottingPerGame = spotted / battles;
        float averageDamagePerGame = damage / battles;
        float averageExperiencePerGame = xp / battles;
        float averageDamageReceivedPerGame = damageTaken / battles;
        float averageShotsPerGame = shots / battles;
        float averageStunAssistedDamage = stunAssistedDamage / battles;
        float averageCapturePointsPerGame = capturePoints / battles;
        float averageDroppedCapturePoints = dropperCapturePoints / battles;

        return buildPlayerStatisticsSnapshot(
                accountId, gameMode, (int) battles, (int) survivedBattles, killDeathRatio, hitMissRatio,
                winLossRatio, totalAverageWn8, recentAverageWn8, averageExperiencePerGame, averageDamagePerGame,
                averageKillsPerGame, averageDamageReceivedPerGame, averageShotsPerGame,
                averageStunAssistedDamage, averageCapturePointsPerGame,
                averageDroppedCapturePoints, averageSpottingPerGame
        );
    }

    private static PlayerStatisticsSnapshot buildPlayerStatisticsSnapshot(
            Integer accountId, String gameMode, Integer totalBattles, Integer survivedBattles, Float killDeathRatio,
            Float hitMissRatio, Float winLossRatio, Float totalAverageWn8, Float recentAverageWn8,
            Float averageExperience, Float averageDamage, Float averageKills,
            Float averageDamageReceived, Float averageShots,
            Float averageStunAssistedDamage, Float averageCapturePoints,
            Float averageDroppedCapturePoints, Float averageSpottingPerGame
    ) {
        PlayerStatisticsSnapshot playerStatisticsSnapshot = new PlayerStatisticsSnapshot();

        playerStatisticsSnapshot.setAccountId(accountId);
        playerStatisticsSnapshot.setGameMode(gameMode);
        playerStatisticsSnapshot.setCreateTimestamp(Instant.now().getEpochSecond());
        playerStatisticsSnapshot.setTotalBattles(totalBattles);
        playerStatisticsSnapshot.setSurvivedBattles(survivedBattles);
        playerStatisticsSnapshot.setKillDeathRatio(killDeathRatio);
        playerStatisticsSnapshot.setHitMissRatio(hitMissRatio);
        playerStatisticsSnapshot.setWinLossRatio(winLossRatio);
        playerStatisticsSnapshot.setTotalAverageWn8(totalAverageWn8);
        playerStatisticsSnapshot.setRecentAverageWn8(recentAverageWn8);
        playerStatisticsSnapshot.setAverageExperience(averageExperience);
        playerStatisticsSnapshot.setAverageDamage(averageDamage);
        playerStatisticsSnapshot.setAverageKills(averageKills);
        playerStatisticsSnapshot.setAverageDamageReceived(averageDamageReceived);
        playerStatisticsSnapshot.setAverageShots(averageShots);
        playerStatisticsSnapshot.setAverageStunAssistedDamage(averageStunAssistedDamage);
        playerStatisticsSnapshot.setAverageCapturePoints(averageCapturePoints);
        playerStatisticsSnapshot.setAverageDroppedCapturePoints(averageDroppedCapturePoints);
        playerStatisticsSnapshot.setAverageSpotting(averageSpottingPerGame);

        return playerStatisticsSnapshot;
    }

    private Map<Integer, WotPlayerDetails> fetchWotPlayerDetails(
            Integer[] accountIds
    ) {
        String[] extras = {"statistics.epic","statistics.fallout","statistics.random","statistics.ranked_battles"};
        String[] fields = {"statistics","account_id"};

        try {
            return Objects.requireNonNull(
                    wotAccountsFeignClient.getPlayerDetails(APP_ID, "", extras, fields, "en", accountIds).getBody()
            ).getData();
        } catch (NullPointerException e) {
            System.out.println("Couldn't fetch WotPlayerDetails with accountId: " + accountIds + "\n" + e.getStackTrace());
            return null;
        }
    }

}
