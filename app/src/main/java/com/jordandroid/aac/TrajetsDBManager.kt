package com.jordandroid.aac

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TrajetsDBManager(context: Context) : SQLiteOpenHelper(context, DBINFO.dbName , null, DBINFO.dbVersion) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(DBINFO.sqlCreateTable)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(DBINFO.sqlDelete)
        onCreate(db)
    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
}
