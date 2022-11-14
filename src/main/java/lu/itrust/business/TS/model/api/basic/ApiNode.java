package lu.itrust.business.TS.model.api.basic;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import lu.itrust.business.TS.model.ilr.AssetNode;
import lu.itrust.business.TS.model.ilr.Position;

public class ApiNode {

    public static final String TRICK_ID = "trickId";
    public static final String DISABLED = "disabled";
    public static final String TYPE = "type";
    public static final String NAME = "name";
    public static final String ID = "id";

    public static final String POSITION_X = "x";
    public static final String POSITION_Y = "y";

    public Map<String, Object> data = new HashMap<>();
    public Map<String, Double> position = new HashMap<>();

    public ApiNode() {
    }

    public ApiNode(AssetNode node) {
        data.put(ID, getId(node));
        data.put(NAME, node.getAsset().getName());
        data.put(TYPE, node.getAsset().getAssetType().getName());
        data.put(DISABLED, !node.getAsset().isSelected());
        data.put(TRICK_ID, node.getAsset().getId());

        if (node.getPosition() != null) {
            position.put(POSITION_X, node.getPosition().getX());
            position.put(POSITION_Y, node.getPosition().getY());
        }
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Map<String, Double> getPosition() {
        return position;
    }

    public void setPosition(Map<String, Double> position) {
        this.position = position;
    }

    public static String getId(AssetNode node) {
        return String.format("U0.%d", node.getId());
    }

}
