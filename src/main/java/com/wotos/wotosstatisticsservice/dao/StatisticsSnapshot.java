package com.wotos.wotosstatisticsservice.dao;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
@Table(name = "statistics_snapshots")
public class StatisticsSnapshot {
    @Id
    @GeneratedValue
    @Column(name = "statistics_snapshot_id")
    private Integer statisticsSnapshotId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_statistics_id")
    @JsonBackReference(value = "vehicle-statistics_statistics-snapshots")
    private VehicleStatistics vehicleStatistics;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_statistics_id")
    @JsonBackReference(value = "player-statistics_statistics-snapshots")
    private PlayerStatistics playerStatistics;
    @Column(name = "create_timestamp")
    private Long createTimestamp;
    @Column(name = "total_battles")
    private Integer totalBattles;
    @Column(name = "survived_battles")
    private Integer survivedBattles;
    @Column(name = "kill_death_ratio")
    private Float killDeathRatio;
    @Column(name = "hit_miss_ratio")
    private Float hitMissRatio;
    @Column(name = "win_loss_ratio")
    private Float winLossRatio;
    @Column(name = "average_wn8")
    private Float averageWn8;
    @Column(name = "average_experience")
    private Float averageExperience;
    @Column(name = "average_damage")
    private Float averageDamage;
    @Column(name = "average_kills")
    private Float averageKills;
    @Column(name = "average_damage_received")
    private Float averageDamageReceived;
    @Column(name = "average_shots")
    private Float averageShots;
    @Column(name = "average_stun_assisted_damage")
    private Float averageStunAssistedDamage;
    @Column(name = "average_capture_points")
    private Float averageCapturePoints;
    @Column(name = "dropped_capture_points")
    private Float averageDroppedCapturePoints;
    @Column(name = "average_spotting")
    private Float averageSpotting;

    public Integer getStatisticsSnapshotId() {
        return statisticsSnapshotId;
    }

    public void setStatisticsSnapshotId(Integer statisticsSnapshotId) {
        this.statisticsSnapshotId = statisticsSnapshotId;
    }

    public VehicleStatistics getVehicleStatistics() {
        return vehicleStatistics;
    }

    public void setVehicleStatistics(VehicleStatistics vehicleStatistics) {
        this.vehicleStatistics = vehicleStatistics;
    }

    public PlayerStatistics getPlayerStatistics() {
        return playerStatistics;
    }

    public void setPlayerStatistics(PlayerStatistics playerStatistics) {
        this.playerStatistics = playerStatistics;
    }

    public Long getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(Long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public Integer getTotalBattles() {
        return totalBattles;
    }

    public void setTotalBattles(Integer totalBattles) {
        this.totalBattles = totalBattles;
    }

    public Integer getSurvivedBattles() {
        return survivedBattles;
    }

    public void setSurvivedBattles(Integer survivedBattles) {
        this.survivedBattles = survivedBattles;
    }

    public Float getKillDeathRatio() {
        return killDeathRatio;
    }

    public void setKillDeathRatio(Float killDeathRatio) {
        this.killDeathRatio = killDeathRatio;
    }

    public Float getHitMissRatio() {
        return hitMissRatio;
    }

    public void setHitMissRatio(Float hitMissRatio) {
        this.hitMissRatio = hitMissRatio;
    }

    public Float getWinLossRatio() {
        return winLossRatio;
    }

    public void setWinLossRatio(Float winLossRatio) {
        this.winLossRatio = winLossRatio;
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

    public Float getAverageDamage() {
        return averageDamage;
    }

    public void setAverageDamage(Float averageDamage) {
        this.averageDamage = averageDamage;
    }

    public Float getAverageKills() {
        return averageKills;
    }

    public void setAverageKills(Float averageKills) {
        this.averageKills = averageKills;
    }

    public Float getAverageDamageReceived() {
        return averageDamageReceived;
    }

    public void setAverageDamageReceived(Float averageDamageReceived) {
        this.averageDamageReceived = averageDamageReceived;
    }

    public Float getAverageShots() {
        return averageShots;
    }

    public void setAverageShots(Float averageShots) {
        this.averageShots = averageShots;
    }

    public Float getAverageStunAssistedDamage() {
        return averageStunAssistedDamage;
    }

    public void setAverageStunAssistedDamage(Float averageStunAssistedDamage) {
        this.averageStunAssistedDamage = averageStunAssistedDamage;
    }

    public Float getAverageCapturePoints() {
        return averageCapturePoints;
    }

    public void setAverageCapturePoints(Float averageCapturePoints) {
        this.averageCapturePoints = averageCapturePoints;
    }

    public Float getAverageDroppedCapturePoints() {
        return averageDroppedCapturePoints;
    }

    public void setAverageDroppedCapturePoints(Float averageDroppedCapturePoints) {
        this.averageDroppedCapturePoints = averageDroppedCapturePoints;
    }

    public Float getAverageSpotting() {
        return averageSpotting;
    }

    public void setAverageSpotting(Float averageSpotting) {
        this.averageSpotting = averageSpotting;
    }
}
