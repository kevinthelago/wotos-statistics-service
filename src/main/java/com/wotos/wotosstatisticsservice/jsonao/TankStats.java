package com.wotos.wotosstatisticsservice.jsonao;

import java.util.LinkedHashMap;

public class TankStats {

    private int tank_id;
    private boolean in_garage;
    private int frags;
    private int mark_of_mastery;
    private int max_frags;
    private int max_xp;
    private int account_id;
    private Statistics clan;
    private Statistics stronghold_skirmish;
    private Statistics regular_team;
    private Statistics company;
    private Statistics all;
    private Statistics stronghold_defense;
    private Statistics team;
    private Statistics globalmap;

    public int getTank_id() {
        return tank_id;
    }

    public void setTank_id(int tank_id) {
        this.tank_id = tank_id;
    }

    public boolean isIn_garage() {
        return in_garage;
    }

    public void setIn_garage(boolean in_garage) {
        this.in_garage = in_garage;
    }

    public int getFrags() {
        return frags;
    }

    public void setFrags(int frags) {
        this.frags = frags;
    }

    public int getMark_of_mastery() {
        return mark_of_mastery;
    }

    public void setMark_of_mastery(int mark_of_mastery) {
        this.mark_of_mastery = mark_of_mastery;
    }

    public int getMax_frags() {
        return max_frags;
    }

    public void setMax_frags(int max_frags) {
        this.max_frags = max_frags;
    }

    public int getMax_xp() {
        return max_xp;
    }

    public void setMax_xp(int max_xp) {
        this.max_xp = max_xp;
    }

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }

    public Statistics getClan() {
        return clan;
    }

    public void setClan(Statistics clan) {
        this.clan = clan;
    }

    public Statistics getStronghold_skirmish() {
        return stronghold_skirmish;
    }

    public void setStronghold_skirmish(Statistics stronghold_skirmish) {
        this.stronghold_skirmish = stronghold_skirmish;
    }

    public Statistics getRegular_team() {
        return regular_team;
    }

    public void setRegular_team(Statistics regular_team) {
        this.regular_team = regular_team;
    }

    public Statistics getCompany() {
        return company;
    }

    public void setCompany(Statistics company) {
        this.company = company;
    }

    public Statistics getAll() {
        return all;
    }

    public void setAll(Statistics all) {
        this.all = all;
    }

    public Statistics getStronghold_defense() {
        return stronghold_defense;
    }

    public void setStronghold_defense(Statistics stronghold_defense) {
        this.stronghold_defense = stronghold_defense;
    }

    public Statistics getTeam() {
        return team;
    }

    public void setTeam(Statistics team) {
        this.team = team;
    }

    public Statistics getGlobalmap() {
        return globalmap;
    }

    public void setGlobalmap(Statistics globalmap) {
        this.globalmap = globalmap;
    }
}
