package pro.crypto.strategy.stoch.adx.ma;

import org.junit.Test;
import pro.crypto.model.StrategyRequest;
import pro.crypto.model.StrategyResult;
import pro.crypto.strategy.StrategyBaseTest;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.IndicatorType.MODIFIED_MOVING_AVERAGE;
import static pro.crypto.model.Position.ENTRY_LONG;
import static pro.crypto.model.Position.ENTRY_SHORT;

public class StochADXMAStrategyTest extends StrategyBaseTest {

    @Test
    public void testStochADXMAStrategy() throws Exception {
        StrategyResult[] expectedResult = loadStrategyExpectedResult("stoch_adx_ma.json", StochADXMAResult[].class);
        StochADXMAResult[] actualResult = new StochADXMAStrategy(buildStochADXMARequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private StrategyRequest buildStochADXMARequest() {
        return StochADXMARequest.builder()
                .originalData(originalData)
                .stochMovingAverageType(MODIFIED_MOVING_AVERAGE)
                .stochFastPeriod(5)
                .stochSlowPeriod(3)
                .adxPeriod(14)
                .firstMaPeriod(5)
                .secondMaPeriod(15)
                .thirdMaPeriod(30)
                .positions(newHashSet(ENTRY_LONG, ENTRY_SHORT))
                .build();
    }

}
