package pro.crypto.tick.generator;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import pro.crypto.model.tick.Tick;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import static java.util.Objects.nonNull;

public abstract class RealDataGenerator {

    public abstract Tick[] generate();

    @SneakyThrows(IOException.class)
    Tick[] loadOriginalData(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource("tick/" + fileName);
        if (nonNull(url)) {
            File file = new File(url.getFile());
            return new Gson().fromJson(FileUtils.readFileToString(file, "UTF-8"), Tick[].class);
        }
        throw new FileNotFoundException();
    }

}
