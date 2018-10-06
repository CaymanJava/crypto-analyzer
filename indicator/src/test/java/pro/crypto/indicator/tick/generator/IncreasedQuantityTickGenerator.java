package pro.crypto.indicator.tick.generator;

import pro.crypto.model.tick.Tick;

import java.time.LocalDateTime;

public class IncreasedQuantityTickGenerator extends OneDayTickWithFullPriceGenerator {

    public IncreasedQuantityTickGenerator(LocalDateTime startDateTime) {
        super(startDateTime);
    }

    @Override
    public Tick[] generate() {
        return loadOriginalData("increased_quantity_original_data.json");
    }

}
