package pro.crypto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.market.Stock;
import pro.crypto.snapshot.MarketSnapshot;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MarketResponse {

    private long id;

    private Stock stock;

    private String marketCurrency;

    private String baseCurrency;

    private String marketCurrencyLong;

    private String baseCurrencyLong;

    private BigDecimal minTradeSize;

    private String marketName;

    private LocalDateTime created;

    private String logoUrl;

    private BigDecimal volume;

    private BigDecimal baseVolume;

    private BigDecimal priceDiff;

    private BigDecimal last;

    private BigDecimal high;

    private BigDecimal low;

    public static MarketResponse fromSnapshot(MarketSnapshot marketSnapshot) {
        return MarketResponse.builder()
                .id(marketSnapshot.getId())
                .stock(marketSnapshot.getStock())
                .marketCurrency(marketSnapshot.getMarketCurrency())
                .baseCurrency(marketSnapshot.getBaseCurrency())
                .marketCurrencyLong(marketSnapshot.getMarketCurrencyLong())
                .baseCurrencyLong(marketSnapshot.getBaseCurrencyLong())
                .minTradeSize(marketSnapshot.getMinTradeSize())
                .marketName(marketSnapshot.getMarketName())
                .created(marketSnapshot.getCreated())
                .logoUrl(marketSnapshot.getLogoUrl())
                .volume(marketSnapshot.getVolume())
                .baseVolume(marketSnapshot.getBaseVolume())
                .priceDiff(marketSnapshot.getPriceDiff())
                .last(marketSnapshot.getLast())
                .high(marketSnapshot.getHigh())
                .low(marketSnapshot.getLow())
                .build();
    }

}
