package pro.crypto.indicator;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.IndicatorRequest;
import pro.crypto.response.IndicatorResult;
import pro.crypto.tick.generator.OneDayTickGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import static java.util.Objects.nonNull;

public abstract class IndicatorAbstractTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    protected Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickGenerator().generate();
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
