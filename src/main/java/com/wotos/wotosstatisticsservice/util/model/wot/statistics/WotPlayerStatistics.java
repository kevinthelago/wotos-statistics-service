package com.wotos.wotosstatisticsservice.util.model.wot.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WotPlayerStatistics {

    private final WotStatisticsByGameMode clan;
    private final WotStatisticsByGameMode all;
    @JsonProperty("regular_team")
    private final WotStatisticsByGameMode regularTeam;
    @JsonProperty("trees_cut")
    private final Integer treesCut;
    private final WotStatisticsByGameMode company;
    @JsonProperty("stronghold_skirmish")
    private final WotStatisticsByGameMode strongholdSkirmish;
    @JsonProperty("stronghold_defense")
    private final WotStatisticsByGameMode strongholdDefense;
    private final WotStatisticsByGameMode historical;
    private final WotStatisticsByGameMode team;
    private final Integer frags;

    public WotPlayerStatistics(
            WotStatisticsByGameMode clan, WotStatisticsByGameMode all, WotStatisticsByGameMode regularTeam,
            Integer treesCut, WotStatisticsByGameMode company, WotStatisticsByGameMode strongholdSkirmish,
            WotStatisticsByGameMode strongholdDefense, WotStatisticsByGameMode historical,
            WotStatisticsByGameMode team, Integer frags
    ) {
        this.clan = clan;
        this.all = all;
        this.regularTeam = regularTeam;
        this.treesCut = treesCut;
        this.company = company;
        this.strongholdSkirmish = strongholdSkirmish;
        this.strongholdDefense = strongholdDefense;
        this.historical = historical;
        this.team = team;
        this.frags = frags;
    }

    public WotStatisticsByGameMode getClan() {
        return clan;
    }

    public WotStatisticsByGameMode getAll() {
        return all;
    }

    public WotStatisticsByGameMode getRegularTeam() {
        return regularTeam;
    }

    public Integer getTreesCut() {
        return treesCut;
    }

    public WotStatisticsByGameMode getCompany() {
        return company;
    }

    public WotStatisticsByGameMode getStrongholdSkirmish() {
        return strongholdSkirmish;
    }

    public WotStatisticsByGameMode getStrongholdDefense() {
        return strongholdDefense;
    }

    public WotStatisticsByGameMode getHistorical() {
        return historical;
    }

    public WotStatisticsByGameMode getTeam() {
        return team;
    }

    public Integer getFrags() {
        return frags;
    }
}
