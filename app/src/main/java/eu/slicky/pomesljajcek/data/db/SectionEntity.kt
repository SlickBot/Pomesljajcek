package eu.slicky.pomesljajcek.data.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(
    tableName = "SectionEntity",
    foreignKeys = [ForeignKey(
        entity = YearEntity::class,
        parentColumns = ["id"],
        childColumns = ["yearId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class SectionEntity(
        @PrimaryKey(autoGenerate = true)
        var id: Long = 0,
        val yearId: Long,
//        val sectionNumber: Int
        val name: String
) : Parcelable