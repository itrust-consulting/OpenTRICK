package lu.itrust.business.ts.model.api.basic;

import lu.itrust.business.ts.model.ilr.AssetEdge;

/**
 * Represents an edge in the API graph.
 */
public class ApiEdge {

    private String source;

    private String target;

    private double p = 1;

    /**
     * Default constructor.
     */
    public ApiEdge() {
    }

    /**
     * Constructs an ApiEdge object based on an AssetEdge object.
     *
     * @param assetEdge The AssetEdge object to create the ApiEdge from.
     */
    public ApiEdge(AssetEdge assetEdge) {
        setSource(ApiNode.getId(assetEdge.getParent()));
        setTarget(ApiNode.getId(assetEdge.getChild()));
        setP(assetEdge.getWeight());
    }

    /**
     * Gets the source node of the edge.
     *
     * @return The source node.
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the source node of the edge.
     *
     * @param source The source node to set.
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Gets the target node of the edge.
     *
     * @return The target node.
     */
    public String getTarget() {
        return target;
    }

    /**
     * Sets the target node of the edge.
     *
     * @param target The target node to set.
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * Gets the probability of the edge.
     *
     * @return The probability value.
     */
    public double getP() {
        return p == 0 ? 1 : p;
    }

    /**
     * Sets the probability of the edge.
     *
     * @param p The probability value to set.
     */
    public void setP(double p) {
        this.p = p;
    }

}
