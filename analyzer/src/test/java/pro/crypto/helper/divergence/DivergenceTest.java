package pro.crypto.helper.divergence;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import pro.crypto.helper.IndicatorResultExtractor;
import pro.crypto.indicator.adl.ADLResult;
import pro.crypto.indicator.tick.generator.OneDayTickGenerator;
import pro.crypto.model.IndicatorResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import static java.util.Objects.nonNull;
import static org.junit.Assert.assertArrayEquals;

public class DivergenceTest {

    @Test
    public void testDivergence() {
        DivergenceResult[] expectedResult = loadExpectedResult();
        DivergenceResult[] actualResult = new Divergence(buildDivergenceRequest()).find();
        assertArrayEquals(expectedResult, actualResult);
    }

    private DivergenceRequest buildDivergenceRequest() {
        ADLResult[] result = (ADLResult[]) loadIndicatorResult();
        return DivergenceRequest.builder()
                .originalData(new OneDayTickGenerator().generate())
                .indicatorValues(IndicatorResultExtractor.extractIndicatorValue(result))
                .build();
    }

    @SneakyThrows(IOException.class)
    private DivergenceResult[] loadExpectedResult() {
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource("analyzer/result/divergence.json");
        if (nonNull(url)) {
            File file = new File(url.getFile());
            return new Gson().fromJson(FileUtils.readFileToString(file, "UTF-8"), DivergenceResult[].class);
        }
        throw new FileNotFoundException();
    }

    @SneakyThrows(IOException.class)
    private IndicatorResult[] loadIndicatorResult() {
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource("analyzer/request/adl_indicator.json");
        if (nonNull(url)) {
            File file = new File(url.getFile());
            return new Gson().fromJson(FileUtils.readFileToString(file, "UTF-8"), ADLResult[].class);
        }
        throw new FileNotFoundException();
    }

}
