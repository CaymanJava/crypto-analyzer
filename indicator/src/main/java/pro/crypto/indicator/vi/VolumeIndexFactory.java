package pro.crypto.indicator.vi;

import pro.crypto.exception.UnknownTypeException;

import static java.lang.String.format;
import static java.util.Objects.isNull;

public class VolumeIndexFactory {

    public static VolumeIndex create(VIRequest request) {
        if (isNull(request.getVolumeIndexType())) {
            throw new UnknownTypeException(format("Unknown volume index type {type: {%s}}", request.getVolumeIndexType()));
        }

        switch (request.getVolumeIndexType()) {
            case POSITIVE_VOLUME_INDEX:
                return new PositiveVolumeIndex(request);
            case NEGATIVE_VOLUME_INDEX:
                return new NegativeVolumeIndex(request);
            default:
                throw new UnknownTypeException(format("Unknown volume index type {type: {%s}}", request.getVolumeIndexType()));
        }
    }

}
