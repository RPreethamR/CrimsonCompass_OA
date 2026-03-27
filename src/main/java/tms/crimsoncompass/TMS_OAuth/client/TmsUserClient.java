package tms.crimsoncompass.TMS_OAuth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import tms.crimsoncompass.TMS_OAuth.dto.UserSyncRequest;

/**
 * Feign client for the TMS main backend service.
 * The base URL is resolved from the {@code tms.service.url} property.
 */
@FeignClient(name = "tms-service", url = "${tms.service.url}")
public interface TmsUserClient {

    @PostMapping("/api/users/sync")
    void syncUser(@RequestBody UserSyncRequest request);
}
