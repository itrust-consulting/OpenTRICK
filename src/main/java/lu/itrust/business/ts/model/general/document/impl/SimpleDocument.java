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

/**
 * Represents a simple document.
 * Extends the {@link Document} class and adds a type field.
 */
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

    /**
     * Constructs a new SimpleDocument object with the specified type, name, length, and data.
     *
     * @param type   the type of the document
     * @param name   the name of the document
     * @param length the length of the document in bytes
     * @param data   the data of the document as a byte array
     */
    public SimpleDocument(SimpleDocumentType type, String name, long length, byte[] data) {
        this(type, name, length, data, new Timestamp(System.currentTimeMillis()));
    }

    /**
     * Represents a simple document.
     */
    public SimpleDocument(SimpleDocumentType type, String name, long length, byte[] data, Timestamp created) {
        super(name, length, data, created);
        setType(type);
    }

    /**
     * Creates a new instance of the SimpleDocument class by copying the data from another SimpleDocument object.
     *
     * @param data The SimpleDocument object from which to copy the data.
     */
    public SimpleDocument(SimpleDocument data) {
        this(data.type, data.getName(), data.getLength(), data.getData().clone(), data.getCreated());
    }

    /**
     * Returns the type of the document.
     *
     * @return the type of the document
     */
    public SimpleDocumentType getType() {
        return type;
    }

    /**
     * Sets the type of the document.
     *
     * @param type the type of the document
     */
    public void setType(SimpleDocumentType type) {
        this.type = type;
    }

}
