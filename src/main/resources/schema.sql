create schema if not exists wotos_statistics;
use wotos_statistics;

create table if not exists expected_statistics(

    tank_id int not null primary key,
    expected_defense float not null,
    expected_frag float not null,
    expected_spot float not null,
    expected_damage float not null,
    expected_win_rate float not null,
    unique(tank_id)

);

create table if not exists player_statistics(

    player_id int not null primary key,
    total_battles int not null,
    average_wn8 float not null,
    unique(player_id)

);

create table if not exists tank_statistics(

    stats_id int not null AUTO_INCREMENT,
    player_id int not null,
    tank_id int not null,
    battles int not null,
    wn8 float not null,

    PRIMARY KEY (stats_id),
    FOREIGN KEY (player_id) REFERENCES player_statistics(player_id)

);
