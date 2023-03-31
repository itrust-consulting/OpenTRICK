package lu.itrust.business.ts.model.parameter.impl;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lu.itrust.business.ts.model.parameter.IIlrSoaScaleParameter;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "description", column = @Column(name = "dtDescription", length = 255, nullable = false)) 
public class IlrSoaScaleParameter extends ColoredParameter implements IIlrSoaScaleParameter {

    public IlrSoaScaleParameter() {
    }

    public IlrSoaScaleParameter(double value, String color, String description) {
        super(value, color, description);
    }
    
}
