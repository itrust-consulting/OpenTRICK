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


/**
 * Represents a recipient of an email.
 * 
 * This class is used to store information about a recipient, including their email address and recipient type.
 * 
 * The recipient's email address can be accessed using the getEmail() method, and can be updated using the setEmail() method.
 * The recipient's type can be accessed using the getType() method, and can be updated using the setType() method.
 * 
 * Example usage:
 * Recipient recipient = new Recipient("john@example.com", RecipientType.TO);
 * String email = recipient.getEmail();
 * RecipientType type = recipient.getType();
 * 
 * Note: The Recipient class is annotated with @Cacheable and @Cache annotations to enable caching of recipient objects.
 */
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

    /**
     * Returns the ID of the recipient.
     *
     * @return the ID of the recipient
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the ID of the recipient.
     *
     * @param id the ID to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Returns the email address of the recipient.
     *
     * @return the email address as a String
     */
    public String getEmail() {
        return email;
    }

    /**
        * Sets the email address of the recipient.
        *
        * @param email the email address to set
        */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Represents the type of recipient for an email.
     */
    public RecipientType getType() {
        return type;
    }

    /**
     * Sets the type of the recipient.
     *
     * @param type the type of the recipient
     */
    public void setType(RecipientType type) {
        this.type = type;
    }

}
