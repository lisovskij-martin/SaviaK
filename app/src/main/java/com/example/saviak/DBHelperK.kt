package com.example.saviak

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelperK internal constructor(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "create table " + TABLE_TICKETS + "(" + KEY_ID + " integer primary key,"
                    + KEY_FROMPLACE + " text, "
                    + KEY_TOPLACE + " text, "
                    + KEY_DATE + " text, "
                    + KEY_GATE + " text, "
                    + KEY_PRICE + " real, "
                    + KEY_WANTPRICE + " real"
                    + ")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, i: Int, i1: Int) {
        db.execSQL("drop table if exists $TABLE_TICKETS")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_VERSION = 5
        private const val DATABASE_NAME = "ticketDB"
        const val TABLE_TICKETS = "tickets"
        const val KEY_ID = "_id"
        const val KEY_FROMPLACE = "from_place"
        const val KEY_TOPLACE = "to_place"
        const val KEY_DATE = "date"
        const val KEY_PRICE = "price"
        const val KEY_WANTPRICE = "want_price"
        const val KEY_GATE = "gate"
    }
}