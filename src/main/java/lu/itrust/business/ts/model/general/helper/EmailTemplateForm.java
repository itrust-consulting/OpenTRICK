package lu.itrust.business.ts.model.general.helper;

public class EmailTemplateForm {
  
    private long id;

    private String email;

    private String title;

    private String template;

    private boolean html;

    private long internalTime;

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

    public boolean isHtml() {
        return html;
    }

    public void setHtml(boolean html) {
        this.html = html;
    }

    public long getInternalTime() {
        return internalTime;
    }

    public void setInternalTime(long internalTime) {
        this.internalTime = internalTime;
    }

    

}
