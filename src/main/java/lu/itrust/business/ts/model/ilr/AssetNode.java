package lu.itrust.business.ts.model.ilr;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKey;
import jakarta.persistence.OneToMany;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.asset.Asset;

/**
 * Represents a node in the asset hierarchy.
 * An AssetNode contains information about the asset, its impact, inherited
 * values, position, and edges to other nodes.
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AssetNode implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idAssetNode")
    private long id;

    /** Link to the asset */
    @ManyToOne
    @JoinColumn(name = "fiImpact", nullable = false, unique = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Cascade(CascadeType.ALL)
    private AssetImpact impact;

    /** Computed value from parent */
    @Column(name = "dtInheritedConfidentiality", nullable = false)
    private int inheritedConfidentiality;

    /** Computed value from parent */
    @Column(name = "dtInheritedIntegrity", nullable = false)
    private int inheritedIntegrity;

    /** Computed value from parent */
    @Column(name = "dtInheritedAvailability", nullable = false)
    private int inheritedAvailability;

    /** This node The heirs */
    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    @MapKey(name = "child")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Cascade(CascadeType.ALL)
    private Map<AssetNode, AssetEdge> edges = new HashMap<>();

    @Embedded
    @AttributeOverride(name = "x", column = @Column(name = "dtPositionX"))
    @AttributeOverride(name = "y", column = @Column(name = "dtPositionY"))
    private Position position;

    public AssetNode() {
    }

    public AssetNode(Asset asset) {
        this(new AssetImpact(asset));
    }

    public AssetNode(AssetImpact impact) {
        setImpact(impact);
    }

    /**
     * Returns the ID of the AssetNode.
     *
     * @return the ID of the AssetNode
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the ID of the AssetNode.
     *
     * @param id the ID to set
     */
    public void setId(final long id) {
        this.id = id;
    }

    /**
     * Returns the asset associated with this AssetNode.
     *
     * @return the asset associated with this AssetNode, or null if the impact is
     *         null.
     */
    public Asset getAsset() {
        return impact == null ? null : impact.getAsset();
    }

    /**
     * Represents the impact of an asset.
     */
    public AssetImpact getImpact() {
        return impact;
    }

    /**
     * Sets the impact of the asset.
     *
     * @param impact the impact to set
     */
    public void setImpact(final AssetImpact impact) {
        this.impact = impact;
    }

    /**
     * Returns the inherited confidentiality of the asset node.
     *
     * @return the inherited confidentiality value
     */
    public int getInheritedConfidentiality() {
        return inheritedConfidentiality;
    }

    /**
     * Sets the inherited confidentiality level for the asset node.
     *
     * @param inheritedConfidentiality the new value for the inherited
     *                                 confidentiality level
     */
    public void setInheritedConfidentiality(final int inheritedConfidentiality) {
        this.inheritedConfidentiality = inheritedConfidentiality;
    }

    /**
     * Returns the inherited integrity value of the AssetNode.
     *
     * @return the inherited integrity value
     */
    public int getInheritedIntegrity() {
        return inheritedIntegrity;
    }

    /**
     * Sets the inherited integrity value for this AssetNode.
     *
     * @param inheritedIntegrity the inherited integrity value to set
     */
    public void setInheritedIntegrity(final int inheritedIntegrity) {
        this.inheritedIntegrity = inheritedIntegrity;
    }

    /**
     * Returns the inherited availability of the asset node.
     *
     * @return the inherited availability value
     */
    public int getInheritedAvailability() {
        return inheritedAvailability;
    }

    /**
     * Sets the inherited availability of the AssetNode.
     *
     * @param inheritedAvailability the inherited availability value to be set
     */
    public void setInheritedAvailability(final int inheritedAvailability) {
        this.inheritedAvailability = inheritedAvailability;
    }

    /**
     * Returns a map of edges associated with this asset node.
     *
     * @return a map of edges, where the key is an AssetNode and the value is an
     *         AssetEdge
     */
    public Map<AssetNode, AssetEdge> getEdges() {
        return edges;
    }

    /**
     * Sets the edges of the AssetNode.
     *
     * @param edges a map containing the edges of the AssetNode
     */
    public void setEdges(final Map<AssetNode, AssetEdge> edges) {
        this.edges = edges;
    }

    /**
     * Returns the position of the asset node.
     *
     * @return the position of the asset node
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Sets the position of the asset node.
     *
     * @param position the new position of the asset node
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * Checks if the current asset node is a leaf node.
     * A leaf node is a node that has no outgoing edges.
     *
     * @return true if the asset node is a leaf node, false otherwise.
     */
    public boolean isLeaf() {
        return edges == null || edges.isEmpty();
    }

    /**
     * Returns the confidentiality level of the asset node.
     * If the impact or the confidentiality impacts are null or empty, it returns
     * the inherited confidentiality level.
     * Otherwise, it returns the maximum value of the confidentiality impacts.
     *
     * @return the confidentiality level of the asset node, or -1 if not available
     */
    public int getConfidentiality() {
        if (impact == null || impact.getConfidentialityImpacts() == null
                || impact.getConfidentialityImpacts().isEmpty())
            return inheritedConfidentiality < 0 ? -1
                    : inheritedConfidentiality;
        return getValue(impact.getConfidentialityImpacts().values().stream().mapToInt(ILRImpact::getValue).max()
                .orElse(-1), inheritedConfidentiality);
    }

    private int getValue(int value, int inheritedValue) {
        return value == -1 ? inheritedValue : value;
    }

    /**
     * Returns the availability of the AssetNode.
     * 
     * @return The availability value of the AssetNode. If the impact or
     *         availability impacts are null or empty,
     *         it returns the inherited availability value. Otherwise, it returns
     *         the maximum availability value
     *         from the impact's availability impacts.
     */
    public int getAvailability() {
        if (impact == null || impact.getAvailabilityImpacts() == null
                || impact.getAvailabilityImpacts().isEmpty())
            return inheritedAvailability < 0 ? -1
                    : inheritedAvailability;
        return getValue(impact.getAvailabilityImpacts().values().stream().mapToInt(ILRImpact::getValue).max()
                .orElse(-1), inheritedAvailability);
    }

    /**
     * Returns the integrity value of the asset node.
     * If the impact or integrity impacts are null or empty, the method returns the
     * inherited integrity value.
     * Otherwise, it returns the maximum integrity value among all the integrity
     * impacts.
     *
     * @return the integrity value of the asset node, or -1 if no integrity value is
     *         found
     */
    public int getIntegrity() {
        if (impact == null || impact.getIntegrityImpacts() == null
                || impact.getIntegrityImpacts().isEmpty())
            return inheritedIntegrity < 0 ? -1
                    : inheritedIntegrity;
        return getValue(impact.getIntegrityImpacts().values().stream().mapToInt(ILRImpact::getValue).max()
                .orElse(-1), inheritedIntegrity);
    }

    @Override
    public AssetNode clone() {
        try {
            AssetNode assetNode = (AssetNode) super.clone();
            if (assetNode.position != null)
                assetNode.position = new Position(position.getX(), position.getY());
            if (edges != null)
                assetNode.edges = edges.values().stream().map(AssetEdge::clone)
                        .collect(Collectors.toMap(AssetEdge::getChild, Function.identity()));
            return assetNode;
        } catch (CloneNotSupportedException e) {
            throw new TrickException("error.clone.asset_node", "AssetNode cannot be copied");
        }
    }

    public AssetNode clone(Asset asset) {
        final AssetNode assetNode = clone();
        if (assetNode.impact != null)
            assetNode.impact = impact.clone(asset);
        return assetNode;

    }

    /**
     * Creates a clone of the current AssetNode with the specified impact.
     *
     * @param impact The impact to be set for the cloned AssetNode.
     * @return A new AssetNode object with the specified impact.
     */
    public AssetNode clone(AssetImpact impact) {
        final AssetNode assetNode = clone();
        if (assetNode.impact != null)
            assetNode.impact = impact;
        return assetNode;

    }

    /**
     * Creates a duplicate of the current AssetNode object.
     * 
     * @return A new AssetNode object that is a copy of the current object.
     * @throws TrickException if cloning is not supported for AssetNode.
     */
    public AssetNode duplicate() {
        try {
            AssetNode assetNode = (AssetNode) super.clone();
            if (assetNode.position != null)
                assetNode.position = new Position(position.getX(), position.getY());
            if (edges != null)
                assetNode.edges = edges.values().stream().map(e -> e.duplicate(assetNode))
                        .collect(Collectors.toMap(AssetEdge::getChild, Function.identity()));
            assetNode.id = 0;
            return assetNode;
        } catch (CloneNotSupportedException e) {
            throw new TrickException("error.clone.asset_node", "AssetNode cannot be copied");
        }
    }

    /**
     * Creates a duplicate of the current AssetNode object.
     * 
     * @param asset The asset to be associated with the duplicated node.
     * @return A new AssetNode object that is a duplicate of the current node.
     */
    public AssetNode duplicate(Asset asset) {
        AssetNode assetNode = duplicate();
        if (assetNode.impact != null)
            assetNode.impact = impact.duplicate(asset);
        return assetNode;
    }

    /**
     * Creates a duplicate of the current AssetNode with the specified impact.
     * 
     * @param impact The impact to be set for the duplicate AssetNode.
     * @return A new AssetNode object that is a duplicate of the current AssetNode
     *         with the specified impact.
     */
    public AssetNode duplicate(AssetImpact impact) {
        AssetNode assetNode = duplicate();
        if (assetNode.impact != null)
            assetNode.impact = impact;
        return assetNode;
    }

}
