create schema if not exists wotos_statistics_test;
--use wotos_statistics_test;

create table if not exists expected_statistics(

    tank_id int not null primary key,
    expected_defense float not null,
    expected_frag float not null,
    expected_spot float not null,
    expected_damage float not null,
    expected_win_rate float not null,
    unique(tank_id)

);

create table if not exists statistics_snapshots(

    statistics_snapshot_id int AUTO_INCREMENT,
    player_id int not null,
    tank_id int,
    total_battles int,
    survived_battles int,
    kill_death_ratio float,
    hit_miss_ratio float,
    win_loss_ratio float,
    average_wn8 float,
    average_experience float,
    average_damage float,
    average_kills float,
    average_damage_received float,
    average_shots float,
    average_stun_assisted_damage float,
    average_capture_points float,
    average_dropped_capture_points float,
    personal_rating int,
    primary key(statistics_snapshot_id)

)

--create table if not exists player_statistics(
--
--    player_id int not null primary key,
--    total_battles int not null,
--    average_wn8 float not null,
--    hit_ratio Float not null,
--    average_experience Float not null,
--    personal_rating int not null,
--    unique(player_id)
--
--);
--
--create table if not exists tank_statistics(
--
--    stats_id int not null AUTO_INCREMENT,
--    player_id int not null REFERENCES player_statistics(player_id),
--    expected_statistics_id int not null REFERENCES player_statistics(player_id),
--    battles int not null,
--    wn8 float not null,
--    kill_ratio float not null,
--    average_xp float not null,
--    PRIMARY KEY (stats_id)
--
--);
