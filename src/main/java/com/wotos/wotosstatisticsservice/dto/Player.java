package com.wotos.wotosstatisticsservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Player {

    @JsonProperty("nickname")
    private String nickname;
    @JsonProperty("accountId")
    private Integer accountId;

    public Player(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

}
