package lu.itrust.business.ts.model.general.document;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/**
 * Represents a generic document.
 */
@MappedSuperclass
public abstract class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idDocument")
    private long id;

    @Column(name = "dtName")
    private String name;

    @Column(name = "dtLength")
    private long length;

    @Column(name = "dtData", length = 16777216)
    private byte[] data;

    @Column(name = "dtCreated")
    private Timestamp created;

    protected Document() {
    }

    protected Document(String name, long length, byte[] data) {
        this(name, length, data, new Timestamp(System.currentTimeMillis()));
    }

    protected Document(String name, long length, byte[] data, Timestamp created) {
        this.name = name;
        this.length = length;
        this.data = data;
        if (created == null)
            this.created = new Timestamp(System.currentTimeMillis());
        else
            this.created = created;
    }

    /**
     * Returns the ID of the document.
     *
     * @return the document ID
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the ID of the document.
     *
     * @param id the document ID
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Returns the name of the document.
     *
     * @return the document name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the document.
     *
     * @param name the document name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the length of the document.
     *
     * @return the document length
     */
    public long getLength() {
        return length;
    }

    /**
     * Sets the length of the document.
     *
     * @param length the document length
     */
    public void setLength(long length) {
        this.length = length;
    }

    /**
     * Returns the data of the document.
     *
     * @return the document data
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Sets the data of the document.
     *
     * @param data the document data
     */
    public void setData(byte[] data) {
        this.data = data;
    }

    /**
     * Returns the timestamp representing the creation time of this document.
     *
     * @return the creation timestamp
     */
    public Timestamp getCreated() {
        return created;
    }

    /**
     * Sets the creation timestamp of this document.
     *
     * @param created the creation timestamp
     */
    public void setCreated(Timestamp created) {
        this.created = created;
    }

}
