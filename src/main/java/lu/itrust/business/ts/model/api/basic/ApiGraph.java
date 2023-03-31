package lu.itrust.business.ts.model.api.basic;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import lu.itrust.business.ts.model.ilr.AssetNode;
import lu.itrust.business.ts.model.ilr.Position;

public class ApiGraph {

    private Map<String, ApiNode> nodes = new HashMap<>();

    private List<ApiEdge> edges = new ArrayList<>();

    public ApiGraph() {
    }

    public ApiGraph(List<AssetNode> assetNodes) {

        this.nodes = assetNodes.stream()
                .collect(Collectors.toMap(ApiNode::getId, ApiNode::new));

        this.edges = assetNodes.stream().flatMap(e -> e.getEdges().values().stream())
                .map(ApiEdge::new).collect(Collectors.toList());

        this.generateRandomPosition();
    }

    public ApiGraph(Map<String, ApiNode> nodes, List<ApiEdge> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public Map<String, ApiNode> getNodes() {
        return nodes;
    }

    public void setNodes(Map<String, ApiNode> nodes) {
        this.nodes = nodes;
    }

    public List<ApiEdge> getEdges() {
        return edges;
    }

    public void setEdges(List<ApiEdge> edges) {
        this.edges = edges;
    }

    protected void generateRandomPosition() {
        final SecureRandom random = new SecureRandom();
        nodes.values().parallelStream().filter(e -> e.getPosition().isEmpty()).forEach(e -> {
            final Position position = Position.generate(nodes.size(), random);
            e.getPosition().put(ApiNode.POSITION_X, position.getX());
            e.getPosition().put(ApiNode.POSITION_Y, position.getY());
        });
    }

}
