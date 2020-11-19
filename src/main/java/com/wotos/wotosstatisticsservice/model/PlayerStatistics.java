package com.wotos.wotosstatisticsservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(schema = "player_statistics")
public class PlayerStatistics {

    @Id
    @Column(name = "player_id", nullable = false, unique = true)
    private int player_id;

}
