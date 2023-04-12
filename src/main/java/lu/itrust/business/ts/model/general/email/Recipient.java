package lu.itrust.business.ts.model.general.email;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

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
