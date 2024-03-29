package pro.crypto.analyzer;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.result.AnalyzerResult;
import pro.crypto.model.tick.Tick;
import pro.crypto.response.IndicatorResult;
import pro.crypto.tick.generator.OneDayTickGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import static java.util.Objects.nonNull;

public class AnalyzerBaseTest {

    protected Tick[] originalData;

    @Before
    public void init() {
        originalData = new OneDayTickGenerator().generate();
    }

    protected AnalyzerRequest buildAnalyzerRequest(String fileName, Class<? extends IndicatorResult[]> clazz) {
        IndicatorResult[] indicatorResults = loadIndicatorResult(fileName, clazz);
        return new AnalyzerRequest(originalData, indicatorResults);
    }

    @SneakyThrows(IOException.class)
    protected AnalyzerResult[] loadAnalyzerExpectedResult(String fileName, Class<? extends AnalyzerResult[]> clazz) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource("analyzer/result/" + fileName);
        if (nonNull(url)) {
            File file = new File(url.getFile());
            return new Gson().fromJson(FileUtils.readFileToString(file, "UTF-8"), clazz);
        }
        throw new FileNotFoundException();
    }

    @SneakyThrows(IOException.class)
    protected IndicatorResult[] loadIndicatorResult(String fileName, Class<? extends IndicatorResult[]> clazz) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource("analyzer/request/" + fileName);
        if (nonNull(url)) {
            File file = new File(url.getFile());
            return new Gson().fromJson(FileUtils.readFileToString(file, "UTF-8"), clazz);
        }
        throw new FileNotFoundException();
    }

}
