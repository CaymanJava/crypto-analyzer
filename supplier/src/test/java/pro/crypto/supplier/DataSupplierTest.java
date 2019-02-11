package pro.crypto.supplier;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pro.crypto.model.market.Market;
import pro.crypto.model.market.MarketData;
import pro.crypto.model.market.Stock;
import pro.crypto.model.tick.TickData;
import pro.crypto.request.GetTickByTimeRequest;
import pro.crypto.request.GetTicksByPeriodRequest;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pro.crypto.model.tick.TimeFrame.FIVE_MIN;

@RunWith(SpringRunner.class)
@SpringBootTest("bittrex.spider.url=localhost:5987")
public class DataSupplierTest {

    @Autowired
    private DataSupplier dataSupplier;
    private static final Stock BITTREX = Stock.BITTREX;
    private static final String MARKET_NAME = "BTC-ETH";
    private static final long MARKET_ID = 78;

    @Test
    @Ignore
    public void getAllStockMarketsTest() {
        MarketData markets = dataSupplier.getAllStockMarkets(BITTREX);
        assertEquals(markets.getStockExchangeName(), BITTREX);
        assertTrue(markets.getMarkets().length > 0);
    }

    @Test
    @Ignore
    public void getStockMarketByNameTest() {
        MarketData marketData = dataSupplier.getStockMarketByName(BITTREX, MARKET_NAME);
        assertEquals(marketData.getStockExchangeName(), BITTREX);

        Market[] markets = marketData.getMarkets();
        assertTrue(markets.length == 1);

        Market market = markets[0];
        assertEquals(market.getMarketCurrency(), "ETH");
        assertEquals(market.getBaseCurrency(), "BTC");
        assertEquals(market.getMarketCurrencyLong(), "Ethereum");
        assertEquals(market.getBaseCurrencyLong(), "Bitcoin");
        assertEquals(market.getMarketName(), MARKET_NAME);
    }

    @Test
    @Ignore
    public void getAllMonitoredMarketTest() {
        MarketData monitoredMarket = dataSupplier.getAllMonitoredMarket(BITTREX);
        assertEquals(monitoredMarket.getStockExchangeName(), BITTREX);
    }

    @Test
    @Ignore
    public void getTicksByPeriodTest() {
        TickData marketResponse = dataSupplier.getTicksByPeriod(GetTicksByPeriodRequest.builder()
                .stock(BITTREX)
                .period(300)
                .marketId(MARKET_ID)
                .timeFrame(FIVE_MIN)
                .build());
        assertEquals(marketResponse.getStockExchangeName(), BITTREX);
        assertEquals(marketResponse.getMarket().getId(), MARKET_ID);
        assertEquals(marketResponse.getTimeFrame(), FIVE_MIN);
        assertTrue(marketResponse.getTicks().length > 0);
    }

    @Test
    @Ignore
    public void getTicksByTimeTest() {
        TickData ticks = dataSupplier.getTicksByTime(GetTickByTimeRequest.builder()
                .stock(BITTREX)
                .marketId(MARKET_ID)
                .timeFrame(FIVE_MIN)
                .from(LocalDateTime.now().minusHours(5))
                .to(LocalDateTime.now())
                .build());
        assertEquals(ticks.getStockExchangeName(), BITTREX);
        assertEquals(ticks.getMarket().getId(), MARKET_ID);
        assertEquals(ticks.getTimeFrame(), FIVE_MIN);
        assertTrue(ticks.getTicks().length > 0);
    }

}
