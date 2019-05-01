package pro.crypto.web.indicator;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pro.crypto.model.IndicatorResult;
import pro.crypto.request.IndicatorCalculationRequest;
import pro.crypto.service.IndicatorService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping("/front-office/indicator")
@AllArgsConstructor
public class FrontIndicatorController {

    private final IndicatorService indicatorService;

    @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public IndicatorResult[] calculate(@Valid @NotNull IndicatorCalculationRequest request) {
        return indicatorService.calculate(request);
    }

}
