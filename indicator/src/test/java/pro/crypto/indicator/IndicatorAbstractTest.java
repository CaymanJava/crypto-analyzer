package pro.crypto.indicator;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import pro.crypto.indicator.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.tick.Tick;

import static java.time.LocalDateTime.of;

public abstract class IndicatorAbstractTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    protected Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    protected abstract IndicatorRequest buildRequest();

}
