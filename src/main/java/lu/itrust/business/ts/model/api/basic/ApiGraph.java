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

/**
 * Represents an API graph.
 * The API graph consists of a collection of nodes and edges that define the structure and relationships of the API.
 * It provides methods to access and manipulate the nodes and edges of the graph.
 */
public class ApiGraph {

    private Map<String, ApiNode> nodes = new HashMap<>();

    private List<ApiEdge> edges = new ArrayList<>();

    public ApiGraph() {
    }

    /**
     * Constructs an API graph based on a list of asset nodes.
     * The nodes and edges are derived from the asset nodes.
     * Random positions are generated for nodes without assigned positions.
     *
     * @param assetNodes the list of asset nodes to build the graph from
     */
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
    

    /**
     * Returns the map of nodes in the API graph.
     *
     * @return the map of nodes
     */
    public Map<String, ApiNode> getNodes() {
        return nodes;
    }

    /**
     * Sets the nodes of the API graph.
     *
     * @param nodes a map containing the nodes of the graph, where the key is the node identifier and the value is the ApiNode object
     */
    public void setNodes(Map<String, ApiNode> nodes) {
        this.nodes = nodes;
    }

    /**
     * Returns the list of edges in the API graph.
     *
     * @return the list of edges
     */
    public List<ApiEdge> getEdges() {
        return edges;
    }

    /**
     * Sets the list of edges for the API graph.
     *
     * @param edges the list of edges to set
     */
    public void setEdges(List<ApiEdge> edges) {
        this.edges = edges;
    }

    /**
     * Generates random positions for nodes that do not have a position assigned.
     * Uses a secure random number generator to generate random positions.
     */
    protected void generateRandomPosition() {
        final SecureRandom random = new SecureRandom();
        nodes.values().parallelStream().filter(e -> e.getPosition().isEmpty()).forEach(e -> {
            final Position position = Position.generate(nodes.size(), random);
            e.getPosition().put(ApiNode.POSITION_X, position.getX());
            e.getPosition().put(ApiNode.POSITION_Y, position.getY());
        });
    }

}
