package com.wotos.wotosstatisticsservice.service;

import org.jetbrains.annotations.NotNull;
import com.wotos.wotosstatisticsservice.dao.Granularity;
import com.wotos.wotosstatisticsservice.dao.PlayerStatisticsSnapshot;
import com.wotos.wotosstatisticsservice.dto.PlayerTrendResponse;
import com.wotos.wotosstatisticsservice.dto.TrendBucket;
import com.wotos.wotosstatisticsservice.dto.TrendPoint;
import com.wotos.wotosstatisticsservice.repo.PlayerStatisticsSnapshotsRepository;
import com.wotos.wotosstatisticsservice.repo.VehicleStatisticsSnapshotsRepository;
import com.wotos.wotosstatisticsservice.client.wot.WotAccountsFeignClient;
import com.wotos.wotosstatisticsservice.client.wot.player.WotPlayerDetails;
import com.wotos.wotosstatisticsservice.client.wot.statistics.WotPlayerStatistics;
import com.wotos.wotosstatisticsservice.client.wot.statistics.WotStatistics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

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

    public PlayerStatisticsService(
            WotAccountsFeignClient wotAccountsFeignClient,

            VehicleStatisticsSnapshotsRepository vehicleStatisticsSnapshotsRepository,
            PlayerStatisticsSnapshotsRepository playerStatisticsSnapshotsRepository
    ) {
        this.wotAccountsFeignClient = wotAccountsFeignClient;

        this.vehicleStatisticsSnapshotsRepository = vehicleStatisticsSnapshotsRepository;
        this.playerStatisticsSnapshotsRepository = playerStatisticsSnapshotsRepository;
    }

    /** Overall game mode used as the single WN8 series for the trend endpoint. */
    private static final String TREND_GAME_MODE = "all";
    /** Default trend window when {@code from} is not supplied (last 90 days). */
    private static final int DEFAULT_TREND_DAYS = 90;

    public Map<Integer, Map<String, List<PlayerStatisticsSnapshot>>> getPlayerStatisticsSnapshotsMap(Integer[] accountIds, String[] gameModes) {
        return playerStatisticsSnapshotsRepository.getPlayerStatisticsMap(accountIds, gameModes);
    }

    /**
     * Builds the player's overall WN8 trend over a date range, collapsing snapshots
     * into day or week buckets (keeping the latest snapshot in each bucket).
     *
     * @param accountId  the WoT account
     * @param from       inclusive start date (UTC); defaults to {@code to} minus 90 days when null
     * @param to         inclusive end date (UTC); defaults to today (UTC) when null
     * @param bucketParam {@code "day"} or {@code "week"}; defaults to day
     * @return the trend with points ordered oldest-first and {@code t} as ISO-8601 UTC
     * @throws IllegalArgumentException if {@code bucketParam} is not a valid bucket
     */
    public PlayerTrendResponse getPlayerWn8Trend(Integer accountId, LocalDate from, LocalDate to, String bucketParam) {
        TrendBucket bucket = TrendBucket.from(bucketParam);

        LocalDate toDate = (to != null) ? to : LocalDate.now(ZoneOffset.UTC);
        LocalDate fromDate = (from != null) ? from : toDate.minusDays(DEFAULT_TREND_DAYS);

        long fromEpoch = fromDate.atStartOfDay(ZoneOffset.UTC).toEpochSecond();
        long toEpoch = toDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond() - 1;

        List<PlayerStatisticsSnapshot> snapshots = playerStatisticsSnapshotsRepository
                .findAllByAccountIdAndGameModeAndCreateTimestampBetweenOrderByCreateTimestampAsc(
                        accountId, TREND_GAME_MODE, fromEpoch, toEpoch);

        // Collapse to the latest snapshot per bucket so each bucket yields one point.
        Map<Instant, PlayerStatisticsSnapshot> latestByBucket = new HashMap<>();
        for (PlayerStatisticsSnapshot snapshot : snapshots) {
            Instant bucketStart = bucketStart(snapshot.getCreateTimestamp(), bucket);
            latestByBucket.merge(bucketStart, snapshot,
                    (existing, candidate) -> candidate.getCreateTimestamp() >= existing.getCreateTimestamp() ? candidate : existing);
        }

        List<TrendPoint> points = latestByBucket.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new TrendPoint(
                        DateTimeFormatter.ISO_INSTANT.format(entry.getKey()),
                        entry.getValue().getTotalAverageWn8(),
                        entry.getValue().getTotalBattles()))
                .collect(Collectors.toList());

        return new PlayerTrendResponse(accountId, bucket.label(), points);
    }

    /** Start instant (UTC) of the day- or week-bucket containing {@code epochSecond}. */
    private static Instant bucketStart(long epochSecond, TrendBucket bucket) {
        LocalDate date = Instant.ofEpochSecond(epochSecond).atZone(ZoneOffset.UTC).toLocalDate();
        LocalDate bucketDate = (bucket == TrendBucket.WEEK)
                ? date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                : date;
        return bucketDate.atStartOfDay(ZoneOffset.UTC).toInstant();
    }

    public Map<Integer, Map<String, PlayerStatisticsSnapshot>> createPlayerStatisticsSnapshotsByAccountIds(Integer[] accountIds) {
        Map<Integer, Map<String, PlayerStatisticsSnapshot>> playerStatisticsSnapshotsMap = new HashMap<>();
        Map<Integer, WotPlayerDetails> wotPlayerDetailsMap = fetchWotPlayerDetails(accountIds);

        for (Integer accountId : accountIds) {
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
        }

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
        playerStatisticsSnapshot.setGranularity(Granularity.DAILY);
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
        String[] fields = {"statistics"};

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
