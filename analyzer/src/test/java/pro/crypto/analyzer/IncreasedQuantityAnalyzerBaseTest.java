package pro.crypto.analyzer;

import org.junit.Before;
import pro.crypto.indicator.tick.generator.IncreasedQuantityTickGenerator;

public class IncreasedQuantityAnalyzerBaseTest extends AnalyzerBaseTest {

    @Before
    public void init() {
        originalData = new IncreasedQuantityTickGenerator().generate();
    }

}
