package pro.crypto.tick.generator;

import pro.crypto.model.tick.Tick;

public class OneDayTickGenerator extends TickAbstractGenerator {

    @Override
    public Tick[] generate() {
        return loadOriginalData("original_data.json");
    }

}
