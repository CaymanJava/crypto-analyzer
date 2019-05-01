package pro.crypto.factory;

import com.google.gson.Gson;
import org.springframework.stereotype.Component;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;
import pro.crypto.model.tick.Tick;

@Component
public class IndicatorRequestFactory {

    public IndicatorRequest buildRequest(Tick[] ticks, IndicatorType indicatorType, String configuration) {
        IndicatorRequest indicatorRequest = new Gson().fromJson(configuration, indicatorType.getRequestClass());
        indicatorRequest.setOriginalData(ticks);
        return indicatorRequest;
    }

}
