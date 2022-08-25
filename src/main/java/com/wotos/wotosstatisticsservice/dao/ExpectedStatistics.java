package com.wotos.wotosstatisticsservice.dao;

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
    @JsonProperty("IDNum")
    private Integer tank_id;
    @Column(name = "expected_defense", nullable = false)
    @JsonProperty("expDef")
    private Float expected_defense;
    @Column(name = "expected_frag", nullable = false)
    @JsonProperty("expFrag")
    private Float expected_frag;
    @Column(name = "expected_spot", nullable = false)
    @JsonProperty("expSpot")
    private Float expected_spot;
    @Column(name = "expected_damage", nullable = false)
    @JsonProperty("expDamage")
    private Float expected_damage;
    @Column(name = "expected_win_rate", nullable = false)
    @JsonProperty("expWinRate")
    private Float expected_win_rate;

    public Integer getTank_id() {
        return tank_id;
    }

    public void setTank_id(Integer tank_id) {
        this.tank_id = tank_id;
    }

    public Float getExpected_defense() {
        return expected_defense;
    }

    public void setExpected_defense(Float expected_defense) {
        this.expected_defense = expected_defense;
    }

    public Float getExpected_frag() {
        return expected_frag;
    }

    public void setExpected_frag(Float expected_frag) {
        this.expected_frag = expected_frag;
    }

    public Float getExpected_spot() {
        return expected_spot;
    }

    public void setExpected_spot(Float expected_spot) {
        this.expected_spot = expected_spot;
    }

    public Float getExpected_damage() {
        return expected_damage;
    }

    public void setExpected_damage(Float expected_damage) {
        this.expected_damage = expected_damage;
    }

    public Float getExpected_win_rate() {
        return expected_win_rate;
    }

    public void setExpected_win_rate(Float expected_win_rate) {
        this.expected_win_rate = expected_win_rate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpectedStatistics that = (ExpectedStatistics) o;
        return Objects.equals(tank_id, that.tank_id) &&
                Objects.equals(expected_defense, that.expected_defense) &&
                Objects.equals(expected_frag, that.expected_frag) &&
                Objects.equals(expected_spot, that.expected_spot) &&
                Objects.equals(expected_damage, that.expected_damage) &&
                Objects.equals(expected_win_rate, that.expected_win_rate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tank_id, expected_defense, expected_frag, expected_spot, expected_damage, expected_win_rate);
    }
}
