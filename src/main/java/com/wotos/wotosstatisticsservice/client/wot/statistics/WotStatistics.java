package com.wotos.wotosstatisticsservice.client.wot.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WotStatistics {

    private final Integer spotted;
    private final Integer battles;
    private final Integer wins;
    private final Integer draws;
    private final Integer losses;
    private final Integer frags;
    private final Integer xp;
    private final Integer shots;
    private final Integer piercings;
    private final Integer hits;
    @JsonProperty("no_damage_direct_hits_received")
    private final Integer noDamageDirectHitsReceived;
    @JsonProperty("direct_hits_received")
    private final Integer directHitsReceived;
    @JsonProperty("explosion_hits")
    private final Integer explosionHits;
    @JsonProperty("explosion_hits_received")
    private final Integer explosionHitsReceived;
    @JsonProperty("piercings_received")
    private final Integer piercingsReceived;
    @JsonProperty("battles_on_stunning_vehicles")
    private final Integer battlesOnStunningVehicles;
    @JsonProperty("max_xp")
    private final Integer maxXp;
    @JsonProperty("survived_battles")
    private final Integer survivedBattles;
    @JsonProperty("dropped_capture_points")
    private final Integer droppedCapturePoints;
    @JsonProperty("hits_percents")
    private final Integer hitRatio;
    @JsonProperty("damage_received")
    private final Integer damageReceived;
    @JsonProperty("stun_number")
    private final Integer stunNumber;
    @JsonProperty("capture_points")
    private final Integer capturePoints;
    @JsonProperty("stun_assisted_damage")
    private final Integer stunAssistedDamage;
    @JsonProperty("avg_damage_assisted")
    private final Float averageDamageAssisted;
    @JsonProperty("avg_damage_assisted_track")
    private final Float averageDamageAssistedTrack;
    @JsonProperty("avg_damage_assisted_radio")
    private final Float averageDamageAssistedRadio;
    @JsonProperty("max_damage")
    private final Integer maxDamage;
    @JsonProperty("battle_avg_xp")
    private final Integer averageXp;
    @JsonProperty("damage_dealt")
    private final Integer damageDealt;
    @JsonProperty("max_frags")
    private final Integer maxFrags;
    @JsonProperty("tanking_factor")
    private final Float tankingFactor;
    @JsonProperty("avg_damage_blocked")
    private final Float averageDamageBlocked;

    public WotStatistics(
            Integer spotted, Integer battles, Integer wins, Integer draws,
            Integer losses, Integer frags, Integer xp, Integer shots, Integer piercings,
            Integer hits, Integer noDamageDirectHitsReceived, Integer directHitsReceived,
            Integer explosionHits, Integer explosionHitsReceived, Integer piercingsReceived,
            Integer battlesOnStunningVehicles, Integer maxXp, Integer survivedBattles,
            Integer droppedCapturePoints, Integer hitRatio, Integer damageReceived,
            Integer stunNumber, Integer capturePoints, Integer stunAssistedDamage,
            Float averageDamageAssisted, Float averageDamageAssistedTrack,
            Float averageDamageAssistedRadio, Integer maxDamage, Integer averageXp,
            Integer damageDealt, Integer maxFrags, Float tankingFactor,
            Float averageDamageBlocked
    ) {
        this.spotted = spotted;
        this.battles = battles;
        this.wins = wins;
        this.draws = draws;
        this.losses = losses;
        this.frags = frags;
        this.xp = xp;
        this.shots = shots;
        this.piercings = piercings;
        this.hits = hits;
        this.noDamageDirectHitsReceived = noDamageDirectHitsReceived;
        this.directHitsReceived = directHitsReceived;
        this.explosionHits = explosionHits;
        this.explosionHitsReceived = explosionHitsReceived;
        this.piercingsReceived = piercingsReceived;
        this.battlesOnStunningVehicles = battlesOnStunningVehicles;
        this.maxXp = maxXp;
        this.survivedBattles = survivedBattles;
        this.droppedCapturePoints = droppedCapturePoints;
        this.hitRatio = hitRatio;
        this.damageReceived = damageReceived;
        this.stunNumber = stunNumber;
        this.capturePoints = capturePoints;
        this.stunAssistedDamage = stunAssistedDamage;
        this.averageDamageAssisted = averageDamageAssisted;
        this.averageDamageAssistedTrack = averageDamageAssistedTrack;
        this.averageDamageAssistedRadio = averageDamageAssistedRadio;
        this.maxDamage = maxDamage;
        this.averageXp = averageXp;
        this.damageDealt = damageDealt;
        this.maxFrags = maxFrags;
        this.tankingFactor = tankingFactor;
        this.averageDamageBlocked = averageDamageBlocked;
    }

    public Integer getSpotted() {
        return spotted;
    }

    public Integer getBattles() {
        return battles;
    }

    public Integer getWins() {
        return wins;
    }

    public Integer getDraws() {
        return draws;
    }

    public Integer getLosses() {
        return losses;
    }

    public Integer getFrags() {
        return frags;
    }

    public Integer getXp() {
        return xp;
    }

    public Integer getShots() {
        return shots;
    }

    public Integer getPiercings() {
        return piercings;
    }

    public Integer getHits() {
        return hits;
    }

    public Integer getNoDamageDirectHitsReceived() {
        return noDamageDirectHitsReceived;
    }

    public Integer getDirectHitsReceived() {
        return directHitsReceived;
    }

    public Integer getExplosionHits() {
        return explosionHits;
    }

    public Integer getExplosionHitsReceived() {
        return explosionHitsReceived;
    }

    public Integer getPiercingsReceived() {
        return piercingsReceived;
    }

    public Integer getBattlesOnStunningVehicles() {
        return battlesOnStunningVehicles;
    }

    public Integer getMaxXp() {
        return maxXp;
    }

    public Integer getSurvivedBattles() {
        return survivedBattles;
    }

    public Integer getDroppedCapturePoints() {
        return droppedCapturePoints;
    }

    public Integer getHitRatio() {
        return hitRatio;
    }

    public Integer getDamageReceived() {
        return damageReceived;
    }

    public Integer getStunNumber() {
        return stunNumber;
    }

    public Integer getCapturePoints() {
        return capturePoints;
    }

    public Integer getStunAssistedDamage() {
        return stunAssistedDamage;
    }

    public Float getAverageDamageAssisted() {
        return averageDamageAssisted;
    }

    public Float getAverageDamageAssistedTrack() {
        return averageDamageAssistedTrack;
    }

    public Float getAverageDamageAssistedRadio() {
        return averageDamageAssistedRadio;
    }

    public Integer getMaxDamage() {
        return maxDamage;
    }

    public Integer getAverageXp() {
        return averageXp;
    }

    public Integer getDamageDealt() {
        return damageDealt;
    }

    public Integer getMaxFrags() {
        return maxFrags;
    }

    public Float getTankingFactor() {
        return tankingFactor;
    }

    public Float getAverageDamageBlocked() {
        return averageDamageBlocked;
    }
}
