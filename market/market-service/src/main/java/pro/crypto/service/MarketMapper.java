package pro.crypto.service;

import org.springframework.stereotype.Component;
import pro.crypto.model.Market;
import pro.crypto.model.market.Stock;
import pro.crypto.model.market.StockMarket;
import pro.crypto.snapshot.MarketSnapshot;

@Component
public class MarketMapper {

    Market toMarket(StockMarket market, Stock stockExchangeName) {
        return Market.builder()
                .stock(stockExchangeName)
                .marketId(market.getId())
                .marketCurrency(market.getMarketCurrency())
                .baseCurrency(market.getBaseCurrency())
                .marketCurrencyLong(market.getMarketCurrencyLong())
                .baseCurrencyLong(market.getBaseCurrencyLong())
                .minTradeSize(market.getMinTradeSize())
                .marketName(market.getMarketName())
                .active(market.isActive())
                .created(market.getCreated())
                .notice(market.getNotice())
                .monitor(market.isMonitor())
                .status(market.getStatus())
                .logoUrl(market.getLogoUrl())
                .high(market.getHigh())
                .low(market.getLow())
                .volume(market.getVolume())
                .last(market.getLast())
                .baseVolume(market.getBaseVolume())
                .updateTime(market.getUpdateTime())
                .bid(market.getBid())
                .ask(market.getAsk())
                .openBuyOrders(market.getOpenBuyOrders())
                .openSellOrders(market.getOpenSellOrders())
                .prevDay(market.getPrevDay())
                .priceDiff(market.getPriceDiff())
                .build();
    }

    MarketSnapshot toSnapshot(Market market) {
        return MarketSnapshot.builder()
                .id(market.getId())
                .marketId(market.getMarketId())
                .stock(market.getStock())
                .marketCurrency(market.getMarketCurrency())
                .baseCurrency(market.getBaseCurrency())
                .marketCurrencyLong(market.getMarketCurrencyLong())
                .baseCurrencyLong(market.getBaseCurrencyLong())
                .minTradeSize(market.getMinTradeSize())
                .marketName(market.getMarketName())
                .active(market.isActive())
                .created(market.getCreated())
                .notice(market.getNotice())
                .monitor(market.isMonitor())
                .status(market.getStatus())
                .logoUrl(market.getLogoUrl())
                .high(market.getHigh())
                .low(market.getLow())
                .volume(market.getVolume())
                .last(market.getLast())
                .baseVolume(market.getBaseVolume())
                .updateTime(market.getUpdateTime())
                .bid(market.getBid())
                .ask(market.getAsk())
                .openBuyOrders(market.getOpenBuyOrders())
                .openSellOrders(market.getOpenSellOrders())
                .prevDay(market.getPrevDay())
                .priceDiff(market.getPriceDiff())
                .build();
    }

}
