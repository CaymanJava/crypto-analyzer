package pro.crypto.strategy.adxstochma;

import org.junit.Test;
import pro.crypto.model.Position;
import pro.crypto.model.StrategyRequest;
import pro.crypto.model.StrategyResult;
import pro.crypto.strategy.StrategyBaseTest;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.IndicatorType.MODIFIED_MOVING_AVERAGE;

public class ADXStochMAStrategyTest extends StrategyBaseTest {

    @Test
    public void testADXStochMAStrategy() throws Exception {
        StrategyResult[] expectedResult = loadStrategyExpectedResult("adx_stoch_ma.json", ADXStochMAResult[].class);
        ADXStochMAResult[] actualResult = new ADXStochMAStrategy(buildADXStochMARequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private StrategyRequest buildADXStochMARequest() {
        return ADXStochMARequest.builder()
                .originalData(originalData)
                .stochMovingAverageType(MODIFIED_MOVING_AVERAGE)
                .stochFastPeriod(5)
                .stochSlowPeriod(3)
                .adxPeriod(14)
                .firstMaPeriod(5)
                .secondMaPeriod(15)
                .thirdMaPeriod(30)
                .positions(newHashSet(Position.values()))
                .build();
    }

}
