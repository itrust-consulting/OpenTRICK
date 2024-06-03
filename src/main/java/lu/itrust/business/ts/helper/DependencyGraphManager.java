package lu.itrust.business.ts.helper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lu.itrust.business.ts.model.asset.Asset;
import lu.itrust.business.ts.model.ilr.AssetEdge;
import lu.itrust.business.ts.model.ilr.AssetNode;

/**
 * The DependencyGraphManager class provides utility methods for working with a
 * dependency graph of AssetNodes.
 * 
 * @author itrust consulting s.a r.l.
 */
public class DependencyGraphManager {

    /**
     * Private constructor to prevent instantiation.
     */
    private DependencyGraphManager() {
    }

    /**
     * Computes the impact of the given list of AssetNodes.
     * 
     * @param nodes the list of AssetNodes to compute the impact for
     * @return true if the impact was computed successfully, false otherwise
     */
    public static boolean computeImpact(List<AssetNode> nodes) {
        nodes.forEach(node -> {
            node.setInheritedConfidentiality(-1);
            node.setInheritedIntegrity(-1);
            node.setInheritedAvailability(-1);
        });

        if (isCyclic(nodes))
            return false;
            
        final List<AssetNode> roots = getRoots(nodes);
        roots.forEach(DependencyGraphManager::computeImpact);
        return true;
    }

    /**
     * Computes the impact of the given AssetNode.
     * On TRICK the graph is reverse compare to ILR dependancy graph.
     * third level -
     * -
     * third level - - > second level -> first level
     * -
     * third level -
     * 
     * @param node the AssetNode to compute the impact for
     */
    public static void computeImpact(AssetNode node) {
        if (node.getEdges().isEmpty()) {
            node.setInheritedConfidentiality(-1);
            node.setInheritedIntegrity(-1);
            node.setInheritedAvailability(-1);
        }
        node.getEdges().values().forEach(edge -> {
            final AssetNode child = edge.getChild();
            computeImpact(child);
            if (child.getAsset().isSelected()) {
                node.setInheritedConfidentiality(Math.max(child.getConfidentiality(), node.getInheritedAvailability()));
                node.setInheritedIntegrity(Math.max(child.getIntegrity(), node.getInheritedIntegrity()));
                node.setInheritedAvailability(Math.max(child.getAvailability(), node.getInheritedAvailability()));
            }else {
                node.setInheritedConfidentiality(Math.max(child.getInheritedConfidentiality(), node.getInheritedConfidentiality()));
                node.setInheritedIntegrity(Math.max(child.getInheritedIntegrity(), node.getInheritedIntegrity()));
                node.setInheritedAvailability(Math.max(child.getInheritedAvailability(), node.getInheritedAvailability()));
            }
        });
    }

    /**
     * Finds an AssetNode by its asset ID in the given list of AssetNodes.
     *
     * @param nodes   the list of AssetNodes to search
     * @param assetId the ID of the asset to find
     * @return the AssetNode with the given asset ID, or null if not found
     */
    public static AssetNode findAssetNodeByAsset(List<AssetNode> nodes, int assetId) {
        for (AssetNode node : nodes) {
            if (node.getAsset().getId() == assetId) {
                return node;
            }
        }
        return null;
    }

    /**
     * Finds an AssetNode by its asset in the given list of AssetNodes.
     *
     * @param nodes the list of AssetNodes to search
     * @param asset the asset to find
     * @return the AssetNode with the given asset, or null if not found
     */
    public static AssetNode findAssetNodeByAsset(List<AssetNode> nodes, Asset asset) {
        if (asset.getId() > 0)
            return findAssetNodeByAsset(nodes, asset.getId());
        for (AssetNode node : nodes) {
            if (node.getAsset().equals(asset)) {
                return node;
            }
        }
        return null;
    }

    /**
     * Checks if there is a cycle in the given list of AssetNodes.
     *
     * @param nodes the list of AssetNodes to check for cycles
     * @return true if a cycle is found, false otherwise
     */
    public static boolean isCyclic(List<AssetNode> nodes) {
        for (AssetNode node : nodes) {
            if (isCyclic(node, new HashSet<>())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a list of root nodes from the given list of nodes.
     *
     * @param nodes the list of nodes to process
     * @return a list of root nodes
     */
    public static List<AssetNode> getRoots(List<AssetNode> nodes) {
        final Set<AssetNode> children = new HashSet<>();
        for (AssetNode node : nodes) {
            for (AssetEdge edge : node.getEdges().values()) {
                children.add(edge.getChild());
            }
        }
        return nodes.stream().filter(e-> !children.contains(e)).toList();
    }

    /**
     * Checks if any of the given asset nodes have dependencies.
     *
     * @param nodes the list of asset nodes to check
     * @return true if any of the asset nodes have dependencies, false otherwise
     */
    public static boolean hasDependancy(List<AssetNode> nodes) {
        return nodes.stream().anyMatch(node -> node.getEdges().size() > 0);
    }

    /**
     * Checks if an AssetNode belongs to a list of AssetNodes or its children.
     *
     * @param node  the AssetNode to check
     * @param nodes the list of AssetNodes to check against
     * @return true if the AssetNode belongs to the list, false otherwise
     */
    public static boolean isBelongTo(AssetNode node, List<AssetNode> nodes) {
        return nodes.stream().anyMatch(parent -> parent.equals(node) || hasLink(parent, node));
    }

    /**
     * Checks if a given parent node has a link to a child node.
     *
     * @param parent The parent node to check.
     * @param child  The child node to check for a link.
     * @return true if the parent node has a link to the child node, false
     *         otherwise.
     */
    public static boolean hasLink(AssetNode parent, AssetNode child) {
        return parent.getEdges().values().stream().anyMatch(edge -> edge.getChild().equals(child));
    }

    /**
     * Checks if there is a cycle in the graph starting from the given node.
     *
     * @param node    the starting node to check for cycles
     * @param visited a set of visited nodes to keep track of visited nodes during
     *                the traversal
     * @return true if a cycle is found, false otherwise
     */
    private static boolean isCyclic(AssetNode node, Set<AssetNode> visited) {
        if (visited.contains(node)) {
            return true;
        }
        visited.add(node);
        for (AssetEdge edge : node.getEdges().values()) {
            if (isCyclic(edge.getChild(), visited)) {
                return true;
            }
        }
        visited.remove(node);
        return false;
    }

}
