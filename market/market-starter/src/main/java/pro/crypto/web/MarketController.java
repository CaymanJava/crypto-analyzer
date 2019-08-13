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
import pro.crypto.request.MarketFindRequest;
import pro.crypto.service.MarketService;
import pro.crypto.service.MarketSynchronizationService;
import pro.crypto.snapshot.MarketSnapshot;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping("/market")
@AllArgsConstructor
public class MarketController {

    private final MarketService marketService;
    private final MarketSynchronizationService synchronizationService;

    @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Page<MarketSnapshot> findAll(@Valid @NotNull MarketFindRequest request, Pageable pageable) {
        return marketService.findAll(request, pageable);
    }

    @GetMapping(value = "/{marketId}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public MarketSnapshot findById(@PathVariable("marketId") Long marketId) {
        return marketService.findById(marketId);
    }

    @GetMapping(value = "/synchronize")
    @ResponseStatus(HttpStatus.OK)
    public void synchronizeMarkets() {
        synchronizationService.synchronizeMarkets();
    }

}
