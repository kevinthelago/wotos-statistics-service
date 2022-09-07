package com.wotos.wotosstatisticsservice.util.feign;

import com.wotos.wotosstatisticsservice.util.model.xvm.XvmApiResponse;
import com.wotos.wotosstatisticsservice.util.model.xvm.XvmExpectedStatistics;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "XvmExpectedStatisticsFeignClient", url = "${env.urls.xvm_expected_statistics}")
public interface XvmExpectedStatisticsFeignClient {

    @GetMapping
    ResponseEntity<XvmApiResponse<List<XvmExpectedStatistics>>> getExpectedStatistics();

}
