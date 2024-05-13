package lu.itrust.business.ts.model.parameter.impl;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lu.itrust.business.ts.model.parameter.IIlrSoaScaleParameter;

/**
 * Represents a scale parameter for the IlrSoaScaleParameter class.
 * This class extends the ColoredParameter class and implements the IIlrSoaScaleParameter interface.
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "description", column = @Column(name = "dtDescription", length = 255, nullable = false)) 
public class IlrSoaScaleParameter extends ColoredParameter implements IIlrSoaScaleParameter {

    /**
     * Default constructor for the IlrSoaScaleParameter class.
     */
    public IlrSoaScaleParameter() {
    }

    /**
     * Constructor for the IlrSoaScaleParameter class with specified values.
     * 
     * @param value       The value of the scale parameter.
     * @param color       The color associated with the scale parameter.
     * @param description The description of the scale parameter.
     */
    public IlrSoaScaleParameter(double value, String color, String description) {
        super(value, color, description);
    }
    
}
