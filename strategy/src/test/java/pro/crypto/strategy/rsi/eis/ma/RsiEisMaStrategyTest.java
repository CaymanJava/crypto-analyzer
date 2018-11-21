package pro.crypto.strategy.rsi.eis.ma;

import org.junit.Test;
import pro.crypto.model.Position;
import pro.crypto.model.StrategyRequest;
import pro.crypto.model.StrategyResult;
import pro.crypto.strategy.StrategyBaseTest;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.IndicatorType.EXPONENTIAL_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.SMOOTHED_MOVING_AVERAGE;
import static pro.crypto.model.tick.PriceType.CLOSE;

public class RsiEisMaStrategyTest extends StrategyBaseTest {

    @Test
    public void testRsiEisMaStrategy() throws Exception {
        StrategyResult[] expectedResult = loadStrategyExpectedResult("rsi_eis_ma.json", RsiEisMaResult[].class);
        RsiEisMaResult[] actualResult = new RsiEisMaStrategy(buildRsiEisMaRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private StrategyRequest buildRsiEisMaRequest() {
        return RsiEisMaRequest.builder()
                .originalData(originalData)
                .rsiMaType(SMOOTHED_MOVING_AVERAGE)
                .rsiPeriod(14)
                .rsiSignalLine(50.0)
                .eisMaPeriod(13)
                .eisMaType(EXPONENTIAL_MOVING_AVERAGE)
                .eisMaPriceType(CLOSE)
                .eisMacdMaType(EXPONENTIAL_MOVING_AVERAGE)
                .eisMacdPriceType(CLOSE)
                .eisMacdFastPeriod(12)
                .eisMacdSlowPeriod(26)
                .eisMacdSignalPeriod(9)
                .fastMaType(EXPONENTIAL_MOVING_AVERAGE)
                .fastMaPriceType(CLOSE)
                .fastMaPeriod(5)
                .slowMaType(EXPONENTIAL_MOVING_AVERAGE)
                .slowMaPriceType(CLOSE)
                .slowMaPeriod(10)
                .positions(newHashSet(Position.ENTRY_LONG, Position.ENTRY_SHORT))
                .build();
    }

}