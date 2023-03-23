package uk.ac.tees.tvshowapp.tmdb.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

public class ProductionCompany implements Serializable {

    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("headquarters")
    @Expose
    private String headquarters;
    @SerializedName("homepage")
    @Expose
    private String homepage;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("logo_path")
    @Expose
    private String logoPath;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("origin_country")
    @Expose
    private String originCountry;
    @SerializedName("parent_company")
    @Expose
    private String parentCompany;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHeadquarters() {
        return headquarters;
    }

    public void setHeadquarters(String headquarters) {
        this.headquarters = headquarters;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriginCountry() {
        return originCountry;
    }

    public void setOriginCountry(String originCountry) {
        this.originCountry = originCountry;
    }

    public String getParentCompany() {
        return parentCompany;
    }

    public void setParentCompany(String parentCompany) {
        this.parentCompany = parentCompany;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductionCompany that = (ProductionCompany) o;
        return Objects.equals(description, that.description) &&
                Objects.equals(headquarters, that.headquarters) &&
                Objects.equals(homepage, that.homepage) &&
                Objects.equals(id, that.id) &&
                Objects.equals(logoPath, that.logoPath) &&
                Objects.equals(name, that.name) &&
                Objects.equals(originCountry, that.originCountry) &&
                Objects.equals(parentCompany, that.parentCompany);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, headquarters, homepage, id, logoPath, name, originCountry, parentCompany);
    }
}