package eu.slicky.pomesljajcek.data.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(
    tableName = "EntryEntity",
    foreignKeys = [ForeignKey(
        entity = SectionEntity::class,
        parentColumns = ["id"],
        childColumns = ["sectionId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class EntryEntity(
        @PrimaryKey(autoGenerate = true)
        var id: Long = 0,
        val sectionId: Long,
        val character: String,
        val pinyin: String,
        val hasTraditional: Boolean
) : Parcelable