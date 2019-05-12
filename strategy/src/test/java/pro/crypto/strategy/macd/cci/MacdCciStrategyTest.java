package pro.crypto.strategy.macd.cci;

import org.junit.Test;
import pro.crypto.model.Position;
import pro.crypto.model.StrategyRequest;
import pro.crypto.model.StrategyResult;
import pro.crypto.strategy.StrategyBaseTest;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
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
