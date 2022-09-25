package com.wotos.wotosstatisticsservice.constants;

import com.wotos.wotosstatisticsservice.util.model.wot.statistics.WotStatistics;

public enum GameModes {
    CLAN("clan"),
    ALL("all"),
    REGULAR_TEAM("regular_team"),
    COMPANY("company"),
    STRONGHOLD_SKIRMISH("stronghold_skirmish"),
    HISTORICAL("historical"),
    TEAM("team"),
    RANDOM("random"),
    EPIC("epic"),
    FALLOUT("fallout"),
    RANKED_BATTLES("ranked_battles");

    private final String name;

    GameModes(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
