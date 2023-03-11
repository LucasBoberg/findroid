package dev.jdtech.jellyfin.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.jdtech.jellyfin.models.FindroidEpisodeDto
import dev.jdtech.jellyfin.models.FindroidMediaStreamDto
import dev.jdtech.jellyfin.models.FindroidMovieDto
import dev.jdtech.jellyfin.models.FindroidSeasonDto
import dev.jdtech.jellyfin.models.FindroidShowDto
import dev.jdtech.jellyfin.models.FindroidSourceDto
import dev.jdtech.jellyfin.models.Server
import dev.jdtech.jellyfin.models.ServerAddress
import dev.jdtech.jellyfin.models.TrickPlayManifestDto
import dev.jdtech.jellyfin.models.User

@Database(
    entities = [Server::class, ServerAddress::class, User::class, FindroidMovieDto::class, FindroidShowDto::class, FindroidSeasonDto::class, FindroidEpisodeDto::class, FindroidSourceDto::class, FindroidMediaStreamDto::class, TrickPlayManifestDto::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ServerDatabase : RoomDatabase() {
    abstract val serverDatabaseDao: ServerDatabaseDao
}
