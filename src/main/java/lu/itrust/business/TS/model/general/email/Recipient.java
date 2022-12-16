package lu.itrust.business.TS.model.general.email;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;


@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Recipient {

    @Id
    @Column(name = "idRecipient")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "dtEmail")
    private String email;

    @Column(name = "dtType")
    @Enumerated(EnumType.STRING)
    private RecipientType type;

    public Recipient() {
    }

    public Recipient(String email, RecipientType type) {
        this.email = email;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public RecipientType getType() {
        return type;
    }

    public void setType(RecipientType type) {
        this.type = type;
    }

}
