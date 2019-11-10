package pro.crypto.helper;

import java.util.Collection;

import static java.util.Objects.isNull;

public class CollectionHelper {

    public static boolean nonEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    private static boolean isEmpty(Collection<?> collection) {
        return isNull(collection) || collection.isEmpty();
    }

}
