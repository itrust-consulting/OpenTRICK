package lu.itrust.business.TS.model.parameter.impl;

import javax.persistence.AttributeOverride;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lu.itrust.business.TS.model.parameter.IIlrSoaScaleParameter;

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
