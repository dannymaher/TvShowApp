package uk.ac.tees.tvshowapp.database.dao;

import androidx.room.Dao;

import uk.ac.tees.tvshowapp.database.dto.ProductionCountryDto;

/**
 * dao for accessing the production country information from the database
 */
@Dao
public abstract class ProductionCountryDao extends BaseDao<ProductionCountryDto> {
}
