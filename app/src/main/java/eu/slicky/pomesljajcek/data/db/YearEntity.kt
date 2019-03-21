package eu.slicky.pomesljajcek.data.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(
    tableName = "YearEntity"
)
data class YearEntity(
        @PrimaryKey(autoGenerate = true)
        var id: Long = 0,
//        val yearNumber: Int
        val name: String
) : Parcelable