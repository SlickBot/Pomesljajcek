package eu.slicky.pomesljajcek.data.db

import androidx.room.*

@Dao
interface EntryDao {

    @Query("SELECT * FROM EntryEntity")
    fun getAll(): List<EntryEntity>

    @Query("SELECT * FROM EntryEntity WHERE sectionId = :sectionId")
    fun getAllFor(sectionId: Long): List<EntryEntity>

    @Query("SELECT * FROM EntryEntity WHERE id = :id")
    fun get(id: Long): EntryEntity

    @Insert
    fun insert(entity: EntryEntity): Long

    @Update
    fun update(entity: EntryEntity)

    @Update
    fun update(entities: List<EntryEntity>)

    @Delete
    fun delete(entity: EntryEntity)

    @Query("DELETE FROM EntryEntity")
    fun deleteAll()

}
