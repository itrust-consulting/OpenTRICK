package lu.itrust.business.ts.model.ilr;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.ts.exception.TrickException;

/**
 * Represents an edge in the asset graph.
 * An edge connects two asset nodes and holds information about the weight of the connection.
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AssetEdge implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idAssetEdge")
    private long id;

    @Column(name = "dtWeight")
    private double weight;

    @Transient
    private boolean marked;

    @ManyToOne
    @JoinColumn(name = "fiParent")
    @Cascade(CascadeType.SAVE_UPDATE)
    private AssetNode parent;

    @ManyToOne
    @JoinColumn(name = "fiChild")
    @Cascade(CascadeType.SAVE_UPDATE)
    private AssetNode child;

    public AssetEdge(){
    }

    public AssetEdge(AssetNode parent, AssetNode child) {
        this(parent, child, 1d);
    }

    public AssetEdge(AssetNode parent, AssetNode child, double weight) {
        this.weight = weight;
        this.parent = parent;
        this.child = child;
    }

    /**
     * Returns the ID of the AssetEdge.
     *
     * @return the ID of the AssetEdge
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the ID of the AssetEdge.
     *
     * @param id the ID to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Returns the weight of the asset edge.
     *
     * @return the weight of the asset edge
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Sets the weight of the asset edge.
     *
     * @param weight the weight to set
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * Returns whether the asset edge is marked.
     *
     * @return true if the asset edge is marked, false otherwise
     */
    public boolean isMarked() {
        return marked;
    }

    /**
     * Sets whether the asset edge is marked.
     *
     * @param marked true if the asset edge is marked, false otherwise
     */
    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    /**
     * Returns the parent AssetNode of the asset edge.
     *
     * @return the parent AssetNode of the asset edge
     */
    public AssetNode getParent() {
        return parent;
    }

    /**
     * Sets the parent AssetNode of the asset edge.
     *
     * @param parent the parent AssetNode to set
     */
    public void setParent(AssetNode parent) {
        this.parent = parent;
    }

    /**
     * Returns the child AssetNode of the asset edge.
     *
     * @return the child AssetNode of the asset edge
     */
    public AssetNode getChild() {
        return child;
    }

    /**
     * Sets the child AssetNode of the asset edge.
     *
     * @param child the child AssetNode to set
     */
    public void setChild(AssetNode child) {
        this.child = child;
    }

    /**
     * Creates and returns a copy of the AssetEdge object.
     *
     * @return a copy of the AssetEdge object
     */
    @Override
    public AssetEdge clone() {
        try {
            return (AssetEdge) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new TrickException("error.clone.assetedge", "AssetEdge cannot be copied");
        }
    }

    /**
     * Creates and returns a copy of the AssetEdge object with the specified parent.
     *
     * @param parent the parent AssetNode for the copied AssetEdge
     * @return a copy of the AssetEdge object with the specified parent
     */
    public AssetEdge clone(AssetNode parent) {
        final AssetEdge edge = clone();
        edge.parent = parent;
        return edge;
    }

    /**
     * Creates and returns a copy of the AssetEdge object with the specified parent and child.
     *
     * @param parent the parent AssetNode for the copied AssetEdge
     * @param child the child AssetNode for the copied AssetEdge
     * @return a copy of the AssetEdge object with the specified parent and child
     */
    public AssetEdge clone(AssetNode parent, AssetNode child) {
        final AssetEdge edge = clone(parent);
        edge.child = child;
        return edge;
    }

    /**
     * Creates and returns a duplicate of the AssetEdge object with a new ID.
     *
     * @return a duplicate of the AssetEdge object with a new ID
     */
    public AssetEdge duplicate() {
        try {
            final AssetEdge edge = (AssetEdge) super.clone();
            edge.id = 0;
            return edge;
        } catch (CloneNotSupportedException e) {
            throw new TrickException("error.clone.assetedge", "AssetEdge cannot be copied");
        }
    }

    /**
     * Creates and returns a duplicate of the AssetEdge object with a new ID and the specified parent.
     *
     * @param parent the parent AssetNode for the duplicated AssetEdge
     * @return a duplicate of the AssetEdge object with a new ID and the specified parent
     */
    public AssetEdge duplicate(AssetNode parent) {
        final AssetEdge edge = duplicate();
        edge.parent = parent;
        return edge;
    }

    /**
     * Creates and returns a duplicate of the AssetEdge object with a new ID, the specified parent, and child.
     *
     * @param parent the parent AssetNode for the duplicated AssetEdge
     * @param child the child AssetNode for the duplicated AssetEdge
     * @return a duplicate of the AssetEdge object with a new ID, the specified parent, and child
     */
    public AssetEdge duplicate(AssetNode parent, AssetNode child) {
        final AssetEdge edge = duplicate(parent);
        edge.child = child;
        return edge;
    }

}
