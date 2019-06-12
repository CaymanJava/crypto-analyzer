package pro.crypto.helper;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.math.BigDecimal;

import static org.apache.commons.lang.StringUtils.isEmpty;

public class BigDecimalTypeAdapter extends TypeAdapter<BigDecimal> {

    @Override
    public void write(JsonWriter jsonWriter, BigDecimal value) throws IOException {
        jsonWriter.value(value);
    }

    @Override
    public BigDecimal read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }
        String result = jsonReader.nextString();
        if (isEmpty(result)) {
            return null;
        }
        return new BigDecimal(result);
    }

}
