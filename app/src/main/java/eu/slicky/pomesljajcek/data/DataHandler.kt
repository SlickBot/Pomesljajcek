package eu.slicky.pomesljajcek.data

import android.content.Context
import androidx.room.Room
import eu.slicky.pomesljajcek.data.db.DatabaseHelper
import eu.slicky.pomesljajcek.data.db.EntryEntity
import eu.slicky.pomesljajcek.data.db.SectionEntity
import eu.slicky.pomesljajcek.data.db.YearEntity
import eu.slicky.pomesljajcek.data.pref.PrefHelper
import eu.slicky.pomesljajcek.data.pref.model.ChallengeCheckedIds
import eu.slicky.pomesljajcek.data.pref.model.EntityOrder
import eu.slicky.pomesljajcek.data.pref.model.SectionOrder
import eu.slicky.pomesljajcek.data.pref.model.YearOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext


class DataHandler(context: Context) {

    private val packageName = context.packageName
    private val databaseName = "$packageName.database"
    private val preferencesName = "$packageName.preferences"

    private val database: DatabaseHelper by lazy(this) { createDatabase(context) }
    val pref: PrefHelper by lazy(this) { PrefHelper(context, preferencesName) }

    private fun createDatabase(context: Context): DatabaseHelper {
        return Room.databaseBuilder(context, DatabaseHelper::class.java, databaseName)
            .addMigrations(DatabaseHelper.MIGRATION_1_2)
            .build()
    }

    /*
     * Years
     */

    suspend fun getYears(ordered: Boolean = true): List<YearEntity> {
        return withContext(Dispatchers.IO) {
            val years = database.yearDAO().getAll()
            return@withContext if (ordered) zipYearsWithOrder(years) else years
        }
    }
    
    suspend fun getYear(yearId: Long): YearEntity {
        return withContext(Dispatchers.IO) {
            database.yearDAO().get(yearId)
        }
    }

    suspend fun zipYearsWithOrder(years: List<YearEntity>): List<YearEntity> {
        return withContext(Dispatchers.IO) {
            val order = pref.getYearOrder()
                ?: return@withContext years

            val zipped = order.yearIds.mapNotNull { id -> years.firstOrNull { it.id == id } }
            val rest = years.filter { it !in zipped }
            return@withContext zipped + rest
        }
    }

    suspend fun saveYearOrder(items: List<YearEntity>) {
        withContext(Dispatchers.IO) {
            pref.saveYearOrder(YearOrder(items.map { it.id }))
        }
    }

    suspend fun addYear(year: YearEntity): YearEntity {
        return withContext(Dispatchers.IO) {
            val newId = database.yearDAO().insert(year)
            database.yearDAO().get(newId)
        }
    }

    suspend fun addYear(yearName: String): YearEntity {
        return withContext(Dispatchers.IO) {
            val year = YearEntity(name = yearName)
            addYear(year)
        }
    }

    suspend fun updateYear(year: YearEntity): YearEntity {
        return withContext(Dispatchers.IO) {
            database.yearDAO().update(year)
            database.yearDAO().get(year.id)
        }
    }

    suspend fun deleteYear(year: YearEntity) {
        return withContext(Dispatchers.IO) {
            database.yearDAO().delete(year)
            pref.clearSectionOrder(year.id)
        }
    }


    /*
     * Sections
     */

    suspend fun getSections(year: YearEntity, ordered: Boolean = true): List<SectionEntity> {
        return withContext(Dispatchers.IO) {
            val sections = database.sectionDAO().getAllFor(year.id)
            return@withContext if (ordered) zipSectionsWithOrder(year, sections) else sections
        }
    }

    suspend fun zipSectionsWithOrder(year: YearEntity, sections: List<SectionEntity>): List<SectionEntity> {
        return withContext(Dispatchers.IO) {
            val order = pref.getSectionOrder(year.id)
                ?: return@withContext sections

            val zipped = order.sectionIds.mapNotNull { id -> sections.firstOrNull { it.id == id } }
            val rest = sections.filter { it !in zipped }
            return@withContext zipped + rest
        }
    }

    suspend fun saveSectionOrder(year: YearEntity, items: List<SectionEntity>) {
        withContext(Dispatchers.IO) {
            pref.saveSectionOrder(year.id, SectionOrder(items.map { it.id }))
        }
    }

    suspend fun getSection(sectionId: Long): SectionEntity {
        return withContext(Dispatchers.IO) {
            database.sectionDAO().get(sectionId)
        }
    }

    suspend fun addSection(section: SectionEntity): SectionEntity {
        return withContext(Dispatchers.IO) {
            val newId = database.sectionDAO().insert(section)
            database.sectionDAO().get(newId)
        }
    }

    suspend fun addSection(year: YearEntity, sectionName: String): SectionEntity {
        return withContext(Dispatchers.IO) {
            val section = SectionEntity(yearId = year.id, name = sectionName)
            addSection(section)
        }
    }

    suspend fun updateSection(section: SectionEntity): SectionEntity {
        return withContext(Dispatchers.IO) {
            database.sectionDAO().update(section)
            database.sectionDAO().get(section.id)
        }
    }

    suspend fun deleteSection(section: SectionEntity) {
        return withContext(Dispatchers.IO) {
            database.sectionDAO().delete(section)
            pref.clearEntryOrder(section.id)
        }
    }


    /*
     * Entries
     */

    suspend fun getEntries(section: SectionEntity, ordered: Boolean = true): List<EntryEntity> {
        return withContext(Dispatchers.IO) {
            val entries = database.entryDAO().getAllFor(section.id)
            return@withContext if (ordered) zipEntryWithOrder(section, entries) else entries
        }
    }

    suspend fun zipEntryWithOrder(section: SectionEntity, entities: List<EntryEntity>): List<EntryEntity> {
        return withContext(Dispatchers.IO) {
            val order = pref.getEntryOrder(section.id)
                ?: return@withContext entities

            val zipped = order.entityIds.mapNotNull { id -> entities.firstOrNull { it.id == id } }
            val rest = entities.filter { it !in zipped }
            zipped + rest
        }
    }

    suspend fun saveEntryOrder(section: SectionEntity, items: List<EntryEntity>) {
        withContext(Dispatchers.IO) {
            pref.saveEntryOrder(section.id, EntityOrder(items.map { it.id }))
        }
    }

    suspend fun getEntry(entryId: Long): EntryEntity {
        return withContext(Dispatchers.IO) {
            database.entryDAO().get(entryId)
        }
    }

    suspend fun addEntry(entry: EntryEntity): EntryEntity {
        return withContext(Dispatchers.IO) {
            val newId = database.entryDAO().insert(entry)
            database.entryDAO().get(newId)
        }
    }

    suspend fun addEntry(section: SectionEntity, character: String, pinyin: String, hasTraditional: Boolean): EntryEntity {
        return withContext(Dispatchers.IO) {
            val entry = EntryEntity(sectionId = section.id, character = character, pinyin = pinyin, hasTraditional = hasTraditional)
            addEntry(entry)
        }
    }

    suspend fun updateEntry(entry: EntryEntity): EntryEntity {
        return withContext(Dispatchers.IO) {
            database.entryDAO().update(entry)
            database.entryDAO().get(entry.id)
        }
    }

    suspend fun deleteEntry(entry: EntryEntity) {
        return withContext(Dispatchers.IO) {
            database.entryDAO().delete(entry)
        }
    }

    /* Challenge */

//    suspend fun getChallengeSections(year: YearEntity, ordered: Boolean = true): List<old_sections> {
//        return withContext(Dispatchers.IO) {
//            val sections = getSections(year, ordered)
//            return@withContext if (ordered) zipChallengeSectionsWithCheckedIds(year, sections) else sections
//        }
//    }

    suspend fun getChallengeData(year: YearEntity, sections: List<SectionEntity>): Pair<Array<String>, BooleanArray> {
        return withContext(Dispatchers.IO) {
            val checkedIds = pref.getChallengeCheckedIds(year.id)
            val texts = async { sections.map { it.name }.toTypedArray() }
            val checks = async { sections.map { it.id in checkedIds?.sectionIds ?: emptyList() }.toBooleanArray() }
            texts.await() to checks.await()
        }
    }

//    suspend fun zipChallengeSectionsWithCheckedIds(year: YearEntity, sections: List<old_sections>): List<old_sections> {
//        return withContext(Dispatchers.IO) {
//            val order = pref.getChallengeCheckedIds(year.id)
//                ?: return@withContext sections
//
//            val zipped = order.sectionIds.mapNotNull { id -> sections.firstOrNull { it.id == id } }
//            val rest = sections.filter { it !in zipped }
//            return@withContext zipped + rest
//        }
//    }

    suspend fun saveChallengeCheckedIds(year: YearEntity, checkedSectionIds: List<Long>) {
        withContext(Dispatchers.IO) {
            pref.saveChallengeCheckedIds(year.id,
                ChallengeCheckedIds(checkedSectionIds)
            )
        }
    }

}
