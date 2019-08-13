package pro.crypto.factory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.springframework.stereotype.Component;
import pro.crypto.helper.BigDecimalTypeAdapter;
import pro.crypto.helper.IndicatorTypeAdapter;
import pro.crypto.model.indicator.IndicatorType;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

@Component
public class JsonParser {

    private Gson gson;

    @PostConstruct
    public void init() {
        gson = new GsonBuilder()
                .registerTypeAdapter(BigDecimal.class, new BigDecimalTypeAdapter())
                .registerTypeAdapter(IndicatorType.class, new IndicatorTypeAdapter())
                .create();
    }

    <T> T parse(String json, Class<T> clazz) throws JsonSyntaxException {
        return gson.fromJson(json, clazz);
    }

}
