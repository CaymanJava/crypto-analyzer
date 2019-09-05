package pro.crypto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.market.Status;
import pro.crypto.model.market.Stock;
import pro.crypto.model.market.StockMarket;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

@Entity
@Table(schema = "crypto_market")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Market {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long marketId;

    @Enumerated(EnumType.STRING)
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

    @Enumerated(EnumType.STRING)
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

    public void update(StockMarket market) {
        if (isNull(market)) {
            return;
        }

        active = market.isActive();
        notice = ofNullable(market.getNotice()).orElse(notice);
        monitor = market.isMonitor();
        status = ofNullable(market.getStatus()).orElse(status);
        logoUrl = ofNullable(market.getLogoUrl()).orElse(logoUrl);
        high = ofNullable(market.getHigh()).orElse(high);
        low = ofNullable(market.getLow()).orElse(low);
        volume = ofNullable(market.getVolume()).orElse(volume);
        last = ofNullable(market.getLast()).orElse(last);
        baseVolume = ofNullable(market.getBaseVolume()).orElse(baseVolume);
        updateTime = ofNullable(market.getUpdateTime()).orElse(updateTime);
        bid = ofNullable(market.getBaseVolume()).orElse(bid);
        ask = ofNullable(market.getAsk()).orElse(ask);
        openBuyOrders = ofNullable(market.getOpenBuyOrders()).orElse(openBuyOrders);
        openSellOrders = ofNullable(market.getOpenSellOrders()).orElse(openSellOrders);
        prevDay = ofNullable(market.getPrevDay()).orElse(prevDay);
        priceDiff = ofNullable(market.getPriceDiff()).orElse(priceDiff);
    }

}
