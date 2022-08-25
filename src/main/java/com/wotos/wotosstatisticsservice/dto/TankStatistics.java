package com.wotos.wotosstatisticsservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TankStatistics {

    private Integer frags;
    private Statistics clan;
    private Statistics all;
    private Statistics company;
    private Statistics historical;
    private Statistics globalmap;
    private Statistics team;
    @JsonProperty("tank_id")
    private Integer tankId;
    @JsonProperty("in_garage")
    private Boolean isInGarage;
    @JsonProperty("mark_of_mastery")
    private Integer markOfMastery;
    @JsonProperty("max_frags")
    private Integer maxFrags;
    @JsonProperty("max_xp")
    private Integer maxXp;
    @JsonProperty("account_id")
    private Integer accountId;
    @JsonProperty("regular_team")
    private Statistics regularTeam;
    @JsonProperty("stronghold_skirmish")
    private Statistics strongholdSkirmish;
    @JsonProperty("stronghold_defense")
    private Statistics strongholdDefense;

    public Integer getFrags() {
        return frags;
    }

    public void setFrags(Integer frags) {
        this.frags = frags;
    }

    public Statistics getClan() {
        return clan;
    }

    public void setClan(Statistics clan) {
        this.clan = clan;
    }

    public Statistics getAll() {
        return all;
    }

    public void setAll(Statistics all) {
        this.all = all;
    }

    public Statistics getCompany() {
        return company;
    }

    public void setCompany(Statistics company) {
        this.company = company;
    }

    public Statistics getHistorical() {
        return historical;
    }

    public void setHistorical(Statistics historical) {
        this.historical = historical;
    }

    public Statistics getGlobalmap() {
        return globalmap;
    }

    public void setGlobalmap(Statistics globalmap) {
        this.globalmap = globalmap;
    }

    public Statistics getTeam() {
        return team;
    }

    public void setTeam(Statistics team) {
        this.team = team;
    }

    public Integer getTankId() {
        return tankId;
    }

    public void setTankId(Integer tankId) {
        this.tankId = tankId;
    }

    public Boolean getInGarage() {
        return isInGarage;
    }

    public void setInGarage(Boolean inGarage) {
        isInGarage = inGarage;
    }

    public Integer getMarkOfMastery() {
        return markOfMastery;
    }

    public void setMarkOfMastery(Integer markOfMastery) {
        this.markOfMastery = markOfMastery;
    }

    public Integer getMaxFrags() {
        return maxFrags;
    }

    public void setMaxFrags(Integer maxFrags) {
        this.maxFrags = maxFrags;
    }

    public Integer getMaxXp() {
        return maxXp;
    }

    public void setMaxXp(Integer maxXp) {
        this.maxXp = maxXp;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Statistics getRegularTeam() {
        return regularTeam;
    }

    public void setRegularTeam(Statistics regularTeam) {
        this.regularTeam = regularTeam;
    }

    public Statistics getStrongholdSkirmish() {
        return strongholdSkirmish;
    }

    public void setStrongholdSkirmish(Statistics strongholdSkirmish) {
        this.strongholdSkirmish = strongholdSkirmish;
    }

    public Statistics getStrongholdDefense() {
        return strongholdDefense;
    }

    public void setStrongholdDefense(Statistics strongholdDefense) {
        this.strongholdDefense = strongholdDefense;
    }
}
