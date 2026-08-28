package edu.tamu.weaver.wro.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import edu.tamu.weaver.data.model.BaseEntity;

@Entity
public class ThemeProperty extends BaseEntity {

    @Column
    private String value;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = CoreTheme.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private CoreTheme theme;

    @OneToOne(fetch = FetchType.EAGER)
    private ThemePropertyName themePropertyName;

    public ThemeProperty() {
    }

    public ThemeProperty(ThemePropertyName themePropertyName, String value) {
        this();
        this.themePropertyName = themePropertyName;
        this.value = value;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the theme
     */
    public CoreTheme getTheme() {
        return theme;
    }

    /**
     * @param theme
     *            the theme to set
     */
    public void setTheme(CoreTheme theme) {
        this.theme = theme;
    }

    /**
     * @return the themePropertyName
     */
    public ThemePropertyName getThemePropertyName() {
        return themePropertyName;
    }

    /**
     * @param themePropertyName
     *            the themePropertyName to set
     */
    public void setThemePropertyName(ThemePropertyName themePropertyName) {
        this.themePropertyName = themePropertyName;
    }

}
