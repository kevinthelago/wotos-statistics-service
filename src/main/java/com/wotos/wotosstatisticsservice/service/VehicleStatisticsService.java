package com.wotos.wotosstatisticsservice.service;

import com.sun.istack.NotNull;
import com.wotos.wotosstatisticsservice.dao.ExpectedStatistics;
import com.wotos.wotosstatisticsservice.dao.VehicleStatisticsSnapshot;
import com.wotos.wotosstatisticsservice.repo.ExpectedStatisticsRepository;
import com.wotos.wotosstatisticsservice.repo.VehicleStatisticsSnapshotsRepository;
import com.wotos.wotosstatisticsservice.util.feign.wot.WotPlayerVehiclesFeignClient;
import com.wotos.wotosstatisticsservice.util.feign.xvm.XvmExpectedStatisticsFeignClient;
import com.wotos.wotosstatisticsservice.util.model.wot.statistics.WotStatisticsByGameMode;
import com.wotos.wotosstatisticsservice.util.model.wot.statistics.WotVehicleStatistics;
import com.wotos.wotosstatisticsservice.util.model.xvm.XvmExpectedStatistics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class VehicleStatisticsService {

    @Value("${env.app_id}")
    private String APP_ID;
    @Value("${env.snapshot_rate}")
    private Integer SNAPSHOT_RATE;

    private final WotPlayerVehiclesFeignClient wotPlayerVehiclesFeignClient;
    private final XvmExpectedStatisticsFeignClient xvmExpectedStatisticsFeignClient;

    private final VehicleStatisticsSnapshotsRepository vehicleStatisticsSnapshotsRepository;
    private final ExpectedStatisticsRepository expectedStatisticsRepository;

//    @PostConstruct
    public void init() {
        List<ExpectedStatistics> expectedStatistics = expectedStatisticsRepository.findAll();

        // ToDo: Validate Expected Statistics is current/hasn't changed
        if (expectedStatistics.size() == 0) {
            initExpectedStatistics();
        }
    }

    public VehicleStatisticsService(
            WotPlayerVehiclesFeignClient wotPlayerVehiclesFeignClient,
            XvmExpectedStatisticsFeignClient xvmExpectedStatisticsFeignClient,

            VehicleStatisticsSnapshotsRepository vehicleStatisticsSnapshotsRepository,
            ExpectedStatisticsRepository expectedStatisticsRepository
    ) {
        this.wotPlayerVehiclesFeignClient = wotPlayerVehiclesFeignClient;
        this.xvmExpectedStatisticsFeignClient = xvmExpectedStatisticsFeignClient;

        this.vehicleStatisticsSnapshotsRepository = vehicleStatisticsSnapshotsRepository;
        this.expectedStatisticsRepository = expectedStatisticsRepository;
    }

    public Map<Integer, Map<Integer, Map<String, List<VehicleStatisticsSnapshot>>>> getPlayerVehicleStatisticsSnapshotsMap(Integer[] accountIds, Integer[] vehicleIds, String[] gameModes) {
        Map<Integer, Map<Integer, Map<String, List<VehicleStatisticsSnapshot>>>> playerVehicleStatisticsSnapshotsMapByAccountIdsAndVehicleIdAndGameMode = new HashMap<>();

        for (Integer accountId : accountIds) {
            Map<Integer, Map<String, List<VehicleStatisticsSnapshot>>> playerVehicleStatisticsSnapshotsMapByVehicleIdAndGameMode = new HashMap<>();

            for (Integer vehicleId : vehicleIds) {
                Map<String, List<VehicleStatisticsSnapshot>> playerVehicleStatisticsSnapshotsMapByGameMode = new HashMap<>();

                for (String gameMode : gameModes) {
                    List<VehicleStatisticsSnapshot> vehicleStatisticsSnapshotListByGameMode = vehicleStatisticsSnapshotsRepository.findByAccountIdAndVehicleIdAndGameMode(accountId, vehicleId, gameMode).orElse(new ArrayList<>());
                    playerVehicleStatisticsSnapshotsMapByGameMode.put(gameMode, vehicleStatisticsSnapshotListByGameMode);
                }

                playerVehicleStatisticsSnapshotsMapByVehicleIdAndGameMode.put(vehicleId, playerVehicleStatisticsSnapshotsMapByGameMode);
            }

            playerVehicleStatisticsSnapshotsMapByAccountIdsAndVehicleIdAndGameMode.put(accountId, playerVehicleStatisticsSnapshotsMapByVehicleIdAndGameMode);
        }

        return playerVehicleStatisticsSnapshotsMapByAccountIdsAndVehicleIdAndGameMode;
    }

    public Map<Integer, Map<Integer, Map<String, VehicleStatisticsSnapshot>>> createPlayerVehicleStatisticsSnapshots(Integer[] accountIds, Integer[] vehicleIds) {
        Map<Integer, List<WotVehicleStatistics>> wotVehicleStatisticsMap = fetchWotVehicleStatistics(accountIds, "", null, null, null, "", vehicleIds);
        Map<Integer, Map<Integer, Map<String, VehicleStatisticsSnapshot>>> vehicleStatisticsSnapshotsMapByPlayer = new HashMap<>();

        for (Integer accountId : accountIds) {
            List<WotVehicleStatistics> wotVehicleStatisticsList = wotVehicleStatisticsMap.get(accountId);
            Map<Integer, Map<String, VehicleStatisticsSnapshot>> vehicleStatisticsSnapshotsMapByVehicle = new HashMap<>();

            for (WotVehicleStatistics wotVehicleStatistics : wotVehicleStatisticsList) {
                Map<String, WotStatisticsByGameMode> wotStatisticsByGameModeMap = generateVehicleStatisticsByGameModeMap(wotVehicleStatistics);
                Map<String, VehicleStatisticsSnapshot> vehicleStatisticsSnapshotsByGameMode = new HashMap<>();
                Integer vehicleId = wotVehicleStatistics.getVehicleId();

                wotStatisticsByGameModeMap.forEach((gameMode, wotStatisticsByGameMode) -> {
                    Integer maxBattles = vehicleStatisticsSnapshotsRepository.findHighestTotalBattlesByAccountIdAndVehicleId(accountId, vehicleId, gameMode).orElse(0);

                    if (wotStatisticsByGameMode.getBattles() - maxBattles > SNAPSHOT_RATE) {
                        ExpectedStatistics expectedStatistics = expectedStatisticsRepository.findById(vehicleId).get();
                        VehicleStatisticsSnapshot vehicleStatisticsSnapshot = calculateVehicleStatisticsSnapshot(
                                accountId, vehicleId, gameMode, wotStatisticsByGameMode, expectedStatistics
                        );

                        vehicleStatisticsSnapshotsByGameMode.put(gameMode, vehicleStatisticsSnapshot);
                        vehicleStatisticsSnapshotsRepository.save(vehicleStatisticsSnapshot);
                    }
                });

                vehicleStatisticsSnapshotsMapByVehicle.put(vehicleId, vehicleStatisticsSnapshotsByGameMode);
            }

            vehicleStatisticsSnapshotsMapByPlayer.put(accountId, vehicleStatisticsSnapshotsMapByVehicle);
        }

        return vehicleStatisticsSnapshotsMapByPlayer;
    }

    private static Map<String, WotStatisticsByGameMode> generateVehicleStatisticsByGameModeMap(WotVehicleStatistics wotVehicleStatistics) {
        Map<String, WotStatisticsByGameMode> vehicleStatisticsByGameModeMap = new HashMap<>();

        vehicleStatisticsByGameModeMap.put("regular_team", wotVehicleStatistics.getRegularTeam());
        vehicleStatisticsByGameModeMap.put("stronghold_skirmish", wotVehicleStatistics.getStrongholdSkirmish());
        vehicleStatisticsByGameModeMap.put("stronghold_defense", wotVehicleStatistics.getStrongholdDefense());
        vehicleStatisticsByGameModeMap.put("clan", wotVehicleStatistics.getClan());
        vehicleStatisticsByGameModeMap.put("all", wotVehicleStatistics.getAll());
        vehicleStatisticsByGameModeMap.put("company", wotVehicleStatistics.getCompany());
//        vehicleStatisticsByGameModeMap.put("global_map", wotVehicleStatistics.getGlobalmap()); // ToDo: Figure this out.
        vehicleStatisticsByGameModeMap.put("team", wotVehicleStatistics.getTeam());

        return vehicleStatisticsByGameModeMap;
    }

    private static VehicleStatisticsSnapshot calculateVehicleStatisticsSnapshot(
            @NotNull Integer accountId, @NotNull Integer vehicleId, @NotNull String gameMode,
            @NotNull WotStatisticsByGameMode wotStatisticsByGameMode,
            @NotNull ExpectedStatistics expectedStatistics
    ) {
        float wins = wotStatisticsByGameMode.getWins();
        float battles = wotStatisticsByGameMode.getBattles();
        float survivedBattles = wotStatisticsByGameMode.getSurvivedBattles();
        float frags = wotStatisticsByGameMode.getFrags();
        float spotted = wotStatisticsByGameMode.getSpotted();
        float damage = wotStatisticsByGameMode.getDamageDealt();
        float damageTaken = wotStatisticsByGameMode.getDamageReceived();
        float dropperCapturePoints = wotStatisticsByGameMode.getDroppedCapturePoints();
        float xp = wotStatisticsByGameMode.getXp();
        float hits = wotStatisticsByGameMode.getHits();
        float shots = wotStatisticsByGameMode.getShots();
        float stunAssistedDamage = wotStatisticsByGameMode.getStunAssistedDamage();
        float capturePoints = wotStatisticsByGameMode.getCapturePoints();

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

        float tune = 10000;

        float DAMAGE = Math.round((averageDamagePerGame / expectedStatistics.getExpectedDamage()) * tune) / tune;
        float SPOT = Math.round((averageSpottingPerGame / expectedStatistics.getExpectedSpot()) * tune) / tune;
        float FRAG = Math.round((averageKillsPerGame / expectedStatistics.getExpectedFrag()) * tune) / tune;
        float DEFENSE = Math.round(((averageDroppedCapturePoints) / expectedStatistics.getExpectedDefense()) * tune) / tune;
        float WIN = Math.round((winLossRatio / expectedStatistics.getExpectedWinRate()) * (tune * 100)) / tune;

        float DAMAGEc = (float) Math.max(0, (DAMAGE - 0.22) / 0.78);
        float SPOTc = (float) Math.max(0, Math.min(DAMAGEc + 0.1, (SPOT - 0.38) / 0.62));
        float FRAGc = (float) Math.max(0, Math.min(DAMAGEc + 0.2, (FRAG - 0.12) / 0.88));
        float DEFENSEc = (float) Math.max(0, Math.min(DAMAGEc + 0.1, (DEFENSE - 0.10) / 0.9));
        float WINc = (float) Math.max(0, (WIN - 0.71) / 0.29);

        float wn8 = (float) ((980 * DAMAGEc) + (210 * DAMAGEc * FRAGc) + (155 * FRAGc * SPOTc) + (75 * DEFENSEc * FRAGc) + (145 * Math.min(1.8, WINc)));

        return buildVehicleStatisticsSnapshot(
                accountId, vehicleId, gameMode, wn8, (int) battles, killDeathRatio, hitMissRatio, winLossRatio,
                averageExperiencePerGame, averageDamagePerGame, averageKillsPerGame,
                averageDamageReceivedPerGame, averageShotsPerGame, averageStunAssistedDamage,
                averageCapturePointsPerGame, averageDroppedCapturePoints, (int) survivedBattles, averageSpottingPerGame
        );
    }

    private static VehicleStatisticsSnapshot buildVehicleStatisticsSnapshot(
            Integer accountId, Integer vehicleId, String gameMode, Float wn8, Integer battles, Float killDeathRatio, Float hitMissRatio, Float winLossRatio,
            Float averageExperiencePerGame, Float averageDamagePerGame, Float averageKillsPerGame,
            Float averageDamageReceivedPerGame, Float averageShotsPerGame, Float averageStunAssistedDamage,
            Float averageCapturePointsPerGame, Float averageDroppedCapturePoints, Integer survivedBattles, Float averageSpottingPerGame
    ) {
        VehicleStatisticsSnapshot vehicleStatisticsSnapshot = new VehicleStatisticsSnapshot();

        vehicleStatisticsSnapshot.setAccountId(accountId);
        vehicleStatisticsSnapshot.setVehicleId(vehicleId);
        vehicleStatisticsSnapshot.setGameMode(gameMode);
        vehicleStatisticsSnapshot.setTotalBattles(battles);
        vehicleStatisticsSnapshot.setCreateTimestamp(Instant.now().getEpochSecond());
        vehicleStatisticsSnapshot.setSurvivedBattles(survivedBattles);
        vehicleStatisticsSnapshot.setAverageWn8(wn8);
        vehicleStatisticsSnapshot.setKillDeathRatio(killDeathRatio);
        vehicleStatisticsSnapshot.setHitMissRatio(hitMissRatio);
        vehicleStatisticsSnapshot.setWinLossRatio(winLossRatio);
        vehicleStatisticsSnapshot.setAverageExperience(averageExperiencePerGame);
        vehicleStatisticsSnapshot.setAverageDamage(averageDamagePerGame);
        vehicleStatisticsSnapshot.setAverageKills(averageKillsPerGame);
        vehicleStatisticsSnapshot.setAverageDamageReceived(averageDamageReceivedPerGame);
        vehicleStatisticsSnapshot.setAverageShots(averageShotsPerGame);
        vehicleStatisticsSnapshot.setAverageStunAssistedDamage(averageStunAssistedDamage);
        vehicleStatisticsSnapshot.setAverageCapturePoints(averageCapturePointsPerGame);
        vehicleStatisticsSnapshot.setAverageDroppedCapturePoints(averageDroppedCapturePoints);
        vehicleStatisticsSnapshot.setAverageSpotting(averageSpottingPerGame);

        return vehicleStatisticsSnapshot;
    }

    private Map<Integer, List<WotVehicleStatistics>> fetchWotVehicleStatistics(
            Integer[] accountIds, String accessToken, String[] extra,
            String[] fields, Integer inGarage, String language, Integer[] vehicleIds
    ) {
        try {
            return Objects.requireNonNull(
                    wotPlayerVehiclesFeignClient.getPlayerVehicleStatistics(APP_ID, accountIds, accessToken, extra, fields, inGarage, language, vehicleIds).getBody()
            ).getData();
        } catch (NullPointerException e) {
            System.out.println("Couldn't fetch WotVehicleStatistics with accountIds: " + accountIds + " and vehicleIds: " + vehicleIds.toString() + "\n" + e.getStackTrace());
            return new HashMap<>();
        }
    }

    private void initExpectedStatistics() {
        List<XvmExpectedStatistics> xvmExpectedStatistics = Objects.requireNonNull(
                xvmExpectedStatisticsFeignClient.getExpectedStatistics().getBody()
        ).getData();
        List<ExpectedStatistics> expectedStatistics = buildExpectedStatistics(xvmExpectedStatistics);

        expectedStatistics.forEach(expectedStatisticsRepository::save);
    }

    private static List<ExpectedStatistics> buildExpectedStatistics(List<XvmExpectedStatistics> xvmExpectedStatistics) {
        List<ExpectedStatistics> expectedStatisticsList = new ArrayList<>();

        xvmExpectedStatistics.forEach(xvm -> {
            ExpectedStatistics expectedStatistics = new ExpectedStatistics();

            expectedStatistics.setVehicleId(xvm.getVehicleId());
            expectedStatistics.setExpectedDefense(xvm.getExpectedDefense());
            expectedStatistics.setExpectedFrag(xvm.getExpectedFrag());
            expectedStatistics.setExpectedSpot(xvm.getExpectedSpot());
            expectedStatistics.setExpectedDamage(xvm.getExpectedDamage());
            expectedStatistics.setExpectedWinRate(xvm.getExpectedWinRate());

            expectedStatisticsList.add(expectedStatistics);
        });

        return expectedStatisticsList;
    }

}
