package uk.ac.tees.tvshowapp.database.dao;

import androidx.room.Dao;

import uk.ac.tees.tvshowapp.database.dto.ProductionCompanyDto;

/**
 * dao for accessing the production company information from the database
 */
@Dao
public abstract class ProductionCompanyDao extends BaseDao<ProductionCompanyDto>{
}
