package pro.crypto.strategy.pivot.rsi.macd.ma;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.StrategyRequest;
import pro.crypto.response.StrategyResult;
import pro.crypto.strategy.StrategyBaseTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Objects.nonNull;
import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.indicator.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.indicator.IndicatorType.SMOOTHED_MOVING_AVERAGE;
import static pro.crypto.model.strategy.Position.ENTRY_LONG;
import static pro.crypto.model.strategy.Position.ENTRY_SHORT;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class PivotRsiMacdMaStrategyTest extends StrategyBaseTest {

    @Test
    public void testPivotRsiMacdMaStrategy() {
        StrategyResult[] expectedResult = loadStrategyExpectedResult("pivot_rsi_macd_ma.json", PivotRsiMacdMaResult[].class);
        PivotRsiMacdMaResult[] actualResult = new PivotRsiMacdMaStrategy(buildPivotRsiMacdMaRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private StrategyRequest buildPivotRsiMacdMaRequest() {
        return PivotRsiMacdMaRequest.builder()
                .originalData(originalData)
                .oneDayTickData(getOneDayTickData())
                .rsiMaType(SMOOTHED_MOVING_AVERAGE)
                .rsiPeriod(14)
                .rsiSignalLine(50.0)
                .macdMaType(EXPONENTIAL_MOVING_AVERAGE)
                .macdPriceType(CLOSE)
                .macdFastPeriod(8)
                .macdSlowPeriod(17)
                .macdSignalPeriod(9)
                .maType(EXPONENTIAL_MOVING_AVERAGE)
                .maPriceType(CLOSE)
                .maPeriod(50)
                .positions(newHashSet(ENTRY_LONG, ENTRY_SHORT))
                .build();
    }

    @SneakyThrows(IOException.class)
    private Tick[] getOneDayTickData() {
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource("tick/usdt_btc_one_day.json");
        if (nonNull(url)) {
            File file = new File(url.getFile());
            return new Gson().fromJson(FileUtils.readFileToString(file, "UTF-8"), Tick[].class);
        }
        throw new FileNotFoundException();
    }

}
