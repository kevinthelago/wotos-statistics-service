package com.wotos.wotosstatisticsservice.dao;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "player_statistics")
public class PlayerStatistics {

    @Id
    @Column(name = "player_statistics_id")
    private Integer playerStatisticsId;
    private Long last_updated;
    @Column(name = "recent_average_wn8")
    private Float recentAverageWn8;

    @JsonProperty("regular_team")
    @JsonManagedReference("vehicle-statistics_statistics-snapshots")
    @OneToMany(mappedBy = "playerStatistics", cascade = CascadeType.ALL)
    private List<StatisticsSnapshot> regularTeam;
    @JsonProperty("stronghold_skirmish")
    @JsonManagedReference("vehicle-statistics_statistics-snapshots")
    @OneToMany(mappedBy = "playerStatistics", cascade = CascadeType.ALL)
    private List<StatisticsSnapshot> strongholdSkirmish;
    @JsonProperty("stronghold_defense")
    @JsonManagedReference("vehicle-statistics_statistics-snapshots")
    @OneToMany(mappedBy = "playerStatistics", cascade = CascadeType.ALL)
    private List<StatisticsSnapshot> strongholdDefense;
    @JsonManagedReference("vehicle-statistics_statistics-snapshots")
    @OneToMany(mappedBy = "playerStatistics", cascade = CascadeType.ALL)
    private List<StatisticsSnapshot> clan;
    @JsonManagedReference("vehicle-statistics_statistics-snapshots")
    @OneToMany(mappedBy = "playerStatistics", cascade = CascadeType.ALL)
    private List<StatisticsSnapshot> all;
    @JsonManagedReference("vehicle-statistics_statistics-snapshots")
    @OneToMany(mappedBy = "playerStatistics", cascade = CascadeType.ALL)
    private List<StatisticsSnapshot> company;
    @JsonManagedReference("vehicle-statistics_statistics-snapshots")
    @OneToMany(mappedBy = "playerStatistics", cascade = CascadeType.ALL)
    private List<StatisticsSnapshot> historical;
    @JsonManagedReference("vehicle-statistics_statistics-snapshots")
    @OneToMany(mappedBy = "playerStatistics", cascade = CascadeType.ALL)
    private List<StatisticsSnapshot> team;
    @JsonManagedReference("vehicle-statistics_statistics-snapshots")
    @OneToMany(mappedBy = "playerStatistics", cascade = CascadeType.ALL)
    private List<StatisticsSnapshot> epic;
    @JsonManagedReference("vehicle-statistics_statistics-snapshots")
    @OneToMany(mappedBy = "playerStatistics", cascade = CascadeType.ALL)
    private List<StatisticsSnapshot> fallout;
    @JsonManagedReference("vehicle-statistics_statistics-snapshots")
    @OneToMany(mappedBy = "playerStatistics", cascade = CascadeType.ALL)
    private List<StatisticsSnapshot> random;
    @JsonProperty("ranked_battles")
    @JsonManagedReference("vehicle-statistics_statistics-snapshots")
    @OneToMany(mappedBy = "playerStatistics", cascade = CascadeType.ALL)
    private List<StatisticsSnapshot> rankedBattles;

    public Integer getPlayerStatisticsId() {
        return playerStatisticsId;
    }

    public void setPlayerStatisticsId(Integer playerStatisticsId) {
        this.playerStatisticsId = playerStatisticsId;
    }

    public Long getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(Long last_updated) {
        this.last_updated = last_updated;
    }

    public Float getRecentAverageWn8() {
        return recentAverageWn8;
    }

    public void setRecentAverageWn8(Float recentAverageWn8) {
        this.recentAverageWn8 = recentAverageWn8;
    }

    public List<StatisticsSnapshot> getRegularTeam() {
        return regularTeam;
    }

    public void setRegularTeam(List<StatisticsSnapshot> regularTeam) {
        this.regularTeam = regularTeam;
    }

    public List<StatisticsSnapshot> getStrongholdSkirmish() {
        return strongholdSkirmish;
    }

    public void setStrongholdSkirmish(List<StatisticsSnapshot> strongholdSkirmish) {
        this.strongholdSkirmish = strongholdSkirmish;
    }

    public List<StatisticsSnapshot> getStrongholdDefense() {
        return strongholdDefense;
    }

    public void setStrongholdDefense(List<StatisticsSnapshot> strongholdDefense) {
        this.strongholdDefense = strongholdDefense;
    }

    public List<StatisticsSnapshot> getClan() {
        return clan;
    }

    public void setClan(List<StatisticsSnapshot> clan) {
        this.clan = clan;
    }

    public List<StatisticsSnapshot> getAll() {
        return all;
    }

    public void setAll(List<StatisticsSnapshot> all) {
        this.all = all;
    }

    public List<StatisticsSnapshot> getCompany() {
        return company;
    }

    public void setCompany(List<StatisticsSnapshot> company) {
        this.company = company;
    }

    public List<StatisticsSnapshot> getHistorical() {
        return historical;
    }

    public void setHistorical(List<StatisticsSnapshot> historical) {
        this.historical = historical;
    }

    public List<StatisticsSnapshot> getTeam() {
        return team;
    }

    public void setTeam(List<StatisticsSnapshot> team) {
        this.team = team;
    }

    public List<StatisticsSnapshot> getEpic() {
        return epic;
    }

    public void setEpic(List<StatisticsSnapshot> epic) {
        this.epic = epic;
    }

    public List<StatisticsSnapshot> getFallout() {
        return fallout;
    }

    public void setFallout(List<StatisticsSnapshot> fallout) {
        this.fallout = fallout;
    }

    public List<StatisticsSnapshot> getRandom() {
        return random;
    }

    public void setRandom(List<StatisticsSnapshot> random) {
        this.random = random;
    }

    public List<StatisticsSnapshot> getRankedBattles() {
        return rankedBattles;
    }

    public void setRankedBattles(List<StatisticsSnapshot> rankedBattles) {
        this.rankedBattles = rankedBattles;
    }
}
