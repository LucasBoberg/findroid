package dev.jdtech.jellyfin.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.UUID

@Entity(tableName = "movies")
data class FindroidMovieDto(
    @PrimaryKey
    val id: UUID,
    val serverId: String?,
    val name: String,
    val originalTitle: String?,
    val overview: String,
    val played: Boolean,
    val favorite: Boolean,
    val runtimeTicks: Long,
    val playbackPositionTicks: Long,
    val premiereDate: LocalDateTime?,
    val communityRating: Float?,
    val officialRating: String?,
    val status: String,
    val productionYear: Int?,
    val endDate: LocalDateTime?,
)

fun FindroidMovie.toFindroidMovieDto(serverId: String? = null): FindroidMovieDto {
    return FindroidMovieDto(
        id = id,
        serverId = serverId,
        name = name,
        originalTitle = originalTitle,
        overview = overview,
        played = played,
        favorite = favorite,
        runtimeTicks = runtimeTicks,
        playbackPositionTicks = playbackPositionTicks,
        premiereDate = premiereDate,
        communityRating = communityRating,
        officialRating = officialRating,
        status = status,
        productionYear = productionYear,
        endDate = endDate,
    )
}
