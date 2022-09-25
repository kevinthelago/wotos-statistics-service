package com.wotos.wotosstatisticsservice.util.model.wot.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WotVehicleStatistics {

    private final Integer frags;
    @JsonProperty("tank_id")
    private final Integer vehicleId;
    @JsonProperty("in_garage")
    private final Boolean isInGarage;
    @JsonProperty("mark_of_mastery")
    private final Integer markOfMastery;
    @JsonProperty("max_frags")
    private final Integer maxFrags;
    @JsonProperty("max_xp")
    private final Integer maxXp;
    @JsonProperty("account_id")
    private final Integer accountId;
    @JsonProperty("regular_team")
    private final WotStatistics regularTeam;
    @JsonProperty("stronghold_skirmish")
    private final WotStatistics strongholdSkirmish;
    @JsonProperty("stronghold_defense")
    private final WotStatistics strongholdDefense;
    private final WotStatistics clan;
    private final WotStatistics all;
    private final WotStatistics company;
    private final WotStatistics historical;
    @JsonProperty("globalmap")
    private final WotStatistics globalMap;
    private final WotStatistics team;
    private final WotStatistics epic;
    private final WotStatistics fallout;
    private final WotStatistics random;
    @JsonProperty("ranked_battles")
    private final WotStatistics rankedBattles;

    public WotVehicleStatistics(
            Integer frags, Integer vehicleId, Boolean isInGarage, Integer markOfMastery,
            Integer maxFrags, Integer maxXp, Integer accountId, WotStatistics regularTeam,
            WotStatistics strongholdSkirmish, WotStatistics strongholdDefense, WotStatistics clan,
            WotStatistics all, WotStatistics company, WotStatistics historical, WotStatistics globalMap,
            WotStatistics team, WotStatistics epic, WotStatistics fallout, WotStatistics random,
            WotStatistics rankedBattles
    ) {
        this.frags = frags;
        this.vehicleId = vehicleId;
        this.isInGarage = isInGarage;
        this.markOfMastery = markOfMastery;
        this.maxFrags = maxFrags;
        this.maxXp = maxXp;
        this.accountId = accountId;
        this.regularTeam = regularTeam;
        this.strongholdSkirmish = strongholdSkirmish;
        this.strongholdDefense = strongholdDefense;
        this.clan = clan;
        this.all = all;
        this.company = company;
        this.historical = historical;
        this.globalMap = globalMap;
        this.team = team;
        this.epic = epic;
        this.fallout = fallout;
        this.random = random;
        this.rankedBattles = rankedBattles;
    }

    public Integer getFrags() {
        return frags;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public Boolean getInGarage() {
        return isInGarage;
    }

    public Integer getMarkOfMastery() {
        return markOfMastery;
    }

    public Integer getMaxFrags() {
        return maxFrags;
    }

    public Integer getMaxXp() {
        return maxXp;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public WotStatistics getRegularTeam() {
        return regularTeam;
    }

    public WotStatistics getStrongholdSkirmish() {
        return strongholdSkirmish;
    }

    public WotStatistics getStrongholdDefense() {
        return strongholdDefense;
    }

    public WotStatistics getClan() {
        return clan;
    }

    public WotStatistics getAll() {
        return all;
    }

    public WotStatistics getCompany() {
        return company;
    }

    public WotStatistics getHistorical() {
        return historical;
    }

    public WotStatistics getGlobalMap() {
        return globalMap;
    }

    public WotStatistics getTeam() {
        return team;
    }

    public WotStatistics getEpic() {
        return epic;
    }

    public WotStatistics getFallout() {
        return fallout;
    }

    public WotStatistics getRandom() {
        return random;
    }

    public WotStatistics getRankedBattles() {
        return rankedBattles;
    }
}
