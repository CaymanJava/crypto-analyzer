package pro.crypto.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import pro.crypto.request.MemberStrategyStatusChangeRequest;

import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Validated
@FeignClient(name = "api-member-control", url = "${pro.crypto.entry.point.api.url}", path = "/member")
public interface MemberStrategyControlProxy {

    @RequestMapping(value = "/strategy/{id}/status/change", method = PUT)
    @ResponseStatus(HttpStatus.OK)
    void changeStatus(@PathVariable("id") Long id, @RequestBody MemberStrategyStatusChangeRequest request);

}
