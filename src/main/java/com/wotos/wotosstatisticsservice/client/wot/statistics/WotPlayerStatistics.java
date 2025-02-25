package com.wotos.wotosstatisticsservice.client.wot.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WotPlayerStatistics {

    private final WotStatistics clan;
    private final WotStatistics all;
    @JsonProperty("regular_team")
    private final WotStatistics regularTeam;
    @JsonProperty("trees_cut")
    private final Integer treesCut;
    private final WotStatistics company;
    @JsonProperty("stronghold_skirmish")
    private final WotStatistics strongholdSkirmish;
    @JsonProperty("stronghold_defense")
    private final WotStatistics strongholdDefense;
    private final WotStatistics historical;
    private final WotStatistics team;
    private final Integer frags;
    private final WotStatistics random;
    private final WotStatistics epic;
    private final WotStatistics fallout;
    @JsonProperty("ranked_battles")
    private final WotStatistics rankedBattles;

    public WotPlayerStatistics(
            WotStatistics clan, WotStatistics all, WotStatistics regularTeam,
            Integer treesCut, WotStatistics company, WotStatistics strongholdSkirmish,
            WotStatistics strongholdDefense, WotStatistics historical, WotStatistics team,
            Integer frags, WotStatistics random, WotStatistics epic, WotStatistics fallout,
            WotStatistics rankedBattles
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
        this.random = random;
        this.epic = epic;
        this.fallout = fallout;
        this.rankedBattles = rankedBattles;
    }

    public WotStatistics getClan() {
        return clan;
    }

    public WotStatistics getAll() {
        return all;
    }

    public WotStatistics getRegularTeam() {
        return regularTeam;
    }

    public Integer getTreesCut() {
        return treesCut;
    }

    public WotStatistics getCompany() {
        return company;
    }

    public WotStatistics getStrongholdSkirmish() {
        return strongholdSkirmish;
    }

    public WotStatistics getStrongholdDefense() {
        return strongholdDefense;
    }

    public WotStatistics getHistorical() {
        return historical;
    }

    public WotStatistics getTeam() {
        return team;
    }

    public Integer getFrags() {
        return frags;
    }

    public WotStatistics getRandom() {
        return random;
    }

    public WotStatistics getEpic() {
        return epic;
    }

    public WotStatistics getFallout() {
        return fallout;
    }

    public WotStatistics getRankedBattles() {
        return rankedBattles;
    }
}
