package eu.slicky.pomesljajcek.data.db

import androidx.room.*

@Dao
interface SectionDao {

    @Query("SELECT * FROM SectionEntity")
    fun getAll(): List<SectionEntity>

    @Query("SELECT * FROM SectionEntity WHERE yearId = :yearId")
    fun getAllFor(yearId: Long): List<SectionEntity>

    @Query("SELECT * FROM SectionEntity WHERE id = :id")
    fun get(id: Long): SectionEntity

    @Insert
    fun insert(entity: SectionEntity): Long

    @Update
    fun update(entity: SectionEntity)

    @Update
    fun update(entities: List<SectionEntity>)

    @Delete
    fun delete(entity: SectionEntity)

    @Query("DELETE FROM SectionEntity")
    fun deleteAll()

}