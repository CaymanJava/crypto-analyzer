package pro.crypto.strategy;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import pro.crypto.model.StrategyResult;
import pro.crypto.model.tick.Tick;
import pro.crypto.tick.generator.OneHourTickGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import static java.util.Objects.nonNull;

public abstract class StrategyBaseTest {

    protected Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneHourTickGenerator().generate();
    }

    @SneakyThrows(IOException.class)
    protected StrategyResult[] loadStrategyExpectedResult(String fileName, Class<? extends StrategyResult[]> clazz) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource("strategy/" + fileName);
        if (nonNull(url)) {
            File file = new File(url.getFile());
            return new Gson().fromJson(FileUtils.readFileToString(file, "UTF-8"), clazz);
        }
        throw new FileNotFoundException();
    }

}
