package com.wotos.wotosstatisticsservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Statistics {

    private Integer spotted;
    private Integer battles;
    private Integer wins;
    private Integer draws;
    private Integer losses;
    private Integer frags;
    private Integer xp;
    private Integer shots;
    private Integer piercings;
    private Integer hits;
    @JsonProperty("no_damage_direct_hits_received")
    private Integer noDamageDirectHitsReceived;
    @JsonProperty("direct_hits_received")
    private Integer directHitsReceived;
    @JsonProperty("explosion_hits")
    private Integer explosionHits;
    @JsonProperty("explosion_hits_received")
    private Integer explosionHitsReceived;
    @JsonProperty("piercings_received")
    private Integer piercingsReceived;
    @JsonProperty("battles_on_stunning_vehicles")
    private Integer battlesOnStunningVehicles;
    @JsonProperty("max_xp")
    private Integer maxXp;
    @JsonProperty("survived_battles")
    private Integer survivedBattles;
    @JsonProperty("dropped_capture_points")
    private Integer droppedCapturePoints;
    @JsonProperty("hits_percents")
    private Integer hitRatio;
    @JsonProperty("damage_received")
    private Integer damageReceived;
    @JsonProperty("stun_number")
    private Integer stunNumber;
    @JsonProperty("capture_points")
    private Integer capturePoints;
    @JsonProperty("stun_assisted_damage")
    private Integer stunAssistedDamage;
    @JsonProperty("avg_damage_assisted")
    private Float averageDamageAssisted;
    @JsonProperty("avg_damage_assisted_track")
    private Float averageDamageAssistedTrack;
    @JsonProperty("avg_damage_assisted_radio")
    private Float averageDamageAssistedRadio;
    @JsonProperty("max_damage")
    private Integer maxDamage;
    @JsonProperty("battle_avg_xp")
    private Integer averageXp;
    @JsonProperty("damage_dealt")
    private Integer damageDealt;
    @JsonProperty("max_frags")
    private Integer maxFrags;
    @JsonProperty("tanking_factor")
    private Float tankingFactor;
    @JsonProperty("avg_damage_blocked")
    private Integer averageDamageBlocked;

    public Integer getSpotted() {
        return spotted;
    }

    public void setSpotted(Integer spotted) {
        this.spotted = spotted;
    }

    public Integer getBattles() {
        return battles;
    }

    public void setBattles(Integer battles) {
        this.battles = battles;
    }

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public Integer getDraws() {
        return draws;
    }

    public void setDraws(Integer draws) {
        this.draws = draws;
    }

    public Integer getLosses() {
        return losses;
    }

    public void setLosses(Integer losses) {
        this.losses = losses;
    }

    public Integer getFrags() {
        return frags;
    }

    public void setFrags(Integer frags) {
        this.frags = frags;
    }

    public Integer getXp() {
        return xp;
    }

    public void setXp(Integer xp) {
        this.xp = xp;
    }

    public Integer getShots() {
        return shots;
    }

    public void setShots(Integer shots) {
        this.shots = shots;
    }

    public Integer getPiercings() {
        return piercings;
    }

    public void setPiercings(Integer piercings) {
        this.piercings = piercings;
    }

    public Integer getHits() {
        return hits;
    }

    public void setHits(Integer hits) {
        this.hits = hits;
    }

    public Integer getNoDamageDirectHitsReceived() {
        return noDamageDirectHitsReceived;
    }

    public void setNoDamageDirectHitsReceived(Integer noDamageDirectHitsReceived) {
        this.noDamageDirectHitsReceived = noDamageDirectHitsReceived;
    }

    public Integer getDirectHitsReceived() {
        return directHitsReceived;
    }

    public void setDirectHitsReceived(Integer directHitsReceived) {
        this.directHitsReceived = directHitsReceived;
    }

    public Integer getExplosionHits() {
        return explosionHits;
    }

    public void setExplosionHits(Integer explosionHits) {
        this.explosionHits = explosionHits;
    }

    public Integer getExplosionHitsReceived() {
        return explosionHitsReceived;
    }

    public void setExplosionHitsReceived(Integer explosionHitsReceived) {
        this.explosionHitsReceived = explosionHitsReceived;
    }

    public Integer getPiercingsReceived() {
        return piercingsReceived;
    }

    public void setPiercingsReceived(Integer piercingsReceived) {
        this.piercingsReceived = piercingsReceived;
    }

    public Integer getBattlesOnStunningVehicles() {
        return battlesOnStunningVehicles;
    }

    public void setBattlesOnStunningVehicles(Integer battlesOnStunningVehicles) {
        this.battlesOnStunningVehicles = battlesOnStunningVehicles;
    }

    public Integer getMaxXp() {
        return maxXp;
    }

    public void setMaxXp(Integer maxXp) {
        this.maxXp = maxXp;
    }

    public Integer getSurvivedBattles() {
        return survivedBattles;
    }

    public void setSurvivedBattles(Integer survivedBattles) {
        this.survivedBattles = survivedBattles;
    }

    public Integer getDroppedCapturePoints() {
        return droppedCapturePoints;
    }

    public void setDroppedCapturePoints(Integer droppedCapturePoints) {
        this.droppedCapturePoints = droppedCapturePoints;
    }

    public Integer getHitRatio() {
        return hitRatio;
    }

    public void setHitRatio(Integer hitRatio) {
        this.hitRatio = hitRatio;
    }

    public Integer getDamageReceived() {
        return damageReceived;
    }

    public void setDamageReceived(Integer damageReceived) {
        this.damageReceived = damageReceived;
    }

    public Integer getStunNumber() {
        return stunNumber;
    }

    public void setStunNumber(Integer stunNumber) {
        this.stunNumber = stunNumber;
    }

    public Integer getCapturePoints() {
        return capturePoints;
    }

    public void setCapturePoints(Integer capturePoints) {
        this.capturePoints = capturePoints;
    }

    public Integer getStunAssistedDamage() {
        return stunAssistedDamage;
    }

    public void setStunAssistedDamage(Integer stunAssistedDamage) {
        this.stunAssistedDamage = stunAssistedDamage;
    }

    public Float getAverageDamageAssisted() {
        return averageDamageAssisted;
    }

    public void setAverageDamageAssisted(Float averageDamageAssisted) {
        this.averageDamageAssisted = averageDamageAssisted;
    }

    public Float getAverageDamageAssistedTrack() {
        return averageDamageAssistedTrack;
    }

    public void setAverageDamageAssistedTrack(Float averageDamageAssistedTrack) {
        this.averageDamageAssistedTrack = averageDamageAssistedTrack;
    }

    public Float getAverageDamageAssistedRadio() {
        return averageDamageAssistedRadio;
    }

    public void setAverageDamageAssistedRadio(Float averageDamageAssistedRadio) {
        this.averageDamageAssistedRadio = averageDamageAssistedRadio;
    }

    public Integer getMaxDamage() {
        return maxDamage;
    }

    public void setMaxDamage(Integer maxDamage) {
        this.maxDamage = maxDamage;
    }

    public Integer getAverageXp() {
        return averageXp;
    }

    public void setAverageXp(Integer averageXp) {
        this.averageXp = averageXp;
    }

    public Integer getDamageDealt() {
        return damageDealt;
    }

    public void setDamageDealt(Integer damageDealt) {
        this.damageDealt = damageDealt;
    }

    public Integer getMaxFrags() {
        return maxFrags;
    }

    public void setMaxFrags(Integer maxFrags) {
        this.maxFrags = maxFrags;
    }

    public Float getTankingFactor() {
        return tankingFactor;
    }

    public void setTankingFactor(Float tankingFactor) {
        this.tankingFactor = tankingFactor;
    }

    public Integer getAverageDamageBlocked() {
        return averageDamageBlocked;
    }

    public void setAverageDamageBlocked(Integer averageDamageBlocked) {
        this.averageDamageBlocked = averageDamageBlocked;
    }
}
