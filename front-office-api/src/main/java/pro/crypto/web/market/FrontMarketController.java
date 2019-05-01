package pro.crypto.web.market;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pro.crypto.request.MarketFindRequest;
import pro.crypto.response.MarketResponse;
import pro.crypto.service.MarketService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static pro.crypto.model.market.Status.AVAILABLE;

@RestController
@RequestMapping("/front-office/market")
@AllArgsConstructor
public class FrontMarketController {

    private final MarketService marketService;

    @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Page<MarketResponse> findAll(@Valid @NotNull MarketFindRequest request, Pageable pageable) {
        request.setActive(true);
        request.setStatus(AVAILABLE);
        return marketService.findAll(request, pageable).map(MarketResponse::fromSnapshot);
    }

}
