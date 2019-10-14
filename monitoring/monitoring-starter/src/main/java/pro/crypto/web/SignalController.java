package pro.crypto.web;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pro.crypto.request.SignalFindRequest;
import pro.crypto.service.SignalService;
import pro.crypto.snapshot.SignalSnapshot;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping("/signal")
@AllArgsConstructor
public class SignalController {

    private final SignalService signalService;

    @GetMapping(value = "/{memberId}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Page<SignalSnapshot> findAll(@PathVariable("memberId") Long memberId,
                                        @Valid @NotNull SignalFindRequest request, Pageable pageable) {
        return signalService.findAll(memberId, request, pageable);
    }

}
