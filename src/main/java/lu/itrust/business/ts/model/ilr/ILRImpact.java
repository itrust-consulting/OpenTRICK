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

/**
 * Represents an ILR (Impact Level Rating) impact.
 * An ILRImpact object contains information about the type and value of the impact.
 */
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

    /**
     * Default constructor for ILRImpact.
     */
    public ILRImpact() {
    }

    /**
     * Constructor for ILRImpact with a given type.
     * 
     * @param type The type of the impact.
     */
    public ILRImpact(ScaleType type) {
        this(type, -1);
    }

    /**
     * Constructor for ILRImpact with a given type and value.
     * 
     * @param type The type of the impact.
     * @param value The value of the impact.
     */
    public ILRImpact(ScaleType type, int value) {
        setType(type);
        setValue(value);
    }

    /**
     * Get the ID of the ILRImpact.
     * 
     * @return The ID of the ILRImpact.
     */
    public long getId() {
        return id;
    }

    /**
     * Set the ID of the ILRImpact.
     * 
     * @param id The ID to set.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get the type of the impact.
     * 
     * @return The type of the impact.
     */
    public ScaleType getType() {
        return type;
    }

    /**
     * Set the type of the impact.
     * 
     * @param type The type to set.
     */
    public void setType(ScaleType type) {
        this.type = type;
    }

    /**
     * Get the value of the impact.
     * 
     * @return The value of the impact.
     */
    public int getValue() {
        return value;
    }

    /**
     * Set the value of the impact.
     * 
     * @param value The value to set.
     */
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
     * Create a duplicate of the ILRImpact object.
     * The ID of the duplicate will be set to 0.
     * 
     * @return A duplicate of the ILRImpact object.
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
