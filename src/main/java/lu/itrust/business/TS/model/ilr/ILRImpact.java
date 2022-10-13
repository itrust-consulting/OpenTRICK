package lu.itrust.business.TS.model.ilr;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.scale.ScaleType;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ILRImpact implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idILRImpact")
    private long id;

    @ManyToOne
    @JoinColumn(name = "fiType", nullable = false)
    @Cascade(CascadeType.SAVE_UPDATE)
    private ScaleType type;

    @Column(name = "dtValue")
    private int value = -1;

    public ILRImpact() {
    }

    public ILRImpact(ScaleType type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ScaleType getType() {
        return type;
    }

    public void setType(ScaleType type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public ILRImpact clone() {
        try {
            return (ILRImpact) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new TrickException("error.clone.ilr_impact", "ILRImpact cannot be copied");
        }
    }

    /**
     * @return
     */
    public ILRImpact duplicate() {
        try {
            final ILRImpact impact = (ILRImpact) super.clone();
            impact.id = 0;
            return impact;
        } catch (CloneNotSupportedException e) {
            throw new TrickException("error.clone.ilr_impact", "ILRImpact cannot be copied");
        }
    }

}
