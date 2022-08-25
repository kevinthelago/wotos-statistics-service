package com.wotos.wotosstatisticsservice.util;

import com.sun.istack.NotNull;
import com.wotos.wotosstatisticsservice.dao.ExpectedStatistics;
import com.wotos.wotosstatisticsservice.dao.StatisticsSnapshot;
import com.wotos.wotosstatisticsservice.dto.TankStatistics;
import com.wotos.wotosstatisticsservice.repo.ExpectedStatisticsRepository;
import org.springframework.stereotype.Component;

@Component
public class CalculateStatistics {

    private final ExpectedStatisticsRepository expectedStatisticsRepository;

    public CalculateStatistics(
            ExpectedStatisticsRepository expectedStatisticsRepository
    ) {
        this.expectedStatisticsRepository = expectedStatisticsRepository;
    }

    public StatisticsSnapshot calculateTankStatisticsSnapshot(int playerID, @NotNull TankStatistics tankStatistics) {
        ExpectedStatistics expectedStatistics = expectedStatisticsRepository.findById(tankStatistics.getTankId()).get();

        float wins = tankStatistics.getAll().getWins();
        float battles = tankStatistics.getAll().getBattles();
        float survivedBattles = tankStatistics.getAll().getSurvivedBattles();
        float frags = tankStatistics.getAll().getFrags();
        float spotted = tankStatistics.getAll().getSpotted();
        float damage = tankStatistics.getAll().getDamageDealt();
        float defense = tankStatistics.getAll().getDroppedCapturePoints();
        float xp = tankStatistics.getAll().getXp();
        float hits = tankStatistics.getAll().getHits();
        float shots = tankStatistics.getAll().getShots();

        float winRate = wins / battles;
        float deaths = battles - survivedBattles == 0 ? 1 : battles - survivedBattles;
        float killRatio = frags / deaths;
        float hitRatio = hits / shots;
        float avgKPG = frags / battles;
        float avgSpot = spotted / battles;
        float avgDPG = damage / battles;
        float avgXP = xp / battles;

        float tune = 10000;

        float DAMAGE = Math.round((avgDPG / expectedStatistics.getExpected_damage()) * tune) / tune;
        float SPOT = Math.round((avgSpot / expectedStatistics.getExpected_spot()) * tune) / tune;
        float FRAG = Math.round((avgKPG / expectedStatistics.getExpected_frag()) * tune) / tune;
        float DEFENSE = Math.round(((defense / battles) / expectedStatistics.getExpected_defense()) * tune) / tune;
        float WIN = Math.round((winRate / expectedStatistics.getExpected_win_rate()) * (tune * 100)) / tune;

        float DAMAGEc = (float) Math.max(0, (DAMAGE - 0.22) / 0.78);
        float SPOTc = (float) Math.max(0, Math.min(DAMAGEc + 0.1, (SPOT - 0.38) / 0.62));
        float FRAGc = (float) Math.max(0, Math.min(DAMAGEc + 0.2, (FRAG - 0.12) / 0.88));
        float DEFENSEc = (float) Math.max(0, Math.min(DAMAGEc + 0.1, (DEFENSE - 0.10) / 0.9));
        float WINc = (float) Math.max(0, (WIN - 0.71) / 0.29);

        float wn8 = (float) ((980 * DAMAGEc) + (210 * DAMAGEc * FRAGc) + (155 * FRAGc * SPOTc) + (75 * DEFENSEc * FRAGc) + (145 * Math.min(1.8, WINc)));
        int tankID = tankStatistics.getTankId();

        StatisticsSnapshot statisticsSnapShot = buildTankStatisticsSnapshot(
                playerID, tankID, wn8, (int) battles, killRatio, avgXP, hitRatio, avgDPG, avgKPG, winRate
        );

//        saveTankStatistics(playerID, tankID, tankStatistics);

        return statisticsSnapShot;
    }

    private StatisticsSnapshot buildTankStatisticsSnapshot(
            Integer playerID, Integer tankID, Float wn8,
            Integer battles, Float killRatio, Float xp,
            Float hitRatio, Float avgDPG, Float avgKPG, Float winRate
    ) {
        StatisticsSnapshot statisticsSnapShot = new StatisticsSnapshot();
        statisticsSnapShot.setPlayerId(playerID);
        statisticsSnapShot.setTankId(tankID);
//        statisticsSnapShot.setPersonalRating();
        statisticsSnapShot.setAverageWn8(wn8);
        statisticsSnapShot.setAverageExperience(xp);
        statisticsSnapShot.setHitRatio(hitRatio);
        statisticsSnapShot.setKillRatio(killRatio);
        statisticsSnapShot.setWinRate(winRate);
        statisticsSnapShot.setDamagePerGame(avgDPG);
        statisticsSnapShot.setAverageKillsPerGame(avgKPG);
        statisticsSnapShot.setTotalBattles(battles);

        return statisticsSnapShot;
    }

//    private void saveTankStatistics(Integer playerID, Integer tankID, TankStatistics tankStatistics) {
//        Optional<Integer> tankInDB = tankStatisticsRepository.findMaxBattlesByPlayerAndTankID(playerID, tankID);
//
//        // Todo: move 100 battle snapshot check to before calculations
//        if (!tankInDB.isPresent() || tankStatistics.getBattles() - tankInDB.get() >= 100) {
//            tankStatisticsRepository.save(tankStatistics);
//        }
//    }

}
