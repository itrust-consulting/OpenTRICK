package lu.itrust.business.TS.model.general.document.impl;

import java.sql.Timestamp;

import javax.persistence.AttributeOverride;
import javax.persistence.Cacheable;
import javax.persistence.Column;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lu.itrust.business.TS.model.general.document.Document;

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
