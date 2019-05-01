package pro.crypto.model.market;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StockMarket {

    private long id;

    private String marketCurrency;

    private String baseCurrency;

    private String marketCurrencyLong;

    private String baseCurrencyLong;

    private BigDecimal minTradeSize;

    private String marketName;

    private boolean active;

    private LocalDateTime created;

    private boolean monitor;

    private Status status;

    private String notice;

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
