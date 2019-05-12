package pro.crypto.strategy.bws;

import org.junit.Test;
import pro.crypto.model.Position;
import pro.crypto.model.StrategyRequest;
import pro.crypto.model.StrategyResult;
import pro.crypto.strategy.StrategyBaseTest;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertArrayEquals;
import static pro.crypto.model.tick.TimeFrame.FIVE_MIN;

public class BillWilliamsStrategyTest extends StrategyBaseTest {

    @Test
    public void testBillWilliamsStrategy() {
        StrategyResult[] expectedResult = loadStrategyExpectedResult("bws.json", BWSResult[].class);
        BWSResult[] actualResult = new BillWilliamsStrategy(buildBWSRequest()).getResult();
        assertArrayEquals(expectedResult, actualResult);
    }

    private StrategyRequest buildBWSRequest() {
        return BWSRequest.builder()
                .originalData(originalData)
                .acFastPeriod(5)
                .acSlowPeriod(34)
                .acSmoothedPeriod(5)
                .alligatorJawPeriod(13)
                .alligatorJawOffset(8)
                .alligatorTeethPeriod(8)
                .alligatorTeethOffset(5)
                .alligatorLipsPeriod(5)
                .alligatorLipsOffset(3)
                .alligatorTimeFrame(FIVE_MIN)
                .aoFastPeriod(5)
                .aoSlowPeriod(34)
                .positions(newHashSet(Position.values()))
                .build();
    }

}
