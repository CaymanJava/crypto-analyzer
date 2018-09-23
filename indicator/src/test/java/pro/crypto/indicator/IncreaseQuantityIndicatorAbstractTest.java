package pro.crypto.indicator;

import org.junit.Before;
import pro.crypto.indicator.tick.generator.IncreasedQuantityTickGenerator;

import static java.time.LocalDateTime.of;

public abstract class IncreaseQuantityIndicatorAbstractTest extends IndicatorAbstractTest {

    @Before
    public void init() {
        originalData = new IncreasedQuantityTickGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

}
