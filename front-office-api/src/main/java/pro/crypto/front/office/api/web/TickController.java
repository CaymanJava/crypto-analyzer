package pro.crypto.front.office.api.web;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pro.crypto.front.office.api.response.TickDataResponse;
import pro.crypto.request.TickPeriodFindRequest;
import pro.crypto.request.TickTimeFindRequest;
import pro.crypto.service.TickService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static pro.crypto.front.office.configuration.swagger.SwaggerConstants.AUTHORIZATION_HEADER_DESCRIPTION;

@RestController
@RequestMapping("/ticks")
@AllArgsConstructor
public class TickController {

    private final TickService tickService;

    @GetMapping(value = "/time", produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = AUTHORIZATION_HEADER_DESCRIPTION, required = true, dataType = "string", paramType = "header", example = "Bearer some_example_access_token")
    })
    @ResponseBody
    public TickDataResponse findTickByTime(@Valid @NotNull TickTimeFindRequest request) {
        return TickDataResponse.fromSnapshot(tickService.getTicksByTime(request));
    }

    @GetMapping(value = "/period", produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = AUTHORIZATION_HEADER_DESCRIPTION, required = true, dataType = "string", paramType = "header", example = "Bearer some_example_access_token")
    })
    @ResponseBody
    public TickDataResponse findTickByPeriod(@Valid @NotNull TickPeriodFindRequest request) {
        return TickDataResponse.fromSnapshot(tickService.getTicksByPeriod(request));
    }

}
