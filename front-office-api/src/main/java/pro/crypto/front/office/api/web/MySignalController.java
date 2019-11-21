package pro.crypto.front.office.api.web;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pro.crypto.front.office.api.Identity;
import pro.crypto.front.office.api.response.SignalResponse;
import pro.crypto.front.office.service.signal.SignalProcessingService;
import pro.crypto.request.SignalFindRequest;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping("/me/signals")
@AllArgsConstructor
public class MySignalController {

    private final SignalProcessingService signalProcessingService;

    @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Page<SignalResponse> findAll(@AuthenticationPrincipal Identity identity,
                                        @Valid @NotNull SignalFindRequest request,
                                        Pageable pageable) {
        return signalProcessingService.find(identity.getMemberId(), request, pageable);
    }

}
