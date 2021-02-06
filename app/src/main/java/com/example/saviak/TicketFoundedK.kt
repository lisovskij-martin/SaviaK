package com.example.saviak

import android.content.ContentValues

class TicketFoundedK {
   companion object{
    var fromIATA: String? = null
    var toIATA: String? = null
    var fromSTROKA: String? = null
    var toSTROKA: String? = null
    var currency = "rub"
    var fromDATE: String? = null
    var gate: String? = null
    var value: Double? = null
    var wantValue: Double? = null
    var range: Int = 0
    var dbHelper = AviaMainActivityK.dbHelper
    override fun toString(): String {
        return buildString {
            append(fromSTROKA!!.split(" ").toTypedArray()[1]).append(" --> ")
            append(toSTROKA!!.split(" ").toTypedArray()[1]).append("\n")
            append(fromDATE).append("  in  ")
            append(gate).append("\n")
            append(Math.round(value!!)).append(" руб ( Желаемая: ")
            append(Math.round(wantValue!!).toString() + " )")
        }
    }

    fun toBase() {
        val database = dbHelper?.writableDatabase
        val contentValues = ContentValues()
        with(contentValues){
            put(DBHelperK.KEY_TOPLACE, toSTROKA)
            put(DBHelperK.KEY_FROMPLACE, fromSTROKA)
            put(DBHelperK.KEY_DATE, fromDATE)
            put(DBHelperK.KEY_GATE, gate)
            put(DBHelperK.KEY_PRICE, value)
            put(DBHelperK.KEY_WANTPRICE, wantValue)
        }
        database?.insert(DBHelperK.TABLE_TICKETS, null, contentValues)
    }
   }
}