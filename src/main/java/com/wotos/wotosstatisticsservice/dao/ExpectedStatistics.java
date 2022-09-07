package com.wotos.wotosstatisticsservice.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "expected_statistics")
public class ExpectedStatistics {
    @Id
    @Column(name = "vehicle_id", nullable = false, unique = true)
    private Integer vehicleId;
    @Column(name = "expected_defense", nullable = false)
    private Float expectedDefense;
    @Column(name = "expected_frag", nullable = false)
    private Float expectedFrag;
    @Column(name = "expected_spot", nullable = false)
    private Float expectedSpot;
    @Column(name = "expected_damage", nullable = false)
    private Float expectedDamage;
    @Column(name = "expected_win_rate", nullable = false)
    private Float expectedWinRate;

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Float getExpectedDefense() {
        return expectedDefense;
    }

    public void setExpectedDefense(Float expectedDefense) {
        this.expectedDefense = expectedDefense;
    }

    public Float getExpectedFrag() {
        return expectedFrag;
    }

    public void setExpectedFrag(Float expectedFrag) {
        this.expectedFrag = expectedFrag;
    }

    public Float getExpectedSpot() {
        return expectedSpot;
    }

    public void setExpectedSpot(Float expectedSpot) {
        this.expectedSpot = expectedSpot;
    }

    public Float getExpectedDamage() {
        return expectedDamage;
    }

    public void setExpectedDamage(Float expectedDamage) {
        this.expectedDamage = expectedDamage;
    }

    public Float getExpectedWinRate() {
        return expectedWinRate;
    }

    public void setExpectedWinRate(Float expectedWinRate) {
        this.expectedWinRate = expectedWinRate;
    }
}
