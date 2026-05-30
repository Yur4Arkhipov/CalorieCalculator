package com.jacqulin.calcalc.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jacqulin.calcalc.core.data.local.dao.MealDao
import com.jacqulin.calcalc.core.data.local.entities.IngredientEntity
import com.jacqulin.calcalc.core.data.local.entities.MealEntity

@Database(
    entities = [
        MealEntity::class,
        IngredientEntity::class
    ],
    version = 4,
    exportSchema = true
)
abstract class MealDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE meal ADD COLUMN imageUri TEXT")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE meal ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `ingredient` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `mealId` INTEGER NOT NULL,
                        `name` TEXT NOT NULL,
                        `weight` DOUBLE NOT NULL,
                        `calories` DOUBLE NOT NULL,
                        `protein` DOUBLE NOT NULL,
                        `carb` DOUBLE NOT NULL,
                        `fat` DOUBLE NOT NULL,
                        FOREIGN KEY(`mealId`) REFERENCES `meal`(`id`) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_ingredient_mealId` ON `ingredient` (`mealId`)")
            }
        }

        fun provideMigrations() = arrayOf(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
    }
}