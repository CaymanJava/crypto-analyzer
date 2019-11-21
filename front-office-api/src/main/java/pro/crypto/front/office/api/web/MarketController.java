package pro.crypto.front.office.api.web;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
import pro.crypto.front.office.api.response.MarketResponse;
import pro.crypto.request.MarketFindRequest;
import pro.crypto.service.MarketService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static pro.crypto.front.office.configuration.swagger.SwaggerConstants.AUTHORIZATION_HEADER_DESCRIPTION;
import static pro.crypto.model.market.Status.AVAILABLE;

@RestController
@RequestMapping("/markets")
@AllArgsConstructor
public class MarketController {

    private final MarketService marketService;

    @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = AUTHORIZATION_HEADER_DESCRIPTION, required = true, dataType = "string", paramType = "header", example = "Bearer some_example_access_token")
    })
    @ResponseBody
    public Page<MarketResponse> findAll(@Valid @NotNull MarketFindRequest request, Pageable pageable) {
        request.setActive(true);
        request.setStatus(AVAILABLE);
        return marketService.findAll(request, pageable).map(MarketResponse::fromSnapshot);
    }

    @GetMapping(value = "/{marketId}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = AUTHORIZATION_HEADER_DESCRIPTION, required = true, dataType = "string", paramType = "header", example = "Bearer some_example_access_token")
    })
    @ResponseBody
    public MarketResponse findMarket(@PathVariable("marketId") Long marketId) {
        return MarketResponse.fromSnapshot(marketService.findById(marketId));
    }

}
