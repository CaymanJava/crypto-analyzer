package pro.crypto.strategy.cci.rsi.atr;

import org.junit.Test;
import pro.crypto.model.Position;
import pro.crypto.model.StrategyRequest;
import pro.crypto.model.StrategyResult;
import pro.crypto.strategy.StrategyBaseTest;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.IndicatorType.SIMPLE_MOVING_AVERAGE;
import static pro.crypto.model.IndicatorType.SMOOTHED_MOVING_AVERAGE;

public class CciRsiAtrStrategyTest extends StrategyBaseTest {

    @Test
    public void testCciRsiAtrStrategy() throws Exception {
        StrategyResult[] expectedResult = loadStrategyExpectedResult("cci_rsi_atr.json", CciRsiAtrResult[].class);
        CciRsiAtrResult[] actualResult = new CciRsiAtrStrategy(buildCciRsiAtrRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private StrategyRequest buildCciRsiAtrRequest() {
        return CciRsiAtrRequest.builder()
                .originalData(originalData)
                .cciPeriod(34)
                .cciSignalLine(0.0)
                .rsiMaType(SMOOTHED_MOVING_AVERAGE)
                .rsiPeriod(10)
                .rsiSignalLine(50.0)
                .atrPeriod(14)
                .atrMaType(SIMPLE_MOVING_AVERAGE)
                .atrMaPeriod(5)
                .positions(newHashSet(Position.ENTRY_LONG, Position.ENTRY_SHORT))
                .build();
    }

}
