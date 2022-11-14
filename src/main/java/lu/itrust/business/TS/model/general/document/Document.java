package lu.itrust.business.TS.model.general.document;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

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
        this.created = created;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    

    
    
}
