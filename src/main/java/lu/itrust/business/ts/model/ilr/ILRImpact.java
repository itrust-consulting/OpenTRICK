package lu.itrust.business.ts.model.ilr;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.scale.ScaleType;

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
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.MERGE, CascadeType.PERSIST})
    private ScaleType type;

    @Column(name = "dtValue")
    private int value = -1;

    public ILRImpact() {
    }

    public ILRImpact(ScaleType type) {
        this(type, -1);
    }

    public ILRImpact(ScaleType type, int value) {
        setType(type);
        setValue(value);
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
