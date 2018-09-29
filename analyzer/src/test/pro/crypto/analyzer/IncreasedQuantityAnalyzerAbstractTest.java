package pro.crypto.analyzer;

import org.junit.Before;
import pro.crypto.indicator.tick.generator.IncreasedQuantityTickGenerator;

import static java.time.LocalDateTime.of;

public abstract class IncreasedQuantityAnalyzerAbstractTest extends AnalyzerAbstractTest {

    @Before
    public void init() {
        originalData = new IncreasedQuantityTickGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

}
