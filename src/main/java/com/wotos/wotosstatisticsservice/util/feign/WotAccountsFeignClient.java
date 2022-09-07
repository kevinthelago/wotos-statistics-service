package com.wotos.wotosstatisticsservice.util.feign;

import com.wotos.wotosstatisticsservice.config.FeignConfig;
import com.wotos.wotosstatisticsservice.util.model.wot.WotApiResponse;
import com.wotos.wotosstatisticsservice.util.model.wot.player.WotPlayerDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "WotAccountsFeignClient", url = "${env.urls.world_of_tanks_api}", configuration = FeignConfig.class)
@RequestMapping("/account")
public interface WotAccountsFeignClient {

    @GetMapping(value = "/info/", consumes = "application/json")
    ResponseEntity<WotApiResponse<Map<Integer, WotPlayerDetails>>> getPlayerDetails(
            @RequestParam("application_id") String appId,
            @RequestParam("access_token") String accessToken,
            @RequestParam("extra") String extra,
            @RequestParam("fields") String fields,
            @RequestParam("language") String language,
            @RequestParam("account_id") Integer accountId
    );

}
