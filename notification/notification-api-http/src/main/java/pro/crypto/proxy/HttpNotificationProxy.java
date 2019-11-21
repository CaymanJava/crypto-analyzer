package pro.crypto.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import pro.crypto.snapshot.NotificationSnapshot;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Validated
@FeignClient(name = "api-notifications", url = "${pro.crypto.entry.point.api.url}", path = "/notifications")
public interface HttpNotificationProxy {

    @RequestMapping(method = GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    Page<NotificationSnapshot> findAll(@RequestParam("query") String query,
                                       Pageable pageable);

}
