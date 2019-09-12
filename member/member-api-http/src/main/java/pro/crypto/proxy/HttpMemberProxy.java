package pro.crypto.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import pro.crypto.MemberStatus;
import pro.crypto.request.MemberCreationRequest;
import pro.crypto.request.MemberUpdateRequest;
import pro.crypto.request.PinActivationRequest;
import pro.crypto.request.TokenActivationRequest;
import pro.crypto.response.ActivationResult;
import pro.crypto.snapshot.MemberSnapshot;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Validated
@FeignClient(name = "api-member", url = "${pro.crypto.entry.point.api.url}", path = "/member")
public interface HttpMemberProxy {

    @RequestMapping(method = GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    Page<MemberSnapshot> findAll(@RequestParam("query") String query,
                                 @RequestParam("status") MemberStatus status,
                                 Pageable pageable);

    @RequestMapping(value = "/{id}", method = GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    MemberSnapshot findById(@PathVariable("id") Long id);

    @RequestMapping(method = POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    Long create(@RequestBody MemberCreationRequest request);

    @RequestMapping(value = "/{id}", method = PUT)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    Long update(@PathVariable("id") Long id, @RequestBody MemberUpdateRequest request);

    @RequestMapping(value = "/activate/token", method = PUT)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    ActivationResult activateByToken(@RequestBody TokenActivationRequest request);

    @RequestMapping(value = "/activate/pin", method = PUT)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    ActivationResult activateByPin(@RequestBody PinActivationRequest request);

}
