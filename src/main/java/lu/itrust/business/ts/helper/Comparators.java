package lu.itrust.business.ts.helper;

import lu.itrust.business.ts.model.asset.Asset;
import lu.itrust.business.ts.model.scenario.Scenario;

public final class Comparators {

    public static java.util.Comparator<? super Asset> assetByValue() {
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

    public static java.util.Comparator<? super Asset> assetByName() {
        return (a1, a2) -> {
            int result = NaturalOrderComparator.compareTo(a1.getName(), a2.getName());
            if (result == 0) {
                result = Double.compare(a2.getValue(), a1.getValue());
                if (result == 0)
                    result = Double.compare(a2.getALE(), a1.getALE());
            }
            return result;
        };
    }

    public static void sortAssetsByValue(java.util.List<Asset> assets) {
        assets.sort(assetByValue());
    }

    public static void sortAssetsByName(java.util.List<Asset> assets) {
        assets.sort(assetByName());
    }

    public static void sortScenariosByName(java.util.List<Scenario> scenarios) {
        scenarios.sort((s1, s2) -> NaturalOrderComparator.compareTo(s1.getName(), s2.getName()));
    }

}
