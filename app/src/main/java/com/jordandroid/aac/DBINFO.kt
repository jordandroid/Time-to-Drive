package com.jordandroid.aac

import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns

object DBINFO : BaseColumns{
    const val dbName="trajetsDB"
    const val dbTable="trajets"
    const val colID="ID"
    const val colTrajetName="Title"
    const val colDate="Date"
    const val colDistance="Distance"
    const val dbVersion=1

    const val sqlCreateTable = "CREATE TABLE IF NOT EXISTS $dbTable ($colID INTEGER PRIMARY KEY AUTOINCREMENT,$colDate TEXT, $colTrajetName TEXT, $colDistance INTEGER);"
    const val sqlDelete = "Drop table IF EXISTS $dbTable"
}