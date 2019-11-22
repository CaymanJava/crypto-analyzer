package pro.crypto.service.sms;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@FeignClient(name = "alpha-sms", url = "${notification.sms.alpha.sms.url}")
public interface AlphaSmsClient {

    @RequestMapping(method = GET)
    String sendSms(@RequestParam("version") String version,
                   @RequestParam("key") String apiKey,
                   @RequestParam("command") String command,
                   @RequestParam("from") String from,
                   @RequestParam("to") String to,
                   @RequestParam("message") String message);

}
