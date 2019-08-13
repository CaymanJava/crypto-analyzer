package pro.crypto.strategy.macd.cci;

import org.junit.Test;
import pro.crypto.model.strategy.Position;
import pro.crypto.request.StrategyRequest;
import pro.crypto.response.StrategyResult;
import pro.crypto.strategy.StrategyBaseTest;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.indicator.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class MacdCciStrategyTest extends StrategyBaseTest {

    @Test
    public void testMacdCciStrategy() {
        StrategyResult[] expectedResult = loadStrategyExpectedResult("macd_cci.json", MacdCciResult[].class);
        MacdCciResult[] actualResult = new MacdCciStrategy(buildMacdCciRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private StrategyRequest buildMacdCciRequest() {
        return MacdCciRequest.builder()
                .originalData(originalData)
                .macdMaType(EXPONENTIAL_MOVING_AVERAGE)
                .macdPriceType(CLOSE)
                .macdFastPeriod(8)
                .macdSlowPeriod(17)
                .macdSignalPeriod(2)
                .cciPeriod(14)
                .cciOversoldLevel(-100.0)
                .cciOverboughtLevel(100.0)
                .positions(newHashSet(Position.values()))
                .build();
    }

}
