package pro.crypto.indicator;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import pro.crypto.indicator.tick.generator.OneDayTickWithFullPriceGenerator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorResult;
import pro.crypto.model.tick.Tick;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import static java.time.LocalDateTime.of;
import static java.util.Objects.nonNull;

public abstract class IndicatorAbstractTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    protected Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickWithFullPriceGenerator(of(2018, 2, 25, 0, 0)).generate();
    }

    @SneakyThrows(IOException.class)
    protected IndicatorResult[] loadExpectedResult(String fileName, Class<? extends IndicatorResult[]> clazz) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource("indicator/result/" + fileName);
        if (nonNull(url)) {
            File file = new File(url.getFile());
            return new Gson().fromJson(FileUtils.readFileToString(file, "UTF-8"), clazz);
        }
        throw new FileNotFoundException();
    }

    protected abstract IndicatorRequest buildRequest();

}
