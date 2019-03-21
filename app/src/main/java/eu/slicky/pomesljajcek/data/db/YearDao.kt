package eu.slicky.pomesljajcek.data.db

import androidx.room.*

@Dao
interface YearDao {

    @Query("SELECT * FROM YearEntity")
    fun getAll(): List<YearEntity>

    @Query("SELECT * FROM YearEntity WHERE id = :id")
    fun get(id: Long): YearEntity

    @Insert
    fun insert(entity: YearEntity): Long

    @Update
    fun update(entity: YearEntity)

    @Update
    fun update(entities: List<YearEntity>)

    @Delete
    fun delete(entity: YearEntity)

    @Query("DELETE FROM YearEntity")
    fun deleteAll()

}