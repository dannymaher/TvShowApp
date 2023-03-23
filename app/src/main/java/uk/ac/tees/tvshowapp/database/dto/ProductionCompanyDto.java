package uk.ac.tees.tvshowapp.database.dto;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * dto used to represent production companies
 */
@Entity
public class ProductionCompanyDto {

    @PrimaryKey
    public int productionCompanyId;
    public String description;
    public String headquarters;
    public String homepage;
    public String logoPath;
    public String name;
    public String originCountry;
    public String parentCompany;

}
