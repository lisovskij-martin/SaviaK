package com.example.saviak

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import com.example.api.responseobjects.TicketRO
import com.example.api.service.TicketClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.collections.ArrayList

class TicketListActivityK : AppCompatActivity(), View.OnClickListener {

    private var idNotification: Int? = null
    var notificationIntent: Intent? = null
    var contentIntent: PendingIntent? = null
    private val checkprice = false
    private val mHandler = Handler()
    private var sharedPrefs: SharedPreferences? = null
    var out: TextView? = null
    var listWithTickets: ListView? = null
    var btnClear: Button? = null
    var notificationManager: NotificationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_tickets)
        findByIds()
        sharedPrefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)

        //УВЕДОМЛЕНИЯ
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID, "My channel",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = "My channel description"
        channel.enableLights(true)
        channel.lightColor = Color.RED
        channel.enableVibration(false)
        if (notificationManager != null) {
            notificationManager!!.createNotificationChannel(channel)
        }
        notificationIntent = Intent(this, TicketListActivityK::class.java)
        contentIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        dbHelper = DBHelperK(this)
        database = dbHelper!!.writableDatabase
        tickets = ArrayList()
        read()
        if (tickets!!.size != 0) {
            showTickets()
        }
    }

    override fun onClick(view: View) {
        if (view.id == findViewById<View>(R.id.btnClear).id) {
            mHandler.removeCallbacks(checkPrices)
            mHandler.post(checkPrices)
        }
    }

    fun findByIds() {
        idNotification=1
        out = findViewById(R.id.out)
        listWithTickets = findViewById(R.id.ListWithTickets)
        btnClear = findViewById(R.id.btnClear)
    }

    fun read() {
        tickets = ArrayList()
        val cursor = database!!.query(DBHelperK.TABLE_TICKETS, null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(DBHelperK.KEY_ID)
            val fromPlaceIndex = cursor.getColumnIndex(DBHelperK.KEY_FROMPLACE)
            val toPlaceIndex = cursor.getColumnIndex(DBHelperK.KEY_TOPLACE)
            val gateIndex = cursor.getColumnIndex(DBHelperK.KEY_GATE)
            val wantPriceIndex = cursor.getColumnIndex(DBHelperK.KEY_WANTPRICE)
            val priceIndex = cursor.getColumnIndex(DBHelperK.KEY_PRICE)
            val dateIndex = cursor.getColumnIndex(DBHelperK.KEY_DATE)
            do {
                val tempTicket = TicketShowedK(
                    cursor.getString(idIndex),
                    cursor.getString(fromPlaceIndex),
                    cursor.getString(toPlaceIndex),
                    cursor.getString(gateIndex),
                    cursor.getString(dateIndex),
                    cursor.getDouble(priceIndex),
                    cursor.getDouble(wantPriceIndex)
                )
                tickets!!.add(tempTicket)
            } while (cursor.moveToNext())
        } else {
            Log.d("mLog", "error. 0 rows")
        }
        cursor.close()
    }

    fun showTickets() {
        val adapter =
            ArrayAdapter(this, R.layout.item_list_tickets, R.id.id_text_list_item, tickets)
        for (ticket in tickets!!) {
            Log.d(AviaMainActivityK.TAG, "show 1:" + ticket.value.toString())
        }
        listWithTickets!!.adapter = adapter
        listWithTickets!!.choiceMode = ListView.CHOICE_MODE_NONE
        listWithTickets!!.isClickable = true
        listWithTickets!!.itemsCanFocus = true
        listWithTickets!!.isFocusable = true
        listWithTickets!!.onItemClickListener =
            OnItemClickListener { adapterView, view, i, l ->
                val idDeletedTicket = tickets!![i].idTicket.toString()
                database!!.delete(
                    DBHelperK.TABLE_TICKETS,
                    DBHelperK.KEY_ID + " = ?",
                    arrayOf(idDeletedTicket)
                )
                tickets!!.removeAt(i)
                showTickets()
            }
    }

    fun clearSpisok() {
        database!!.delete(DBHelperK.TABLE_TICKETS, null, null)
    }

    fun notificate(title: String?, text: String?) {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.airplane_material) //ВОзможно тут надо добавить обычный текст еще , хз)))
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.airplane_material
                )
            )
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(contentIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setAutoCancel(true)
        val build = builder.build()
        if (notificationManager != null) {
            notificationManager!!.notify(idNotification!!, build)
            idNotification!!.plus(1)
        }
    }

    private val checkPrices: Runnable = object : Runnable {
        override fun run() {
            val builder = Retrofit.Builder()
                .baseUrl("https://lyssa.aviasales.ru/")
                .addConverterFactory(GsonConverterFactory.create())
            val retrofit = builder.build()
            val ticketsClient = retrofit.create(TicketClient::class.java)
            for (ticket in tickets) {
                val call = ticketsClient.reposForTickets(
                    ticket.fromStroka.split(" ").toTypedArray()[0],
                    ticket.toStroka.split(" ").toTypedArray()[0], ticket.fromDate, ticket.currency,
                    "0", "false"
                )
                call.enqueue(object : Callback<TicketRO> {
                    override fun onResponse(call: Call<TicketRO>, response: Response<TicketRO>) {
                        if ((response != null) && (response.body() != null) && (response.body()!!.prices.size > 0)) {
                            ticket.value = response.body()!!.prices[0].value
                            val ticketValues = ContentValues()
                            ticketValues.put(
                                DBHelperK.KEY_PRICE,
                                response.body()!!.prices[0].value
                            )
                            database!!.update(
                                "tickets",
                                ticketValues,
                                "from_place = ? AND to_place = ? AND date= ?",
                                arrayOf(ticket.fromStroka, ticket.toStroka, ticket.fromDate)
                            )
                            Log.d(
                                "Value check",
                                ticket.value.toString() + " VS " + ticket.wantValue
                            )
                            if (ticket.value < ticket.wantValue) {
                                val notificationTitle = "Билет достиг желаемой цены!"
                                val notificationText = (ticket.fromStroka + " => "
                                        + ticket.toStroka + " ЦЕНА:" + ticket.value)
                                notificate(notificationTitle, notificationText)
                            }
                        } else {
                            Log.d("UPDATE", "Ticket was not find")
                        }
                    }

                    override fun onFailure(call: Call<TicketRO>, t: Throwable) {
                        Log.d(AviaMainActivityK.TAG, "onFailure: " + call.request())
                        Log.d(AviaMainActivityK.TAG, "Exeption!!!! " + t.localizedMessage)
                    }
                })
            }
            read()
            showTickets()
            //                          ЧАСЫ * МИНУТЫ * СЕКУНДЫ * МИЛИСЕКУНДЫ
            mHandler.postDelayed(this, (1 * 1 * 30 * 1000).toLong())
        }
    }

    companion object {
        var dbHelper: DBHelperK? = null
        var tickets: ArrayList<TicketShowedK> = ArrayList()
        var database: SQLiteDatabase? = null
        private const val CHANNEL_ID = "Cat channel"
        const val APP_PREFERENCES = "mysettings"
    }
}