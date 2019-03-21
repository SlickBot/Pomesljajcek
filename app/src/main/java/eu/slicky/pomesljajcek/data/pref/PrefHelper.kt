package eu.slicky.pomesljajcek.data.pref

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import eu.slicky.pomesljajcek.data.pref.model.ChallengeCheckedIds
import eu.slicky.pomesljajcek.data.pref.model.EntityOrder
import eu.slicky.pomesljajcek.data.pref.model.SectionOrder
import eu.slicky.pomesljajcek.data.pref.model.YearOrder

class PrefHelper(context: Context, name: String) {

    private val preferences: SharedPreferences by lazy(this) { createPreferences(context, name) }
    private val gson = Gson()

    private fun createPreferences(context: Context, name: String): SharedPreferences {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    fun getYearOrder(): YearOrder? {
        return gson.fromJson(preferences.getString("year_order", null), YearOrder::class.java)
    }

    fun saveYearOrder(order: YearOrder) {
        preferences.edit().putString("year_order", gson.toJson(order)).apply()
    }

    fun clearYearOrder() {
        preferences.edit().remove("year_order").apply()
    }

    fun getSectionOrder(yearId: Long): SectionOrder? {
        return gson.fromJson(preferences.getString("section_order_$yearId", null), SectionOrder::class.java)
    }

    fun saveSectionOrder(yearId: Long, order: SectionOrder) {
        preferences.edit().putString("section_order_$yearId", gson.toJson(order)).apply()
    }

    fun clearSectionOrder(yearId: Long) {
        preferences.edit().remove("section_order_$yearId").apply()
    }


    fun getEntryOrder(sectionId: Long): EntityOrder? {
        return gson.fromJson(preferences.getString("entity_order_$sectionId", null), EntityOrder::class.java)
    }

    fun saveEntryOrder(sectionId: Long, order: EntityOrder) {
        preferences.edit().putString("entity_order_$sectionId", gson.toJson(order)).apply()
    }

    fun clearEntryOrder(sectionId: Long) {
        preferences.edit().remove("entity_order_$sectionId").apply()
    }


    fun getChallengeCheckedIds(yearId: Long): ChallengeCheckedIds? {
        return gson.fromJson(preferences.getString("challenge_checked_$yearId", null), ChallengeCheckedIds::class.java)
    }

    fun saveChallengeCheckedIds(yearId: Long, checkedIds: ChallengeCheckedIds) {
        preferences.edit().putString("challenge_checked_$yearId", gson.toJson(checkedIds)).apply()
    }

    fun clearChallengeCheckedIds(yearId: Long) {
        preferences.edit().remove("challenge_checked_$yearId").apply()
    }

    fun shouldShowPPDialog(): Boolean {
        return preferences.getBoolean("show_pp", true)
    }

    fun setShouldShowPPDialog(show: Boolean) {
        preferences.edit().putBoolean("show_pp", show).apply()
    }

}
