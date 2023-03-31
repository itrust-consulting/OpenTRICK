package lu.itrust.business.ts.model.general;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lu.itrust.business.ts.model.general.helper.EmailTemplateForm;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class EmailTemplate {

    @Id
    @Column(name = "idEmailTemplate")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "dtEmail", nullable = false)
    private String email;

    @Column(name = "dtTitle", nullable = false)
    private String title;

    @Column(name = "dtTemplate", nullable = false)
    private String template;

    @Column(name = "dtHtml", nullable = false)
    private boolean html = true;

    @Column(name = "dtInternalTime", nullable = false)
    private long internalTime = 1000;

    public EmailTemplate() {
    }

    public EmailTemplate(String email, String title, String template, boolean html) {
        this.email = email;
        this.html = html;
        this.title = title;
        this.template = template;
    }

    public EmailTemplate(EmailTemplate emailTemplate) {
        update(emailTemplate);
    }

    public EmailTemplate(EmailTemplateForm form) {
        update(form);
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

    public boolean isHtml() {
        return html;
    }

    public void setHtml(boolean html) {
        this.html = html;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public long getInternalTime() {
        return internalTime;
    }

    public void setInternalTime(long internalTime) {
        this.internalTime = internalTime;
    }

    public void update(EmailTemplate emailTemplate) {
        this.email = emailTemplate.email;
        this.html = emailTemplate.html;
        this.title = emailTemplate.title;
        this.internalTime = emailTemplate.internalTime;
        this.template = emailTemplate.template;
    }

    public void update(EmailTemplateForm form) {
        this.email = form.getEmail();
        this.html = form.isHtml();
        this.title = form.getTitle();
        this.internalTime = form.getInternalTime();
        this.template = form.getTemplate();
    }

}
