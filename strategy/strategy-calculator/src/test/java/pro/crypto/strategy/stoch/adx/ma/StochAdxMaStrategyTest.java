package pro.crypto.strategy.stoch.adx.ma;

import org.junit.Test;
import pro.crypto.request.StrategyRequest;
import pro.crypto.response.StrategyResult;
import pro.crypto.strategy.StrategyBaseTest;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.indicator.IndicatorType.MODIFIED_MOVING_AVERAGE;
import static pro.crypto.model.strategy.Position.ENTRY_LONG;
import static pro.crypto.model.strategy.Position.ENTRY_SHORT;

public class StochAdxMaStrategyTest extends StrategyBaseTest {

    @Test
    public void testStochAdxMaStrategy() {
        StrategyResult[] expectedResult = loadStrategyExpectedResult("stoch_adx_ma.json", StochAdxMaResult[].class);
        StochAdxMaResult[] actualResult = new StochAdxMaStrategy(buildStochAdxMaRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private StrategyRequest buildStochAdxMaRequest() {
        return StochAdxMaRequest.builder()
                .originalData(originalData)
                .stochMovingAverageType(MODIFIED_MOVING_AVERAGE)
                .stochFastPeriod(5)
                .stochSlowPeriod(3)
                .adxPeriod(14)
                .firstMaPeriod(5)
                .secondMaPeriod(15)
                .thirdMaPeriod(30)
                .stochasticSignalLine(50d)
                .adxSignalLine(20d)
                .positions(newHashSet(ENTRY_LONG, ENTRY_SHORT))
                .build();
    }

}
