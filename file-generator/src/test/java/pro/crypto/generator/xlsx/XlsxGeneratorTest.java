package pro.crypto.generator.xlsx;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pro.crypto.model.tick.Tick;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import static java.util.Objects.nonNull;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class XlsxGeneratorTest {

    private final static int EXPECTED_BYTE_ARRAY_LENGTH = 7394;

    @Autowired
    private XlsxGenerator xlsxGenerator;

    private Tick[] originalData;

    @Before
    public void init() {
        originalData = loadOriginalData();
    }

    @Test
    public void testXlsxTickGeneration() throws Exception {
        byte[] actualFileBytes = xlsxGenerator.generateTickFile(originalData);
        assertEquals(actualFileBytes.length, EXPECTED_BYTE_ARRAY_LENGTH);
    }

    @SneakyThrows(IOException.class)
    private Tick[] loadOriginalData() {
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource("tick/test_data.json");
        if (nonNull(url)) {
            File file = new File(url.getFile());
            return new Gson().fromJson(FileUtils.readFileToString(file, "UTF-8"), Tick[].class);
        }
        throw new FileNotFoundException();
    }

}