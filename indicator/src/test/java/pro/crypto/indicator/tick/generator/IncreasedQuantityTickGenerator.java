package pro.crypto.indicator.tick.generator;

import pro.crypto.model.tick.Tick;

public class IncreasedQuantityTickGenerator extends OneDayTickGenerator {

    @Override
    public Tick[] generate() {
        return loadOriginalData("increased_quantity_original_data.json");
    }

}
