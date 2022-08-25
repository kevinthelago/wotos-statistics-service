package com.wotos.wotosstatisticsservice.dao;

import javax.persistence.*;

@Entity
@Table(schema = "statistics_snapshots")
public class StatisticsSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "snapshot_id", nullable = false, unique = true)
    private Integer snapshotId;
    @Column(name = "player_id", nullable = false)
    private Integer playerId;
    @Column(name = "tank_id")
    private Integer tankId;
    @Column(name = "personal_rating")
    private int personalRating;
    @Column(name = "averageWn8")
    private Float averageWn8;
    @Column(name = "averageExperience")
    private Float averageExperience;
    @Column(name = "hit_ratio")
    private Float hitRatio;
    @Column(name = "kill_ratio")
    private Float killRatio;
    @Column(name = "win_rate")
    private Float winRate;
    @Column(name = "damage_per_game")
    private Float damagePerGame;
    @Column(name = "average_kills_per_game")
    private Float averageKillsPerGame;
    @Column(name = "total_battles")
    private Integer totalBattles;

    public Integer getSnapshotId() {
        return snapshotId;
    }

    public void setSnapshotId(Integer snapshotId) {
        this.snapshotId = snapshotId;
    }

    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    public Integer getTankId() {
        return tankId;
    }

    public void setTankId(Integer tankId) {
        this.tankId = tankId;
    }

    public int getPersonalRating() {
        return personalRating;
    }

    public void setPersonalRating(int personalRating) {
        this.personalRating = personalRating;
    }

    public Float getAverageWn8() {
        return averageWn8;
    }

    public void setAverageWn8(Float averageWn8) {
        this.averageWn8 = averageWn8;
    }

    public Float getAverageExperience() {
        return averageExperience;
    }

    public void setAverageExperience(Float averageExperience) {
        this.averageExperience = averageExperience;
    }

    public Float getHitRatio() {
        return hitRatio;
    }

    public void setHitRatio(Float hitRatio) {
        this.hitRatio = hitRatio;
    }

    public Float getKillRatio() {
        return killRatio;
    }

    public void setKillRatio(Float killRatio) {
        this.killRatio = killRatio;
    }

    public Float getWinRate() {
        return winRate;
    }

    public void setWinRate(Float winRate) {
        this.winRate = winRate;
    }

    public Float getDamagePerGame() {
        return damagePerGame;
    }

    public void setDamagePerGame(Float damagePerGame) {
        this.damagePerGame = damagePerGame;
    }

    public Float getAverageKillsPerGame() {
        return averageKillsPerGame;
    }

    public void setAverageKillsPerGame(Float averageKillsPerGame) {
        this.averageKillsPerGame = averageKillsPerGame;
    }

    public Integer getTotalBattles() {
        return totalBattles;
    }

    public void setTotalBattles(Integer totalBattles) {
        this.totalBattles = totalBattles;
    }
}
