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
import static pro.crypto.model.market.Stock.BITTREX;
import static pro.crypto.model.tick.TimeFrame.FIVE_MIN;

@RunWith(SpringRunner.class)
@SpringBootTest("bittrex.spider.url=localhost:5987")
public class DataSupplierTest {

    @Autowired
    private DataSupplier dataSupplier;
    private static final Stock STOCK = BITTREX;
    private static final String MARKET_NAME = "BTC-ETH";
    private static final long MARKET_ID = 78;

    @Test
    @Ignore
    public void getAllStockMarketsTest() throws Exception {
        MarketData markets = dataSupplier.getAllStockMarkets(STOCK);
        assertEquals(markets.getStockExchangeName(), STOCK);
        assertTrue(markets.getMarkets().length > 0);
    }

    @Test
    @Ignore
    public void getStockMarketByNameTest() throws Exception {
        MarketData marketData = dataSupplier.getStockMarketByName(STOCK, MARKET_NAME);
        assertEquals(marketData.getStockExchangeName(), STOCK);

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
    public void getAllMonitoredMarketTest() throws Exception {
        MarketData monitoredMarket = dataSupplier.getAllMonitoredMarket(STOCK);
        assertEquals(monitoredMarket.getStockExchangeName(), STOCK);
    }

    @Test
    @Ignore
    public void getTicksByPeriodTest() throws Exception {
        TickData ticks = dataSupplier.getTicksByPeriod(GetTicksByPeriodRequest.builder()
                .stock(STOCK)
                .period(20)
                .marketId(MARKET_ID)
                .timeFrame(FIVE_MIN)
                .build());
        assertEquals(ticks.getStockExchangeName(), STOCK);
        assertEquals(ticks.getMarket().getId(), MARKET_ID);
        assertEquals(ticks.getTimeFrame(), FIVE_MIN);
        assertTrue(ticks.getTicks().length > 0);
    }

    @Test
    @Ignore
    public void getTicksByTimeTest() throws Exception {
        TickData ticks = dataSupplier.getTicksByTime(GetTickByTimeRequest.builder()
                .stock(STOCK)
                .marketId(MARKET_ID)
                .timeFrame(FIVE_MIN)
                .from(LocalDateTime.now().minusHours(5))
                .to(LocalDateTime.now())
                .build());
        assertEquals(ticks.getStockExchangeName(), STOCK);
        assertEquals(ticks.getMarket().getId(), MARKET_ID);
        assertEquals(ticks.getTimeFrame(), FIVE_MIN);
        assertTrue(ticks.getTicks().length > 0);
    }

}