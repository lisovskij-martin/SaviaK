package com.example.saviak

import android.app.DatePickerDialog.OnDateSetListener
import java.util.*

class WorkWithDateK {
    companion object{
    var d =
        OnDateSetListener { view, SelectedYear, SelectedMonthOfYear, SelectedDayOfMonth ->
            AviaMainActivityK.DateIsSet = true
            var dateTodey = String(
                StringBuilder()
                    .append(SelectedYear)
                    .append("-")
                    .append(SelectedMonthOfYear + 1)
                    .append("-")
                    .append(SelectedDayOfMonth)
            )
            if (dateTodey.toCharArray()[6] == '-') dateTodey =
                dateTodey.substring(0, 5) + "0" + dateTodey.substring(5, dateTodey.length - 0)
            if (dateTodey.length == 9) dateTodey =
                dateTodey.substring(0, 8) + "0" + dateTodey.substring(8, 9)
            AviaMainActivityK.dateinput?.text = dateTodey
            AviaMainActivityK.dateFROM = AviaMainActivityK.dateinput?.text.toString()
            view.init(SelectedYear, SelectedMonthOfYear, SelectedDayOfMonth, null)
        }

        fun setInitialDateTime() {
            val year: Int? = AviaMainActivityK.dateAndTime?.get(Calendar.YEAR)
            val month: Int? = AviaMainActivityK.dateAndTime?.get(Calendar.MONTH)
            val day: Int? = AviaMainActivityK.dateAndTime?.get(Calendar.DAY_OF_MONTH)
            var dateToday = buildString {
                append(year)
                append("-")
                append((month?.plus(1)).toString())
                append("-")
                append(day)
            }
            if (dateToday.toCharArray()[6] == '-') dateToday =
                dateToday.substring(0, 5) + "0" + dateToday.substring(5, dateToday.length - 0)
            if (dateToday.length == 9) dateToday =
                dateToday.substring(0, 8) + "0" + dateToday.substring(8, 9)
            AviaMainActivityK.dateinput?.text = dateToday
            AviaMainActivityK.dateFROM = AviaMainActivityK.dateinput?.text.toString()
            AviaMainActivityK.DateIsSet = true
        }
    }


}