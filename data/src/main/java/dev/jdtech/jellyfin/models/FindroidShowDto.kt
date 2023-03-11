package dev.jdtech.jellyfin.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.UUID

@Entity(tableName = "shows")
data class FindroidShowDto(
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
    val communityRating: Float?,
    val officialRating: String?,
    val status: String,
    val productionYear: Int?,
    val endDate: LocalDateTime?,
    val unplayedItemCount: Int? = null,
)

fun FindroidShow.toFindroidShowDto(serverId: String? = null): FindroidShowDto {
    return FindroidShowDto(
        id = id,
        serverId = serverId,
        name = name,
        originalTitle = originalTitle,
        overview = overview,
        played = played,
        favorite = favorite,
        runtimeTicks = runtimeTicks,
        playbackPositionTicks = playbackPositionTicks,
        communityRating = communityRating,
        officialRating = officialRating,
        status = status,
        productionYear = productionYear,
        endDate = endDate,
    )
}
