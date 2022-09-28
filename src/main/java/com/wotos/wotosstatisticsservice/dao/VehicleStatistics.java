package com.wotos.wotosstatisticsservice.dao;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
public class VehicleStatistics {

    @Id
    @GeneratedValue
    @Column(name = "vehicle_statistics_id")
    private Integer vehicleStatisticsId;
    @Column(name = "vehicle_id", nullable = false)
    private Integer vehicleId;

    @JsonProperty("regular_team")
    @JsonManagedReference("vehicle-statistics_statistics-snapshots")
    @OneToMany(mappedBy = "vehicleStatistics", cascade = CascadeType.ALL)
    private Set<StatisticsSnapshot> regularTeam;
    @JsonProperty("stronghold_skirmish")
    @JsonManagedReference("vehicle-statistics_statistics-snapshots")
    @OneToMany(mappedBy = "vehicleStatistics", cascade = CascadeType.ALL)
    private Set<StatisticsSnapshot> strongholdSkirmish;
    @JsonProperty("stronghold_defense")
    @JsonManagedReference("vehicle-statistics_statistics-snapshots")
    @OneToMany(mappedBy = "vehicleStatistics", cascade = CascadeType.ALL)
    private Set<StatisticsSnapshot> strongholdDefense;
    @JsonManagedReference("vehicle-statistics_statistics-snapshots")
    @OneToMany(mappedBy = "vehicleStatistics", cascade = CascadeType.ALL)
    private Set<StatisticsSnapshot> clan;
    @JsonManagedReference("vehicle-statistics_statistics-snapshots")
    @OneToMany(mappedBy = "vehicleStatistics", cascade = CascadeType.ALL)
    private Set<StatisticsSnapshot> all;
    @JsonManagedReference("vehicle-statistics_statistics-snapshots")
    @OneToMany(mappedBy = "vehicleStatistics", cascade = CascadeType.ALL)
    private Set<StatisticsSnapshot> company;
    @JsonManagedReference("vehicle-statistics_statistics-snapshots")
    @OneToMany(mappedBy = "vehicleStatistics", cascade = CascadeType.ALL)
    private Set<StatisticsSnapshot> historical;
    @JsonProperty("globalmap")
    @JsonManagedReference("vehicle-statistics_statistics-snapshots")
    @OneToMany(mappedBy = "vehicleStatistics", cascade = CascadeType.ALL)
    private Set<StatisticsSnapshot> globalMap;
    @JsonManagedReference("vehicle-statistics_statistics-snapshots")
    @OneToMany(mappedBy = "vehicleStatistics", cascade = CascadeType.ALL)
    private Set<StatisticsSnapshot> team;
    @JsonManagedReference("vehicle-statistics_statistics-snapshots")
    @OneToMany(mappedBy = "vehicleStatistics", cascade = CascadeType.ALL)
    private Set<StatisticsSnapshot> epic;
    @JsonManagedReference("vehicle-statistics_statistics-snapshots")
    @OneToMany(mappedBy = "vehicleStatistics", cascade = CascadeType.ALL)
    private Set<StatisticsSnapshot> fallout;
    @JsonManagedReference("vehicle-statistics_statistics-snapshots")
    @OneToMany(mappedBy = "vehicleStatistics", cascade = CascadeType.ALL)
    private Set<StatisticsSnapshot> random;
    @JsonProperty("ranked_battles")
    @JsonManagedReference("vehicle-statistics_statistics-snapshots")
    @OneToMany(mappedBy = "vehicleStatistics", cascade = CascadeType.ALL)
    private Set<StatisticsSnapshot> rankedBattles;

    public Set<StatisticsSnapshot> getRegularTeam() {
        return regularTeam;
    }

    public void setRegularTeam(Set<StatisticsSnapshot> regularTeam) {
        this.regularTeam = regularTeam;
    }

    public Set<StatisticsSnapshot> getStrongholdSkirmish() {
        return strongholdSkirmish;
    }

    public void setStrongholdSkirmish(Set<StatisticsSnapshot> strongholdSkirmish) {
        this.strongholdSkirmish = strongholdSkirmish;
    }

    public Set<StatisticsSnapshot> getStrongholdDefense() {
        return strongholdDefense;
    }

    public void setStrongholdDefense(Set<StatisticsSnapshot> strongholdDefense) {
        this.strongholdDefense = strongholdDefense;
    }

    public Set<StatisticsSnapshot> getClan() {
        return clan;
    }

    public void setClan(Set<StatisticsSnapshot> clan) {
        this.clan = clan;
    }

    public Set<StatisticsSnapshot> getAll() {
        return all;
    }

    public void setAll(Set<StatisticsSnapshot> all) {
        this.all = all;
    }

    public Set<StatisticsSnapshot> getCompany() {
        return company;
    }

    public void setCompany(Set<StatisticsSnapshot> company) {
        this.company = company;
    }

    public Set<StatisticsSnapshot> getHistorical() {
        return historical;
    }

    public void setHistorical(Set<StatisticsSnapshot> historical) {
        this.historical = historical;
    }

    public Set<StatisticsSnapshot> getGlobalMap() {
        return globalMap;
    }

    public void setGlobalMap(Set<StatisticsSnapshot> globalMap) {
        this.globalMap = globalMap;
    }

    public Set<StatisticsSnapshot> getTeam() {
        return team;
    }

    public void setTeam(Set<StatisticsSnapshot> team) {
        this.team = team;
    }

    public Set<StatisticsSnapshot> getEpic() {
        return epic;
    }

    public void setEpic(Set<StatisticsSnapshot> epic) {
        this.epic = epic;
    }

    public Set<StatisticsSnapshot> getFallout() {
        return fallout;
    }

    public void setFallout(Set<StatisticsSnapshot> fallout) {
        this.fallout = fallout;
    }

    public Set<StatisticsSnapshot> getRandom() {
        return random;
    }

    public void setRandom(Set<StatisticsSnapshot> random) {
        this.random = random;
    }

    public Set<StatisticsSnapshot> getRankedBattles() {
        return rankedBattles;
    }

    public void setRankedBattles(Set<StatisticsSnapshot> rankedBattles) {
        this.rankedBattles = rankedBattles;
    }
}
