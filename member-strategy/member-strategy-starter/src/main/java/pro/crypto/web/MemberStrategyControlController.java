package pro.crypto.web;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pro.crypto.request.MemberStrategyStatusChangeRequest;
import pro.crypto.service.MemberStrategyControlService;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping("/member")
@AllArgsConstructor
public class MemberStrategyControlController {

    private final MemberStrategyControlService memberStrategyControlService;

    @PutMapping(value = "/strategy/{id}/status/change", produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void changeStatus(@PathVariable("id") Long id, @RequestBody MemberStrategyStatusChangeRequest request) {
        memberStrategyControlService.changeStatus(id, request);
    }

}
