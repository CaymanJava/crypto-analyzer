package pro.crypto.indicator.tick.generator;

import pro.crypto.model.tick.Tick;

import java.time.LocalDateTime;

public class OneDayTickWithFullPriceGenerator extends TickAbstractGenerator {

    public OneDayTickWithFullPriceGenerator(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    @Override
    public Tick[] generate() {
        return loadOriginalData("original_data.json");
    }

}
