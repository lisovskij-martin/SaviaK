package com.example.saviak

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import com.example.api.model.aviaticket.City
import com.example.api.model.trip.Trip
import com.example.api.responseobjects.CityRO
import com.example.api.responseobjects.TripRO
import com.example.api.service.CityClientTravelP
import com.example.api.service.CityClientTripster
import com.example.api.service.TripClient
import pl.droidsonroids.gif.GifImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class TripMainActivityK() : AppCompatActivity() {
    var listView: ListView? = null
    var btn_search: Button? = null
    var btn_to_avia: Button? = null
    var tripArrayList: ArrayList<Trip>? = null
    var tripFavouriteList: ArrayList<Trip>? = null
    var search_gifview: GifImageView? = null
    var dateAndTime: Calendar? = null
    var listView_city: ListView? = null
    var cityIsSet = false
    var LL_trip_main: RelativeLayout? = null
    var btn_close_city: ImageButton? = null
    var btn_dataChoose1: ImageButton? = null
    var btn_dataChoose2: ImageButton? = null
    var btn_priceChoose1: ImageButton? = null
    var btn_priceChoose2: ImageButton? = null
    var editTxtCity: EditText? = null
    var txtv_date1: TextView? = null
    var txtv_date2: TextView? = null
    var txtv_price1: TextView? = null
    var txtv_price2: TextView? = null
    private var sharedPrefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_trip)
        setInitialValue()
        setListeners()
        sharedPrefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
    }

    private fun setInitialValue() {
        tripArrayList = ArrayList()
        tripFavouriteList = ArrayList()
        listView = findViewById(R.id.listview)
        btn_search = findViewById(R.id.btn_search_trip)
        btn_close_city = findViewById(R.id.imgbtn_close_city)
        editTxtCity = findViewById(R.id.edittxt_trip_city)
        txtv_date1 = findViewById(R.id.txt_date1)
        txtv_date2 = findViewById(R.id.txt_date2)
        txtv_price1 = findViewById(R.id.txt_price1)
        txtv_price2 = findViewById(R.id.txt_price2)
        LL_trip_main = findViewById(R.id.LL_trip_main)
        search_gifview = findViewById(R.id.search_gifview)
        btn_dataChoose1 = findViewById(R.id.btn_dataChoose1)
        btn_dataChoose2 = findViewById(R.id.btn_dataChoose2)
        btn_priceChoose1 = findViewById(R.id.btn_priceChoose1)
        btn_priceChoose2 = findViewById(R.id.btn_priceChoose2)
        listView_city = findViewById(R.id.listView_city)
        btn_to_avia = findViewById(R.id.btn_to_avia)
        dateAndTime = Calendar.getInstance()
        setInitialDateTime(dateAndTime)
    }

    private fun setListeners() {
        btn_search!!.setOnClickListener { //                Toast toast1=Toast.makeText(getApplicationContext(), "НАЖАЛ!", Toast.LENGTH_SHORT);
            //                toast1.show();
            if (cityIsSet && (editTxtCity!!.text.toString().length > 1) && (txtv_date1!!.text != null) && (txtv_date2!!.text != null)) {
                LL_trip_main!!.visibility = View.GONE
                search_gifview!!.visibility = View.VISIBLE
                searchTrips()
            }
        }
        btn_to_avia!!.setOnClickListener {
            val intent_to_avia = Intent(this, AviaMainActivityK::class.java)
            startActivity(intent_to_avia)
        }
        btn_dataChoose1!!.setOnClickListener {
            DatePickerDialog(
                this, datePickListener1,
                dateAndTime!![Calendar.YEAR],
                dateAndTime!![Calendar.MONTH],
                dateAndTime!![Calendar.DAY_OF_MONTH]
            )
                .show()
        }
        btn_dataChoose2!!.setOnClickListener {
            DatePickerDialog(
                this, datePickListener2,
                dateAndTime!![Calendar.YEAR],
                dateAndTime!![Calendar.MONTH],
                dateAndTime!![Calendar.DAY_OF_MONTH]
            )
                .show()
        }
        btn_priceChoose1!!.setOnClickListener { if (cityIsSet) showPriceDialog(txtv_price1) }
        btn_priceChoose2!!.setOnClickListener { if (cityIsSet) showPriceDialog(txtv_price2) }
        btn_close_city!!.setOnClickListener {
            editTxtCity!!.setText("")
            editTxtCity!!.isEnabled = true
            cityIsSet = false
            listView_city!!.adapter = null
            listView_city!!.visibility = View.VISIBLE
        }
        if (checkPermForInternet()) editTxtCity!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                Log.d(
                    TAG,
                    "beforeTextChanged1:$s"
                )
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                Log.d(
                    TAG,
                    "onTextChanged1:$s"
                )
                if (s.length != 0 && !cityIsSet) getCity(s.toString())
            }

            override fun afterTextChanged(s: Editable) {
                Log.d(
                    TAG,
                    "afterTextChanged1:$s"
                )
            }
        })
    }

    private fun searchTrips() {
        val builder = Retrofit.Builder()
            .baseUrl("https://experience.tripster.ru/api/search/")
            .addConverterFactory(GsonConverterFactory.create())
        val retrofit = builder.build()
        val ticketsClient = retrofit.create(TripClient::class.java)
        val call = ticketsClient.reposForTrips(
            cityID,
            "json",
            "false",
            "price",
            txtv_price1!!.text.toString().split(" ").toTypedArray()[0],
            txtv_price2!!.text.toString().split(" ").toTypedArray()[0],
            txtv_date1!!.text.toString(),
            txtv_date2!!.text.toString()
        )
        Log.d(
            "WTF",
            cityID + " " + txtv_price1!!.text.toString()
                .substring(0, txtv_price1!!.text.toString().length - 1) + " " +
                    txtv_price2!!.text.toString()
                        .substring(0, txtv_price2!!.text.toString().length - 1) + " " +
                    txtv_date1!!.text.toString() + " " +
                    txtv_date2!!.text.toString()
        )
        call.enqueue(object : Callback<TripRO?> {
            override fun onResponse(call: Call<TripRO?>, response: Response<TripRO?>) {
                if ((response != null) && (response.body() != null) && (response.body()!!.results.size > 0)) {
                    Log.d(TAG, "response.body():" + response.body()!!.results[0].tagline)
                    tripArrayList = ArrayList()
                    tripArrayList!!.addAll(response.body()!!.results)
                    val intent_to_triplist = Intent(
                        this@TripMainActivityK,
                        TripListActivityK::class.java
                    )
                    intent_to_triplist.putExtra("triplist", tripArrayList)
                    startActivity(intent_to_triplist)
                } else {
                    val toast = Toast.makeText(
                        applicationContext,
                        "Экскурсий не найдено!",
                        Toast.LENGTH_SHORT
                    )
                    val v = toast.view.findViewById<View>(android.R.id.message) as TextView
                    search_gifview!!.visibility = View.GONE
                    LL_trip_main!!.visibility = View.VISIBLE
                    v.setTextColor(Color.RED)
                    v.setBackgroundColor(0)
                    toast.show()
                }
            }

            override fun onFailure(call: Call<TripRO?>, t: Throwable) {
                search_gifview!!.visibility = View.GONE
                LL_trip_main!!.visibility = View.VISIBLE
                Log.d(TAG, "onFailure: " + call.request())
                Log.d(TAG, "Exeption!!!! " + t.localizedMessage)
            }
        })
    }

    private fun checkPermForInternet(): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            //динамическое получение прав на INTERNET
            if ((checkSelfPermission(Manifest.permission.INTERNET)
                        == PackageManager.PERMISSION_GRANTED)
            ) {
                Log.d(TAG, "Permission is granted")
                return true
            } else {
                Log.d(TAG, "Permission is revoked")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.INTERNET),
                    1
                )
            }
        } else {
        }
        return false
    }

    fun getCity(cityName: String?) {
        val builder = Retrofit.Builder()
            .baseUrl("https://places.aviasales.ru/v2/")
            .addConverterFactory(GsonConverterFactory.create())
        val retrofit = builder.build()
        val cityClientTravelP = retrofit.create(
            CityClientTravelP::class.java
        )
        val call = cityClientTravelP.reposForCities(
            (cityName)!!, "ru", "city", "7"
        )
        call.enqueue(object : Callback<List<City>> {
            override fun onResponse(call: Call<List<City>>, response: Response<List<City>>) {
                val cities = (response.body())!!
                val citiesToListString = ArrayList<String>()
                for (city: City in cities) {
                    citiesToListString.add(city.toString())
                }
                val adapter = ArrayAdapter(
                    this@TripMainActivityK,
                    R.layout.item_list_trip_cities,
                    R.id.text1,
                    citiesToListString
                )
                Log.d(TAG, "onResponse: ")
                listView_city!!.adapter = adapter
                val list = listView_city
                list!!.choiceMode = ListView.CHOICE_MODE_SINGLE
                list.onItemClickListener =
                    OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
                        cityIsSet = true
                        //editTxtCity.setText(citiesToListString.get(position));
                        cityIATA =
                            citiesToListString.get(position).split(" ").toTypedArray().get(0)
                        adapter.clear()
                        //editTxtCity.setEnabled(false);
                        Log.d(
                            "getcityfrom",
                            "cityIATA=" + cityIATA
                        )
                        city2
                    }
            }

            override fun onFailure(call: Call<List<City>>, t: Throwable) {
                Log.d(TAG, "Exeption!!!! " + t.localizedMessage)
            }
        })
    }

    val city2: Unit
        get() {
            val builder = Retrofit.Builder()
                .baseUrl("https://experience.tripster.ru/api/")
                .addConverterFactory(GsonConverterFactory.create())
            val retrofit = builder.build()
            val cityClientTripster = retrofit.create(
                CityClientTripster::class.java
            )
            val call = cityClientTripster.reposForCities((cityIATA)!!)
            call.enqueue(object : Callback<CityRO?> {
                override fun onResponse(call: Call<CityRO?>, response: Response<CityRO?>) {
                    if ((response != null) && (response.body() != null) && (response.body()!!.results.size > 0)) {
                        Log.d(
                            "getcity2", ("response.body().getResults().get(0).getName_ru:"
                                    + response.body()!!.results[0].name_ru)
                        )
                        editTxtCity!!.setText(response.body()!!.results[0].name_ru)
                        cityID = response.body()!!.results[0].id
                        cityCurrency = response.body()!!.results[0].country.currency
                        txtv_price1!!.text = String.format(
                            "%s %s", txtv_price1!!.text.toString()
                                .split(" ").toTypedArray().get(0), cityCurrency
                        )
                        txtv_price2!!.text = String.format(
                            "%s %s", txtv_price2!!.text.toString()
                                .split(" ").toTypedArray().get(0), cityCurrency
                        )
                        editTxtCity!!.isEnabled = false
                        cityIsSet = true
                    } else {
                        val toast = Toast.makeText(
                            applicationContext,
                            "Экскурсий не найдено!",
                            Toast.LENGTH_SHORT
                        )
                        val v = toast.view.findViewById<View>(android.R.id.message) as TextView
                        v.setTextColor(Color.RED)
                        v.setBackgroundColor(0)
                        toast.show()
                    }
                }

                override fun onFailure(call: Call<CityRO?>, t: Throwable) {
                    Log.d(TAG, "onFailure: " + call.request())
                    Log.d(TAG, "Exeption!!!! " + t.localizedMessage)
                }
            })
        }

    fun setPrefs() {
        Log.d("setPrefs", "Город=" + editTxtCity!!.text.toString())
        val editor = sharedPrefs!!.edit()
        editor.putString("Город", editTxtCity!!.text.toString())
        editor.putString("Дата от", txtv_date1!!.text.toString())
        editor.putString("Дата до", txtv_date2!!.text.toString())
        editor.putString("Цена от", txtv_price1!!.text.toString())
        editor.putString("Цена до", txtv_price2!!.text.toString())
        editor.putString("cityIATA", cityIATA)
        editor.putString("cityCurrency", cityCurrency)
        editor.apply()
    }

    // метод для получения текста из SharedPreferences по ключу
    val prefs: Unit
        get() {
            val editor = sharedPrefs!!.edit()
            Log.d("getPrefs", "Город=" + sharedPrefs!!.getString("Город", ""))
            if (sharedPrefs!!.contains("Город")) {
                cityIsSet = true
                listView_city!!.visibility = View.GONE
                editTxtCity!!.setText(sharedPrefs!!.getString("Город", ""))
                editTxtCity!!.isEnabled = (editTxtCity!!.text.toString() == "")
            }
            if (sharedPrefs!!.contains("Дата от")) {
                txtv_date1!!.text = sharedPrefs!!.getString("Дата от", "")
            }
            if (sharedPrefs!!.contains("Дата до")) {
                txtv_date2!!.text = sharedPrefs!!.getString("Дата до", "")
            }
            if (sharedPrefs!!.contains("Цена от")) {
                txtv_price1!!.text = sharedPrefs!!.getString("Цена от", "")
            }
            if (sharedPrefs!!.contains("Цена до")) {
                txtv_price2!!.text = sharedPrefs!!.getString("Цена до", "")
            }
            if ((sharedPrefs!!.contains("cityIATA") && sharedPrefs!!.getString(
                    "cityIATA",
                    ""
                ) != "")
            ) {
                cityIATA = sharedPrefs!!.getString("cityIATA", "")
                city2
            }
            if (sharedPrefs!!.contains("cityCurrency")) {
                cityCurrency = sharedPrefs!!.getString("cityCurrency", "")
            }
        }

    override fun onResume() {
        Log.d("MainActivity", "onResume")
        super.onResume()
        prefs
    }

    override fun onPause() {
        Log.d("MainActivity", "onPause")
        setPrefs()
        super.onPause()
    }

    override fun onStop() {
        Log.d("MainActivity", "onDestroy")
        LL_trip_main!!.visibility = View.VISIBLE
        search_gifview!!.visibility = View.GONE
        super.onStop()
    }

    override fun onDestroy() {
        applicationContext.deleteSharedPreferences(APP_PREFERENCES)
        super.onDestroy()
    }

    fun showPriceDialog(textView: TextView?) {
        val alert = AlertDialog.Builder(this)
        if (textView === txtv_price1) alert.setTitle("Укажите нижний диапазон:") else alert.setTitle(
            "Укажите верхний диапазон"
        )
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER
        alert.setView(input)
        alert.setPositiveButton(
            "ОК"
        ) { dialog, whichButton ->
            val temp = input.text.toString()
            textView!!.text = String.format(
                "%s %s",
                temp,
                cityCurrency
            )
        }
        alert.setNegativeButton(
            "Cancel"
        ) { dialog, whichButton ->
            // Canceled.
        }
        alert.show()
    }

    private val datePickListener1: OnDateSetListener =
        OnDateSetListener { view, SelectedYear, SelectedMonthOfYear, SelectedDayOfMonth ->
            var date_selected = String(
                StringBuilder()
                    .append(SelectedYear)
                    .append("-")
                    .append(SelectedMonthOfYear + 1)
                    .append("-")
                    .append(SelectedDayOfMonth)
            )
            if (date_selected.toCharArray()[6] == '-') date_selected =
                (date_selected.substring(0, 5) + "0"
                        + date_selected.substring(5, date_selected.length - 0))
            if (date_selected.length == 9) date_selected = (date_selected.substring(0, 8) + "0"
                    + date_selected.substring(8, 9))
            txtv_date1!!.text = date_selected
            view.init(SelectedYear, SelectedMonthOfYear, SelectedDayOfMonth, null)
        }
    private val datePickListener2: OnDateSetListener =
        OnDateSetListener { view, SelectedYear, SelectedMonthOfYear, SelectedDayOfMonth ->
            var date_selected = String(
                StringBuilder()
                    .append(SelectedYear)
                    .append("-")
                    .append(SelectedMonthOfYear + 1)
                    .append("-")
                    .append(SelectedDayOfMonth)
            )
            if (date_selected.toCharArray()[6] == '-') date_selected =
                (date_selected.substring(0, 5) + "0"
                        + date_selected.substring(5, date_selected.length - 0))
            if (date_selected.length == 9) date_selected = (date_selected.substring(0, 8) + "0"
                    + date_selected.substring(8, 9))
            txtv_date2!!.text = date_selected
            view.init(SelectedYear, SelectedMonthOfYear, SelectedDayOfMonth, null)
        }

    private fun setInitialDateTime(dateAndTime: Calendar?) {
        val year = dateAndTime!![Calendar.YEAR]
        val month = dateAndTime[Calendar.MONTH]
        val day = dateAndTime[Calendar.DAY_OF_MONTH]
        var dateToday = String(
            StringBuilder()
                .append(year)
                .append("-")
                .append(month + 1)
                .append("-")
                .append(day)
        )
        if (dateToday.toCharArray()[6] == '-') dateToday =
            dateToday.substring(0, 5) + "0" + dateToday.substring(5, dateToday.length - 0)
        if (dateToday.length == 9) dateToday =
            dateToday.substring(0, 8) + "0" + dateToday.substring(8, 9)
        txtv_date1!!.text = dateToday
        txtv_date2!!.text = dateToday
    }

    companion object {
        var TAG = "HTTP"
        var cityIATA: String? = ""
        var cityID = ""
        var cityCurrency: String? = ""
        val APP_PREFERENCES = "mysettings"
    }
}