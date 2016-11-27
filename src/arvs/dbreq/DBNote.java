package arvs.dbreq;

import java.io.Serializable;

public class DBNote implements Serializable {

    private String name;
    private String title;
    private String text;
    private Boolean isFailed;

    public Boolean getIsFailed() {
        return isFailed;
    }

    public void setIsFailed(Boolean isFailed) {
        this.isFailed = isFailed;
    }
    
 

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
