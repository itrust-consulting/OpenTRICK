package lu.itrust.business.ts.model.general.document.impl;

import java.sql.Timestamp;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lu.itrust.business.ts.model.general.document.Document;


/**
 * Represents an attachment document.
 * This class extends the Document class and provides additional functionality for attachments.
 */
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "idAttachment"))
public class Attachment extends Document {

    public Attachment() {
    }

    public Attachment(String name, long length, byte[] data) {
        super(name, length, data);
    }

    public Attachment(String name, long length, byte[] data, Timestamp created) {
        super(name, length, data, created);
    }
}
