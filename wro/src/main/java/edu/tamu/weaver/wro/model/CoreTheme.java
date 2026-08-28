package edu.tamu.weaver.wro.model;

import static jakarta.persistence.CascadeType.DETACH;
import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.REFRESH;
import static jakarta.persistence.FetchType.EAGER;
import static org.hibernate.annotations.FetchMode.SELECT;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

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
