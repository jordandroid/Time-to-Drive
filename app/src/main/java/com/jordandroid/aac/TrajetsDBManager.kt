package com.jordandroid.aac;

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class TrajetsDBManager(context: Context) : SQLiteOpenHelper(context, DB_INFO.dbName , null, DB_INFO.dbVersion) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(DB_INFO.sqlCreateTable)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(DB_INFO.sqlDelete)
        onCreate(db)
    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
}
