package com.wotos.wotosstatisticsservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(schema = "expected_statistics")
public class ExpectedStatistics {

    @Id
    @Column(name = "tank_id", nullable = false, unique = true)
    @JsonProperty("tank_id")
    private int IDNum;
    @Column(name = "expected_defense", nullable = false)
    @JsonProperty("expected_defense")
    private float expDef;
    @Column(name = "expected_frag", nullable = false)
    @JsonProperty("expected_frags")
    private float expFrag;
    @Column(name = "expected_spot", nullable = false)
    @JsonProperty("expected_spotted")
    private float expSpot;
    @Column(name = "expected_damage", nullable = false)
    @JsonProperty("expected_damage")
    private float expDamage;
    @Column(name = "expected_win_rate", nullable = false)
    @JsonProperty("expected_win_rate")
    private float expWinRate;

    public int getIDNum() {
        return IDNum;
    }

    public void setIDNum(int IDNum) {
        this.IDNum = IDNum;
    }

    public float getExpDef() {
        return expDef;
    }

    public void setExpDef(float expDef) {
        this.expDef = expDef;
    }

    public float getExpFrag() {
        return expFrag;
    }

    public void setExpFrag(float expFrag) {
        this.expFrag = expFrag;
    }

    public float getExpSpot() {
        return expSpot;
    }

    public void setExpSpot(float expSpot) {
        this.expSpot = expSpot;
    }

    public float getExpDamage() {
        return expDamage;
    }

    public void setExpDamage(float expDamage) {
        this.expDamage = expDamage;
    }

    public float getExpWinRate() {
        return expWinRate;
    }

    public void setExpWinRate(float expWinRate) {
        this.expWinRate = expWinRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpectedStatistics that = (ExpectedStatistics) o;
        return IDNum == that.IDNum &&
                Float.compare(that.expDef, expDef) == 0 &&
                Float.compare(that.expFrag, expFrag) == 0 &&
                Float.compare(that.expSpot, expSpot) == 0 &&
                Float.compare(that.expDamage, expDamage) == 0 &&
                Float.compare(that.expWinRate, expWinRate) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(IDNum, expDef, expFrag, expSpot, expDamage, expWinRate);
    }
}
