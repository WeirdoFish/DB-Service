package arvs.dbreq;

import java.io.Serializable;

public class DBNote implements Serializable {

    private String password;
    private String name;
    private String id;
    private String title;
    private String text;
    private Boolean isFailed;

    public Boolean getIsFailed() {
        return isFailed;
    }

    public void setIsFailed(Boolean isFailed) {
        this.isFailed = isFailed;
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
