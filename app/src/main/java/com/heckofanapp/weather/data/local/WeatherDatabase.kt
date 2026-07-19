package com.heckofanapp.weather.data.local

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RenameColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.heckofanapp.weather.data.local.dao.airquality.AirQualityDao
import com.heckofanapp.weather.data.local.dao.github.GithubDao
import com.heckofanapp.weather.data.local.dao.location.LocationsDao
import com.heckofanapp.weather.data.local.dao.weather.WeatherBlocksDao
import com.heckofanapp.weather.data.local.dao.weather.WeatherDao
import com.heckofanapp.weather.data.local.dao.weather.WeatherUnitsDao
import com.heckofanapp.weather.data.local.dao.weather.nws.NwsDao
import com.heckofanapp.weather.data.local.entity.airquality.CurrentAirQualityEntity
import com.heckofanapp.weather.data.local.entity.airquality.HourlyAirQualityEntity
import com.heckofanapp.weather.data.local.entity.github.GithubEntity
import com.heckofanapp.weather.data.local.entity.location.WeatherLocationEntity
import com.heckofanapp.weather.data.local.entity.weather.CurrentWeatherEntity
import com.heckofanapp.weather.data.local.entity.weather.DailyWeatherEntity
import com.heckofanapp.weather.data.local.entity.weather.HourlyWeatherEntity
import com.heckofanapp.weather.data.local.entity.weather.blocks.WeatherBlockEntity
import com.heckofanapp.weather.data.local.entity.weather.nws.NwsGridPointsEntity
import com.heckofanapp.weather.data.local.entity.weather.units.AppWeatherUnitsEntity

val MIGRATION_40_41 = object : Migration(
    endVersion = 41,
    startVersion = 40,
) {
    override fun migrate(
        db: SupportSQLiteDatabase,
    ) {
        /**
         * Need to check if the column already exists
         * because I forgot when it was added? and it keeps crashing because it already exists
         */
        val all = db.query("PRAGMA table_info(weather_hourly)")
        var exists = false

        while (all.moveToNext()) {
            val columnName = all.getString(
                all.getColumnIndexOrThrow("name")
            )

            if (columnName == "dewPoint") {
                exists = true
                break
            }
        }
        all.close()
        if (!exists) {
            db.execSQL(
                "ALTER TABLE weather_hourly ADD COLUMN dewPoint REAL"
            )
        }
    }
}

@Database(
    autoMigrations = [
        AutoMigration(
            from = 39,
            to = 40,
        ),
        AutoMigration(
            from = 42,
            to = 43,
        ),
        AutoMigration(
            from = 43,
            to = 44,
        ),
        AutoMigration(
            from = 44,
            spec = WeatherMasterDatabase.UnitsRenameMigration::class,
            to = 45,
        ),
        AutoMigration(
            from = 46,
            to = 47,
        ),
    ],
    entities = [
        WeatherLocationEntity::class,
        CurrentWeatherEntity::class,
        HourlyWeatherEntity::class,
        DailyWeatherEntity::class,
        AppWeatherUnitsEntity::class,
        WeatherBlockEntity::class,
        CurrentAirQualityEntity::class,
        HourlyAirQualityEntity::class,
        NwsGridPointsEntity::class,
        GithubEntity::class
    ],
    version = 47,
)
abstract class WeatherMasterDatabase : RoomDatabase() {

    @RenameColumn.Entries(
        RenameColumn(
            fromColumnName = "tempUnit",
            tableName = "weather_units",
            toColumnName = "temperature",
        ),
        RenameColumn(
            fromColumnName = "windUnit",
            tableName = "weather_units",
            toColumnName = "speed",
        ),
        RenameColumn(
            fromColumnName = "distanceUnit",
            tableName = "weather_units",
            toColumnName = "distance",
        ),
        RenameColumn(
            fromColumnName = "pressureUnit",
            tableName = "weather_units",
            toColumnName = "pressure",
        ),
        RenameColumn(
            fromColumnName = "precipitationUnit",
            tableName = "weather_units",
            toColumnName = "precipitation",
        ),
    )
    class UnitsRenameMigration : AutoMigrationSpec
    abstract fun airQualityDao(): AirQualityDao
    abstract fun githubDao(): GithubDao
    abstract fun locationsDao(): LocationsDao
    abstract fun nwsDao(): NwsDao
    abstract fun weatherBlocksDao(): WeatherBlocksDao
    abstract fun weatherDao(): WeatherDao
    abstract fun weatherUnitsDao(): WeatherUnitsDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherMasterDatabase? = null

        fun getInstance(context: Context): WeatherMasterDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    WeatherMasterDatabase::class.java,
                    "weather_master.db"
                ).addMigrations(
                    MIGRATION_40_41,
                    MIGRATION_41_42,
                    MIGRATION_45_46,
                ).build().also {
                    INSTANCE = it
                }
            }
    }
}

val MIGRATION_41_42 = object : Migration(
    endVersion = 41,
    startVersion = 42,
) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            ALTER TABLE weather_daily ADD COLUMN dusk INTEGER NOT NULL Default 0
        """.trimIndent()
        )
        db.execSQL(
            """
            ALTER TABLE weather_daily ADD COLUMN dawn INTEGER NOT NULL Default 0
        """.trimIndent()
        )
    }
}

val MIGRATION_45_46 = object : Migration(
    endVersion = 46,
    startVersion = 45,
) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE weather_locations ADD COLUMN sortOrder INTEGER NOT NULL DEFAULT 0"
        )
        // Preserve current implicit order (default-first, then insertion) for existing users
        db.execSQL(
            "UPDATE weather_locations SET sortOrder = rowid"
        )
    }
}
