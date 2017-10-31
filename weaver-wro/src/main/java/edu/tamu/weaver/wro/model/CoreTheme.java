package edu.tamu.weaver.wro.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;
import static org.hibernate.annotations.FetchMode.SELECT;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Fetch;

import edu.tamu.weaver.data.model.BaseEntity;

@Entity
public class CoreTheme extends BaseEntity {

    @Column
    private String name;

    @Column
    private Boolean active = false;

    @Fetch(SELECT)
    @OneToMany(mappedBy = "theme", fetch = EAGER, cascade = { DETACH, MERGE, REFRESH })
    private Set<ThemeProperty> themeProperties = new HashSet<ThemeProperty>();

    public CoreTheme() {

    }

    public CoreTheme(String name) {
        this();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Set<ThemeProperty> getThemeProperties() {
        return themeProperties;
    }

    public void setThemeProperties(Set<ThemeProperty> themeProperties) {
        this.themeProperties = themeProperties;
    }

    public void addThemeProperty(ThemeProperty themeProperty) {
        themeProperties.add(themeProperty);
    }

    public void removeThemeProperty(ThemeProperty themeProperty) {
        themeProperties.remove(themeProperty);
    }

    public void clearThemeProperties() {
        themeProperties.clear();
    }

}
