package lu.itrust.business.ts.model.general.document.impl;

import java.sql.Timestamp;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lu.itrust.business.ts.model.general.document.Document;

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

    public SimpleDocument(SimpleDocument data) {
        this(data.type, data.getName(), data.getLength(), data.getData().clone(), data.getCreated());
    }

    public SimpleDocumentType getType() {
        return type;
    }

    public void setType(SimpleDocumentType type) {
        this.type = type;
    }

}
