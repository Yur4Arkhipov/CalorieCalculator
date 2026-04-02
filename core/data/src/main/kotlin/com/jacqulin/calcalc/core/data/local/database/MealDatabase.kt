package com.jacqulin.calcalc.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jacqulin.calcalc.core.data.local.dao.MealDao
import com.jacqulin.calcalc.core.data.local.entities.MealComponentEntity
import com.jacqulin.calcalc.core.data.local.entities.MealEntity

@Database(
    entities = [MealEntity::class, MealComponentEntity::class],
    version = 4
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
                // Create meal_component table
                db.execSQL(
                    """CREATE TABLE `meal_component` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `mealId` INTEGER NOT NULL, 
                        `name` TEXT NOT NULL, 
                        `calories` INTEGER NOT NULL, 
                        `protein` INTEGER NOT NULL, 
                        `fat` INTEGER NOT NULL, 
                        `carbs` INTEGER NOT NULL, 
                        FOREIGN KEY(`mealId`) REFERENCES `meal`(`id`) ON DELETE CASCADE
                    )""".trimIndent()
                )

                // Create index on mealId
                db.execSQL("CREATE INDEX `index_meal_component_mealId` ON `meal_component` (`mealId`)")
            }
        }
    }
}