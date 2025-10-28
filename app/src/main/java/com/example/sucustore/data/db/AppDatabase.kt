package com.example.sucustore.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.sucustore.data.db.dao.*
import com.example.sucustore.data.db.entity.*

@Database(
    entities = [
        User::class,
        Product::class,
        CartItem::class,
        Order::class,
    ],
    version = 2, // 🔺 Subimos la versión de 1 a 2
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // DAOs
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao

    companion object {

        // 🔧 MIGRACIÓN: de versión 1 → 2
        // Esto agrega la nueva columna 'role' a la tabla 'users'
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE users ADD COLUMN role TEXT NOT NULL DEFAULT 'CLIENT'"
                )
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        // ⚙️ Método para obtener la base de datos (singleton)
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sucustore_db"
                )
                    // Registramos la migración aquí 👇
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
