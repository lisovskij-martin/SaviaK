package com.example.saviak

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.api.model.trip.Trip
import com.squareup.picasso.Picasso
import kotlin.collections.ArrayList

class TripListActivityK : AppCompatActivity() {

    var tripArrayList: ArrayList<Trip>? = null
    var btn_back: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_trips)
        tripArrayList = intent.extras!!.get("triplist") as ArrayList<Trip>
        btn_back = findViewById(R.id.btn_back)
        val adapter = TripAdapterK(applicationContext, R.layout.item_list_trips, tripArrayList!!)
        val listView = findViewById<ListView>(R.id.listview)
        with(listView){
            this.adapter = adapter
            this.choiceMode = ListView.CHOICE_MODE_NONE
            this.isClickable = true
            this.itemsCanFocus = true
            this.isFocusable = true
            this.onItemClickListener = OnItemClickListener { adapterView, view, i, l -> showAdvancedInfo(
            tripArrayList!![i]) } }
        btn_back!!.setOnClickListener(View.OnClickListener {
            val intent_to_mainactivity = Intent(this, TripMainActivityK::class.java)
            startActivity(intent_to_mainactivity)
        })
    }

    fun showAdvancedInfo(trip: Trip) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("\"" + trip.title + "\"")
        val inflater2 = LayoutInflater.from(this)
        val window_info = inflater2.inflate(R.layout.window_trip_info, null)
        dialog.setView(window_info)

        //txtv_info_advanced_1
        val txtv_info_advanced_1 = window_info.findViewById<TextView>(R.id.txt_info_advanced_1)
        txtv_info_advanced_1.text = String.format("Экскурсовод %s", trip.guide.first_name)
        txtv_info_advanced_1.textSize = 16f
        txtv_info_advanced_1.setTextColor(Color.parseColor("#001f42"))

        //txtv_info_advanced_2
        val txtv_info_advanced_2 = window_info.findViewById<TextView>(R.id.txt_info_advanced_2)
        txtv_info_advanced_2.text = String.format("%s рейтинг", trip.guide.rating.toString())
        txtv_info_advanced_2.textSize = 13f

        //txtv_info_price
        val txtv_info_price = window_info.findViewById<TextView>(R.id.txt_info_price)
        txtv_info_price.setTextColor(Color.parseColor("#002958"))
        txtv_info_price.text = trip.getPriceString()

        //txtv_info_desc
        val txtv_info_desc = window_info.findViewById<TextView>(R.id.txt_info_desc)
        txtv_info_desc.setTextColor(Color.parseColor("#002958"))
        txtv_info_desc.text = String.format("Описание: %s", trip.tagline)

        //txtv_info_limits
        val txtv_limits = window_info.findViewById<TextView>(R.id.txt_info_limits)
        txtv_limits.setTextColor(Color.parseColor("#002958"))
        if (trip.max_persons > 0) txtv_limits.text = trip.max_persons.toString()
        txtv_limits.setTextColor(Color.parseColor("#002958"))

        //txtv_info_timelimit

        var tripDurationString = ""
        tripDurationString = if (trip.duration < 1) (trip.duration * 60).toInt().toString() + " мин" else trip.duration.toInt().toString() + " ч"
        val txtv_info_timelimit = window_info.findViewById<TextView>(R.id.txt_info_timelimit)
        txtv_info_timelimit.text = tripDurationString
        txtv_info_timelimit.setTextColor(Color.parseColor("#002958"))
        val txtv_info_rating = window_info.findViewById<TextView>(R.id.txt_info_rating)
        txtv_info_rating.text = String.format("%s ★", trip.rating)
        txtv_info_rating.setTextColor(Color.parseColor("#002958"))


        //avatar
        val avatar = window_info.findViewById<ImageView>(R.id.image_advanced_info)
        try {
            Picasso.get().load(trip.guide.avatar.medium).resize(200, 200).centerCrop()
                    .into(avatar)
        } catch (e: Exception) {
            Log.e(TAG, "Could not load image from: " + trip.guide.avatar.medium)
        }
        dialog.setNegativeButton("Назад") { dialogInterface, i -> dialogInterface.dismiss() }
        dialog.show()
    }

    companion object {
        private const val TAG = "TriplistActivityK"
    }
}