package lu.itrust.business.TS.model.api.basic;

import lu.itrust.business.TS.model.ilr.AssetEdge;

public class ApiEdge {

    private String source;

    private String target;

    private double p = 1;

    public ApiEdge() {
    }

    public ApiEdge(AssetEdge assetEdge) {
        setSource(ApiNode.getId(assetEdge.getParent()));
        setTarget(ApiNode.getId(assetEdge.getChild()));
        setP(assetEdge.getWeight());
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public double getP() {
        return p == 0 ? 1 : p;
    }

    public void setP(double p) {
        this.p = p;
    }

}
