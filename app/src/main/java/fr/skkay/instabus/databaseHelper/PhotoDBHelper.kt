package fr.skkay.instabus.databaseHelper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import fr.skkay.instabus.contracts.PhotoContract

class PhotoDBHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "photoList.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val SQL_CREATE_PHOTOLIST_TABLE =
                "CREATE TABLE ${PhotoContract.PhotoEntry.TABLE_NAME} (" +
                    "${PhotoContract.PhotoEntry._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "${PhotoContract.PhotoEntry.COLUMN_TITLE} TEXT NOT NULL, " +
                    "${PhotoContract.PhotoEntry.COLUMN_IMAGE} INTEGER NOT NULL, " +
                    "${PhotoContract.PhotoEntry.COLUMN_TIMESTAMP} TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "${PhotoContract.PhotoEntry.COLUMN_STATION_ID} TEXT NOT NULL" +
                ");"
        db?.execSQL(SQL_CREATE_PHOTOLIST_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ${PhotoContract.PhotoEntry.TABLE_NAME}")
        onCreate(db)
    }
}
