package lu.itrust.business.TS.model.asset;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.TS.exception.TrickException;

/*@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "fiParent", "fiChild" }))*/
public class AssetEdge implements Cloneable {

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idAssetEdge")
    private long id = -1;

    @Column(name = "dtWeight", nullable = false)
    private double weight;

    @ManyToOne
	@JoinColumn(name = "fiParent", nullable = false)
	@Cascade(CascadeType.SAVE_UPDATE)
    private Asset parent;

    @ManyToOne
	@JoinColumn(name = "fiChild", nullable = false)
	@Cascade(CascadeType.SAVE_UPDATE)
    private Asset child;

    public AssetEdge() {
    }

    public AssetEdge(final double weight, final Asset parent, final Asset child) {
        this.weight = weight;
        this.parent = parent;
        this.child = child;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(final double weight) {
        this.weight = weight;
    }

    public Asset getParent() {
        return parent;
    }

    public void setParent(final Asset parent) {
        this.parent = parent;
    }

    public Asset getChild() {
        return child;
    }

    public void setChild(final Asset child) {
        this.child = child;
    }

    @Override
    public AssetEdge clone() {
        try {
            return (AssetEdge) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new TrickException("error.clone.asset_egde", "Asset egde cannot be copied");
        }
    }

}
