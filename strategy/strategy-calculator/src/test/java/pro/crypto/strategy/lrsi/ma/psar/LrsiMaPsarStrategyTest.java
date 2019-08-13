package pro.crypto.strategy.lrsi.ma.psar;

import org.junit.Test;
import pro.crypto.model.strategy.Position;
import pro.crypto.request.StrategyRequest;
import pro.crypto.response.StrategyResult;
import pro.crypto.strategy.StrategyBaseTest;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.indicator.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class LrsiMaPsarStrategyTest extends StrategyBaseTest {

    @Test
    public void testLrsiMaPsarStrategy() {
        StrategyResult[] expectedResult = loadStrategyExpectedResult("lrsi_ma_psar.json", LrsiMaPsarResult[].class);
        LrsiMaPsarResult[] actualResult = new LrsiMaPsarStrategy(buildLrsiMaPsarRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private StrategyRequest buildLrsiMaPsarRequest() {
        return LrsiMaPsarRequest.builder()
                .originalData(originalData)
                .lrsiGamma(0.5)
                .lrsiOversoldLevel(0.15)
                .lrsiOverboughtLevel(0.85)
                .maType(EXPONENTIAL_MOVING_AVERAGE)
                .maPriceType(CLOSE)
                .maPeriod(16)
                .psarMinAccelerationFactor(0.02)
                .psarMaxAccelerationFactor(0.2)
                .positions(newHashSet(Position.values()))
                .build();
    }

}
