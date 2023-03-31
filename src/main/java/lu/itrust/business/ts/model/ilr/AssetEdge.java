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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public AssetNode getParent() {
        return parent;
    }

    public void setParent(AssetNode parent) {
        this.parent = parent;
    }

    public AssetNode getChild() {
        return child;
    }

    public void setChild(AssetNode child) {
        this.child = child;
    }

    @Override
    public AssetEdge clone() {
        try {
            return (AssetEdge) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new TrickException("error.clone.assetedge", "AssetEdge cannot be copied");
        }
    }

    public AssetEdge clone(AssetNode parent) {
        final AssetEdge edge = clone();
        edge.parent = parent;
        return edge;
    }

    public AssetEdge clone(AssetNode parent, AssetNode child) {
        final AssetEdge edge = clone(parent);
        edge.child = child;
        return edge;
    }

    public AssetEdge duplicate() {
        try {
            final AssetEdge edge = (AssetEdge) super.clone();
            edge.id = 0;
            return edge;
        } catch (CloneNotSupportedException e) {
            throw new TrickException("error.clone.assetedge", "AssetEdge cannot be copied");
        }
    }

    public AssetEdge duplicate(AssetNode parent) {
        final AssetEdge edge = duplicate();
        edge.parent = parent;
        return edge;
    }

    public AssetEdge duplicate(AssetNode parent, AssetNode child) {
        final AssetEdge edge = duplicate(parent);
        edge.child = child;
        return edge;
    }

}
