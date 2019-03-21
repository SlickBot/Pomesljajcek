package eu.slicky.pomesljajcek.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
        version = 2,
        exportSchema = false,
        entities = [
            YearEntity::class,
            SectionEntity::class,
            EntryEntity::class
        ]
)
abstract class DatabaseHelper : RoomDatabase() {

    abstract fun yearDAO(): YearDao

    abstract fun sectionDAO(): SectionDao

    abstract fun entryDAO(): EntryDao

    object MIGRATION_1_2 : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // rename old_entries -> EntriesEntity
            database.execSQL("CREATE TABLE EntryEntity_new (id INTEGER NOT NULL, sectionId INTEGER NOT NULL, character TEXT NOT NULL, pinyin TEXT NOT NULL, hasTraditional INTEGER NOT NULL, PRIMARY KEY(id), CONSTRAINT fk_sections FOREIGN KEY (sectionId) REFERENCES SectionEntity(id) ON DELETE CASCADE)")
            database.execSQL("INSERT INTO EntryEntity_new (id, sectionId, character, pinyin, hasTraditional) SELECT id, sectionId, character, pinyin, hasTraditional FROM old_entries")
            database.execSQL("DROP TABLE old_entries")
            database.execSQL("ALTER TABLE EntryEntity_new RENAME TO EntryEntity")
            // rename old_sections -> SectionEntity and change sectionNumber to name
            database.execSQL("CREATE TABLE SectionEntity (id INTEGER NOT NULL, yearId INTEGER NOT NULL, name TEXT NOT NULL, PRIMARY KEY(id), CONSTRAINT fk_years FOREIGN KEY (yearId) REFERENCES YearEntity(id) ON DELETE CASCADE)")
            database.execSQL("INSERT INTO SectionEntity (id, yearId, name) SELECT id, yearId, 'Section ' || sectionNumber AS name FROM old_sections")
            database.execSQL("DROP TABLE old_sections")
            // rename change yearNumber to name
            database.execSQL("CREATE TABLE YearEntity_new (id INTEGER NOT NULL, name TEXT NOT NULL, PRIMARY KEY(id))")
            database.execSQL("INSERT INTO YearEntity_new (id, name) SELECT id, 'Year ' || yearNumber AS name FROM YearEntity")
            database.execSQL("DROP TABLE YearEntity")
            database.execSQL("ALTER TABLE YearEntity_new RENAME TO YearEntity")
        }
    }
    
}