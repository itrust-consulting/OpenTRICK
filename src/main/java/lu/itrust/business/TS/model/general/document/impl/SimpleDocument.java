package lu.itrust.business.TS.model.general.document.impl;

import java.sql.Timestamp;

import javax.persistence.AttributeOverride;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lu.itrust.business.TS.model.general.document.Document;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "idSimpleDocument"))
public class SimpleDocument extends Document {

    @Column(name = "dtType", nullable = false)
    @Enumerated(EnumType.STRING)
    private SimpleDocumentType type;

    public SimpleDocument() {
    }

    public SimpleDocument(SimpleDocumentType type, String name, long length, byte[] data) {
        this(type, name, length, data, new Timestamp(System.currentTimeMillis()));
    }

    public SimpleDocument(SimpleDocumentType type, String name, long length, byte[] data, Timestamp created) {
        super(name, length, data, created);
        setType(type);
    }

    public SimpleDocumentType getType() {
        return type;
    }

    public void setType(SimpleDocumentType type) {
        this.type = type;
    }

}
