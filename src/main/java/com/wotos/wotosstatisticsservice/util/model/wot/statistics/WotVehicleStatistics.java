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
    private final WotStatisticsByGameMode regularTeam;
    @JsonProperty("stronghold_skirmish")
    private final WotStatisticsByGameMode strongholdSkirmish;
    @JsonProperty("stronghold_defense")
    private final WotStatisticsByGameMode strongholdDefense;
    private final WotStatisticsByGameMode clan;
    private final WotStatisticsByGameMode all;
    private final WotStatisticsByGameMode company;
    private final WotStatisticsByGameMode historical;
    @JsonProperty("globalmap")
    private final WotStatisticsByGameMode globalMap;
    private final WotStatisticsByGameMode team;

    public WotVehicleStatistics(
            Integer frags, Integer vehicleId, Boolean isInGarage, Integer markOfMastery,
            Integer maxFrags, Integer maxXp, Integer accountId, WotStatisticsByGameMode regularTeam,
            WotStatisticsByGameMode strongholdSkirmish, WotStatisticsByGameMode strongholdDefense,
            WotStatisticsByGameMode clan, WotStatisticsByGameMode all, WotStatisticsByGameMode company,
            WotStatisticsByGameMode historical, WotStatisticsByGameMode globalMap, WotStatisticsByGameMode team
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

    public WotStatisticsByGameMode getRegularTeam() {
        return regularTeam;
    }

    public WotStatisticsByGameMode getStrongholdSkirmish() {
        return strongholdSkirmish;
    }

    public WotStatisticsByGameMode getStrongholdDefense() {
        return strongholdDefense;
    }

    public WotStatisticsByGameMode getClan() {
        return clan;
    }

    public WotStatisticsByGameMode getAll() {
        return all;
    }

    public WotStatisticsByGameMode getCompany() {
        return company;
    }

    public WotStatisticsByGameMode getHistorical() {
        return historical;
    }

    public WotStatisticsByGameMode getGlobalmap() {
        return globalMap;
    }

    public WotStatisticsByGameMode getTeam() {
        return team;
    }
}
