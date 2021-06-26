package com.jordandroid.aac

import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns

object DB_INFO : BaseColumns{
    val dbName="trajetsDB"
    val dbTable="trajets"
    val colID="ID"
    val colTrajetName="Title"
    val colDate="Date"
    val colDistance="Distance"
    val dbVersion=1

    val sqlCreateTable="CREATE TABLE IF NOT EXISTS "+dbTable +" ("+ colID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+ colDate+ " TEXT, "+ colTrajetName+" TEXT, "+ colDistance+" INTEGER);"
    val sqlDelete = "Drop table IF EXISTS "+ dbTable
}