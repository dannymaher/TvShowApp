package uk.ac.tees.tvshowapp.database.dao;

import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

import java.util.List;

import uk.ac.tees.tvshowapp.database.dto.GenreDto;

/**
 * base data access object containing methods used by all others
 * @param <T> the type of dto to use
 */
public abstract class BaseDao<T> {

    /**
     * insert an item
     * @param dto the item to insert
     * @return -1 on failure
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long insert(T dto);

    /**
     * update an item with the same primary key
     * @param dto the item to update
     */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    public abstract void update(T dto);

    /**
     * insert an item, or update it if there is already a matching item
     * @param dto the item to insert / update
     */
    public void put(T dto) {
        long id = insert(dto);
        if (id == -1) {
            update(dto);
        }
    }

    /**
     * insert multiple items, replace items on conflict.
     * WARNING: this will trigger a delete and any cascade deletes
     * @param dtos the items to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void put(List<T> dtos);

}
