package dev.jdtech.jellyfin.models

import java.util.UUID
import org.jellyfin.sdk.model.DateTime
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemPerson
import org.jellyfin.sdk.model.api.PlayAccess

data class JellyfinShowItem(
    override val id: UUID,
    override val name: String,
    override val originalTitle: String?,
    override val overview: String,
    override val sources: List<JellyfinSource>,
    val seasons: List<JellyfinSeasonItem>,
    override val played: Boolean,
    override val favorite: Boolean,
    override val canPlay: Boolean,
    override val canDownload: Boolean,
    override val playbackPositionTicks: Long = 0L,
    override val unplayedItemCount: Int?,
    override val playedPercentage: Float? = null,
    val genres: List<String>,
    val people: List<BaseItemPerson>,
    val runtimeTicks: Long,
    val communityRating: Float?,
    val officialRating: String?,
    val status: String,
    val productionYear: Int?,
    val endDate: DateTime?,
) : JellyfinItem

fun BaseItemDto.toJellyfinShowItem(): JellyfinShowItem {
    return JellyfinShowItem(
        id = id,
        name = name.orEmpty(),
        originalTitle = originalTitle,
        overview = overview.orEmpty(),
        played = userData?.played ?: false,
        favorite = userData?.isFavorite ?: false,
        canPlay = playAccess != PlayAccess.NONE,
        canDownload = canDownload ?: false,
        playbackPositionTicks = userData?.playbackPositionTicks ?: 0L,
        unplayedItemCount = userData?.unplayedItemCount,
        sources = emptyList(),
        seasons = emptyList(),
        genres = genres ?: emptyList(),
        people = people ?: emptyList(),
        runtimeTicks = runTimeTicks ?: 0,
        communityRating = communityRating,
        officialRating = officialRating,
        status = status ?: "Ended",
        productionYear = productionYear,
        endDate = endDate,
    )
}
