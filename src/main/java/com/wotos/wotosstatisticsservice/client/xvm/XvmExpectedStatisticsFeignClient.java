package com.wotos.wotosstatisticsservice.client.xvm;

import com.wotos.wotosstatisticsservice.client.xvm.xvm.XvmApiResponse;
import com.wotos.wotosstatisticsservice.client.xvm.xvm.XvmExpectedStatistics;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "XvmExpectedStatisticsFeignClient", url = "${env.urls.xvm_expected_statistics}")
public interface XvmExpectedStatisticsFeignClient {

    @GetMapping
    ResponseEntity<XvmApiResponse<List<XvmExpectedStatistics>>> getExpectedStatistics();

}
