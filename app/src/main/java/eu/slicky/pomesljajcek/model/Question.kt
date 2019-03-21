package eu.slicky.pomesljajcek.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Question(
    val value: String,
    val answer: String,
    val hasTraditional: Boolean,
    var answered: Boolean = false
) : Parcelable