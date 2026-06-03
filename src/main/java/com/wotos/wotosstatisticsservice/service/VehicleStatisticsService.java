package com.wotos.wotosstatisticsservice.service;

import org.jetbrains.annotations.NotNull;
import com.wotos.wotosstatisticsservice.dao.ExpectedStatistics;
import com.wotos.wotosstatisticsservice.dao.VehicleStatisticsSnapshot;
import com.wotos.wotosstatisticsservice.repo.ExpectedStatisticsRepository;
import com.wotos.wotosstatisticsservice.repo.VehicleStatisticsSnapshotsRepository;
import com.wotos.wotosstatisticsservice.client.wot.WotPlayerVehiclesFeignClient;
import com.wotos.wotosstatisticsservice.client.xvm.XvmExpectedStatisticsFeignClient;
import com.wotos.wotosstatisticsservice.client.wot.statistics.WotStatistics;
import com.wotos.wotosstatisticsservice.client.wot.statistics.WotVehicleStatistics;
import com.wotos.wotosstatisticsservice.client.xvm.xvm.XvmExpectedStatistics;
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

    private final Wn8Calculator wn8Calculator;

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
            ExpectedStatisticsRepository expectedStatisticsRepository,

            Wn8Calculator wn8Calculator
    ) {
        this.wotPlayerVehiclesFeignClient = wotPlayerVehiclesFeignClient;
        this.xvmExpectedStatisticsFeignClient = xvmExpectedStatisticsFeignClient;

        this.vehicleStatisticsSnapshotsRepository = vehicleStatisticsSnapshotsRepository;
        this.expectedStatisticsRepository = expectedStatisticsRepository;

        this.wn8Calculator = wn8Calculator;
    }

    public Map<Integer, Map<Integer, Map<String, List<VehicleStatisticsSnapshot>>>> getPlayerVehicleStatisticsSnapshotsMap(Integer[] accountIds, Integer[] vehicleIds, String[] gameModes) {
        return vehicleStatisticsSnapshotsRepository.findAllPlayerVehicleStatisticsMapByAccountId(accountIds, vehicleIds, gameModes);
    }

    public Map<Integer, Map<Integer, Map<String, VehicleStatisticsSnapshot>>> createPlayerVehicleStatisticsSnapshots(Integer[] accountIds, Integer[] vehicleIds) {
        Map<Integer, List<WotVehicleStatistics>> wotVehicleStatisticsMap = fetchWotVehicleStatistics(accountIds, vehicleIds);
        Map<Integer, Map<Integer, Map<String, VehicleStatisticsSnapshot>>> vehicleStatisticsSnapshotsMapByPlayer = new HashMap<>();

        for (Integer accountId : accountIds) {
            List<WotVehicleStatistics> wotVehicleStatisticsList = wotVehicleStatisticsMap.get(accountId);
            Map<Integer, Map<String, VehicleStatisticsSnapshot>> vehicleStatisticsSnapshotsMapByVehicle = new HashMap<>();

            for (WotVehicleStatistics wotVehicleStatistics : wotVehicleStatisticsList) {
                Map<String, WotStatistics> wotStatisticsByGameModeMap = buildWotVehicleStatisticsByGameModeMap(wotVehicleStatistics);
                Map<String, VehicleStatisticsSnapshot> vehicleStatisticsSnapshotsByGameMode = new HashMap<>();
                Integer vehicleId = wotVehicleStatistics.getVehicleId();

                wotStatisticsByGameModeMap.forEach((gameMode, wotStatisticsByGameMode) -> {
                    if (wotStatisticsByGameMode != null) {
                        Integer maxBattles = vehicleStatisticsSnapshotsRepository.findHighestTotalBattlesByAccountIdAndVehicleId(accountId, vehicleId, gameMode).orElse(0);

                        if (wotStatisticsByGameMode.getBattles() - maxBattles > SNAPSHOT_RATE) {
                            // XVM may not publish expected statistics for every vehicle (new or
                            // removed tanks). Exclude such a vehicle from the snapshot rather than
                            // throwing — WN8 cannot be computed without expected values.
                            Optional<ExpectedStatistics> expectedStatistics = expectedStatisticsRepository.findById(vehicleId);
                            if (expectedStatistics.isEmpty()) {
                                return;
                            }

                            float wn8 = wn8Calculator.calculateWn8(wotStatisticsByGameMode, expectedStatistics.get());
                            VehicleStatisticsSnapshot vehicleStatisticsSnapshot = buildVehicleStatisticsSnapshotFromRaw(
                                    accountId, vehicleId, gameMode, wn8, wotStatisticsByGameMode
                            );

                            vehicleStatisticsSnapshotsByGameMode.put(gameMode, vehicleStatisticsSnapshot);
                            vehicleStatisticsSnapshotsRepository.save(vehicleStatisticsSnapshot);
                        }
                    }
                });

                vehicleStatisticsSnapshotsMapByVehicle.put(vehicleId, vehicleStatisticsSnapshotsByGameMode);
            }

            vehicleStatisticsSnapshotsMapByPlayer.put(accountId, vehicleStatisticsSnapshotsMapByVehicle);
        }

        return vehicleStatisticsSnapshotsMapByPlayer;
    }

    private static Map<String, WotStatistics> buildWotVehicleStatisticsByGameModeMap(WotVehicleStatistics wotVehicleStatistics) {
        Map<String, WotStatistics> vehicleStatisticsByGameModeMap = new HashMap<>();

        vehicleStatisticsByGameModeMap.put("regular_team", wotVehicleStatistics.getRegularTeam());
        vehicleStatisticsByGameModeMap.put("stronghold_skirmish", wotVehicleStatistics.getStrongholdSkirmish());
        vehicleStatisticsByGameModeMap.put("stronghold_defense", wotVehicleStatistics.getStrongholdDefense());
        vehicleStatisticsByGameModeMap.put("clan", wotVehicleStatistics.getClan());
        vehicleStatisticsByGameModeMap.put("all", wotVehicleStatistics.getAll());
        vehicleStatisticsByGameModeMap.put("company", wotVehicleStatistics.getCompany());
        vehicleStatisticsByGameModeMap.put("team", wotVehicleStatistics.getTeam());
        vehicleStatisticsByGameModeMap.put("epic", wotVehicleStatistics.getEpic());
        vehicleStatisticsByGameModeMap.put("fallout", wotVehicleStatistics.getFallout());
        vehicleStatisticsByGameModeMap.put("random", wotVehicleStatistics.getRandom());
        vehicleStatisticsByGameModeMap.put("ranked_battles", wotVehicleStatistics.getRankedBattles());

        return vehicleStatisticsByGameModeMap;
    }

    /**
     * Builds a vehicle snapshot from raw WoT statistics and a pre-computed WN8
     * rating (see {@link Wn8Calculator}). Derives the per-battle averages stored on
     * the snapshot but does not itself compute WN8.
     */
    private static VehicleStatisticsSnapshot buildVehicleStatisticsSnapshotFromRaw(
            @NotNull Integer accountId, @NotNull Integer vehicleId, @NotNull String gameMode,
            float wn8, @NotNull WotStatistics wotStatistics
    ) {
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
        float wins = wotStatistics.getWins();

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
            Integer[] accountIds, Integer[] vehicleIds
    ) {
        String[] extras = {"epic", "fallout", "random", "ranked_battles"};
        String[] fields = {};

        try {
            return Objects.requireNonNull(
                    wotPlayerVehiclesFeignClient.getPlayerVehicleStatistics(APP_ID, accountIds, "", extras, fields, null, "en", vehicleIds).getBody()
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
