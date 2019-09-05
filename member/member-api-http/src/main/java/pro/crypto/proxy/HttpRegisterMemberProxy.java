package pro.crypto.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import pro.crypto.request.MemberRegisterRequest;
import pro.crypto.snapshot.MemberSnapshot;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Validated
@FeignClient(name = "api-member", url = "${pro.crypto.entry.point.api.url}", path = "/member/register")
public interface HttpRegisterMemberProxy {

    @RequestMapping(method = POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    MemberSnapshot register(@RequestBody MemberRegisterRequest request);

}
