package pro.crypto.helper;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import pro.crypto.model.IndicatorType;

import java.io.IOException;

import static org.apache.commons.lang.StringUtils.isEmpty;

public class IndicatorTypeAdapter extends TypeAdapter<IndicatorType> {

    @Override
    public void write(JsonWriter jsonWriter, IndicatorType indicatorType) throws IOException {
        jsonWriter.value(indicatorType.toString());
    }

    @Override
    public IndicatorType read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }
        String result = jsonReader.nextString();
        if (isEmpty(result)) {
            return null;
        }
        return IndicatorType.valueOf(result);
    }

}
