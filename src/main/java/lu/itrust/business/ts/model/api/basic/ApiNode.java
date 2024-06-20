package lu.itrust.business.ts.model.api.basic;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import lu.itrust.business.ts.model.ilr.AssetNode;
import lu.itrust.business.ts.model.ilr.Position;

/**
 * Represents an API node.
 * This class is used to store information about an API node, including its data and position.
 */
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

    /**
     * Constructs a new empty ApiNode object.
     */
    public ApiNode() {
    }

    /**
     * Constructs a new ApiNode object based on the provided AssetNode.
     *
     * @param node The AssetNode object to create the ApiNode from.
     */
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

    /**
     * Gets the data of the ApiNode.
     *
     * @return The data of the ApiNode.
     */
    public Map<String, Object> getData() {
        return data;
    }

    /**
     * Sets the data of the ApiNode.
     *
     * @param data The data to set for the ApiNode.
     */
    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    /**
     * Gets the position of the ApiNode.
     *
     * @return The position of the ApiNode.
     */
    public Map<String, Double> getPosition() {
        return position;
    }

    /**
     * Sets the position of the ApiNode.
     *
     * @param position The position to set for the ApiNode.
     */
    public void setPosition(Map<String, Double> position) {
        this.position = position;
    }

    /**
     * Gets the ID of the provided AssetNode.
     *
     * @param node The AssetNode object.
     * @return The ID of the AssetNode.
     */
    public static String getId(AssetNode node) {
        return String.format("U0.%d", node.getId());
    }

}
