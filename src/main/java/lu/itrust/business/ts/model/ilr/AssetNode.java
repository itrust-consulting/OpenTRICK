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

/*****
 * 
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
    @OneToMany(mappedBy = "parent")
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

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public Asset getAsset() {
        return impact == null ? null : impact.getAsset();
    }

    public AssetImpact getImpact() {
        return impact;
    }

    public void setImpact(final AssetImpact impact) {
        this.impact = impact;
    }

    public int getInheritedConfidentiality() {
        return inheritedConfidentiality;
    }

    public void setInheritedConfidentiality(final int inheritedConfidentiality) {
        this.inheritedConfidentiality = inheritedConfidentiality;
    }

    public int getInheritedIntegrity() {
        return inheritedIntegrity;
    }

    public void setInheritedIntegrity(final int inheritedIntegrity) {
        this.inheritedIntegrity = inheritedIntegrity;
    }

    public int getInheritedAvailability() {
        return inheritedAvailability;
    }

    public void setInheritedAvailability(final int inheritedAvailability) {
        this.inheritedAvailability = inheritedAvailability;
    }

    public Map<AssetNode, AssetEdge> getEdges() {
        return edges;
    }

    public void setEdges(final Map<AssetNode, AssetEdge> edges) {
        this.edges = edges;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public boolean isLeaf() {
        return edges == null || edges.isEmpty();
    }

    public int getConfidentiality() {
        if (impact == null || impact.getConfidentialityImpacts() == null
                || impact.getConfidentialityImpacts().isEmpty())
            return inheritedConfidentiality < 0 ? -1
                    : inheritedConfidentiality;
        return impact.getConfidentialityImpacts().values().stream().mapToInt(ILRImpact::getValue).reduce(Integer::max)
                .orElse(-1);
    }

    public int getAvailability() {
        if (impact == null || impact.getAvailabilityImpacts() == null
                || impact.getAvailabilityImpacts().isEmpty())
            return inheritedAvailability < 0 ? -1
                    : inheritedAvailability;
        return impact.getAvailabilityImpacts().values().stream().mapToInt(ILRImpact::getValue).reduce(Integer::max)
                .orElse(-1);
    }

    public int getIntegrity() {
        if (impact == null || impact.getIntegrityImpacts() == null
                || impact.getIntegrityImpacts().isEmpty())
            return inheritedIntegrity < 0 ? -1
                    : inheritedIntegrity;
        return impact.getIntegrityImpacts().values().stream().mapToInt(ILRImpact::getValue).reduce(Integer::max)
                .orElse(-1);
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

    public AssetNode clone(AssetImpact impact) {
        final AssetNode assetNode = clone();
        if (assetNode.impact != null)
            assetNode.impact = impact;
        return assetNode;

    }

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

    public AssetNode duplicate(Asset asset) {
        AssetNode assetNode = duplicate();
        if (assetNode.impact != null)
            assetNode.impact = impact.duplicate(asset);
        return assetNode;
    }

    public AssetNode duplicate(AssetImpact impact) {
        AssetNode assetNode = duplicate();
        if (assetNode.impact != null)
            assetNode.impact = impact;
        return assetNode;
    }

}
