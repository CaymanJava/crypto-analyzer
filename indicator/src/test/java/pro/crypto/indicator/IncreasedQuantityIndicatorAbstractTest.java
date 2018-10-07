package pro.crypto.indicator;

import org.junit.Before;
import pro.crypto.indicator.tick.generator.IncreasedQuantityTickGenerator;

public abstract class IncreasedQuantityIndicatorAbstractTest extends IndicatorAbstractTest {

    @Before
    public void init() {
        originalData = new IncreasedQuantityTickGenerator().generate();
    }

}
