package arvs.database;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "notes")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Notes.findAll", query = "SELECT n FROM Notes n"),
    @NamedQuery(name = "Notes.findById", query = "SELECT n FROM Notes n WHERE n.id = :id"),
    @NamedQuery(name = "Notes.findByTime", query = "SELECT n FROM Notes n WHERE n.time = :time"),
    @NamedQuery(name = "Notes.findByTitle", query = "SELECT n FROM Notes n WHERE n.title = :title"),
    @NamedQuery(name = "Notes.findByText", query = "SELECT n FROM Notes n WHERE n.text = :text")})
public class Notes implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Basic(optional = false)
    @Column(name = "time")
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp time;

    @Column(name = "title")
    private String title;

    @Column(name = "text")
    private String text;

    @JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne(optional = false)
   // @PrimaryKeyJoinColumn
    private Owners owners;

    public Notes() {
    }

    public Notes(String title, String text, Owners owners) {
        setTitle(title);
        setText(text);
        setOwners(owners);
        Date datetime = new Date();
        setTime(new Timestamp(datetime.getTime()));
    }

    public Notes(String title, String text) {
        setTitle(title);
        setText(text);
        Date datetime = new Date();
        setTime(new Timestamp(datetime.getTime()));
    }

    public Notes(Integer id) {
        this.id = id;
    }

    public Notes(Integer id, Timestamp time) {
        this.id = id;
        this.time = time;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
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

    public Owners getOwners() {
        return owners;
    }

    public void setOwners(Owners owners) {
        this.owners = owners;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Notes)) {
            return false;
        }
        Notes other = (Notes) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "arvs.database.Notes[ id=" + id + " ]";
    }

}
