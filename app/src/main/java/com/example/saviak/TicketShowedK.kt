package com.example.saviak

class TicketShowedK(
    var idTicket: String,
    var fromStroka: String,
    var toStroka:String,
    var gate:String,
    var fromDate: String,
    var value: Double,
    var wantValue: Double
) {
    var currency: String = "rub"

    override fun toString(): String {
        return buildString{
            append(fromStroka.split(" ").toTypedArray()[1]).append(" --> ")
            append(toStroka.split(" ").toTypedArray()[1]).append("\n")
            append(fromDate).append("  in  ")
            append(gate).append("\n")
            append(Math.round(value!!)).append(" руб ( Желаемая: ")
            append(Math.round(wantValue!!).toString() + " )")
            toString()
        }
    }


}