package pro.crypto.snapshot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.market.Status;
import pro.crypto.model.market.Stock;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MarketSnapshot {

    private long id;

    private long marketId;

    private Stock stock;

    private String marketCurrency;

    private String baseCurrency;

    private String marketCurrencyLong;

    private String baseCurrencyLong;

    private BigDecimal minTradeSize;

    private String marketName;

    private boolean active;

    private LocalDateTime created;

    private String notice;

    private boolean monitor;

    private Status status;

    private String logoUrl;

    private BigDecimal high;

    private BigDecimal low;

    private BigDecimal volume;

    private BigDecimal last;

    private BigDecimal baseVolume;

    private LocalDateTime updateTime;

    private BigDecimal bid;

    private BigDecimal ask;

    private Integer openBuyOrders;

    private Integer openSellOrders;

    private BigDecimal prevDay;

    private BigDecimal priceDiff;

}
