package com.wotos.wotosstatisticsservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class PlayerPersonalData {

    @JsonProperty("client_language")
    private String clientLanguage;
    @JsonProperty("last_battle_time")
    private Integer lastBattleTime;
    @JsonProperty("account_id")
    private Integer accountId;
    @JsonProperty("created_at")
    private Integer createdAt;
    @JsonProperty("updated_at")
    private Integer updatedAt;
    @JsonProperty("private")
    private Boolean isPrivateAccount;
    @JsonProperty("global_rating")
    private Integer globalRating;
    @JsonProperty("clan_id")
    private Integer clanId;
    @JsonProperty("statistics")
    private Map<String, Object> statistics;
    @JsonProperty("nickname")
    private String nickname;
    @JsonProperty("logout_at")
    private Integer logoutAt;

    public String getClientLanguage() {
        return clientLanguage;
    }

    public void setClientLanguage(String clientLanguage) {
        this.clientLanguage = clientLanguage;
    }

    public Integer getLastBattleTime() {
        return lastBattleTime;
    }

    public void setLastBattleTime(Integer lastBattleTime) {
        this.lastBattleTime = lastBattleTime;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Integer createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Integer updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getPrivateAccount() {
        return isPrivateAccount;
    }

    public void setPrivateAccount(Boolean isPrivateAccount) {
        this.isPrivateAccount = isPrivateAccount;
    }

    public Integer getGlobalRating() {
        return globalRating;
    }

    public void setGlobalRating(Integer globalRating) {
        this.globalRating = globalRating;
    }

    public Integer getClanId() {
        return clanId;
    }

    public void setClanId(Integer clanId) {
        this.clanId = clanId;
    }

    public Map<String, Object> getStatistics() {
        return statistics;
    }

    public void setStatistics(Map<String, Object> statistics) {
        this.statistics = statistics;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getLogoutAt() {
        return logoutAt;
    }

    public void setLogoutAt(Integer logoutAt) {
        this.logoutAt = logoutAt;
    }
}
