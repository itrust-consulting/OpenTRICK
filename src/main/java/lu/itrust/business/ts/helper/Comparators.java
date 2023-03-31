package lu.itrust.business.ts.helper;

import lu.itrust.business.ts.model.asset.Asset;

public final class Comparators {

    public static java.util.Comparator<? super Asset> ASSET() {
        return (a1, a2) -> {
            int result = Double.compare(a2.getValue(), a1.getValue());
            if (result == 0) {
                result = Double.compare(a2.getALE(), a1.getALE());
                if (result == 0)
                    result = NaturalOrderComparator.compareTo(a1.getName(), a2.getName());
            }
            return result;
        };
    }
}
