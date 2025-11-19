package cl.duoc.myapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cl.duoc.myapplication.database.dao.SessionDao
import cl.duoc.myapplication.database.dao.UserDao
import cl.duoc.myapplication.model.User
import cl.duoc.myapplication.model.Session


@Database(
    entities = [User::class, Session::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun sessionDao(): SessionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fashion_app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}