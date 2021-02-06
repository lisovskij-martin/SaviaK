package com.example.saviak

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
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
import com.example.api.responseobjects.TicketRO
import com.example.api.service.CityClientTravelP
import com.example.api.service.NearestCityClient
import com.example.api.service.TicketClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class AviaMainActivityK : AppCompatActivity(), View.OnClickListener {
    var cityInput1: EditText? = null
    var cityInput2: EditText? = null
    var txtTicketOut: TextView? = null
    var wantvalue: TextView? = null
    var save: Button? = null
    var goToSpisok: Button? = null
    var btn_to_trip: Button? = null
    var listCity1: ListView? = null
    var listCity2: ListView? = null
    var imgbtn1: ImageButton? = null
    var imgbtn2: ImageButton? = null
    var mSettings: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_avia)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        setId()
        setInitialValue()
        WorkWithDateK.setInitialDateTime()
        findNearestAirport()
        dbHelper = DBHelperK(this)

        //РАБОТА С ГОРОДАМИ
        if (checkPermForInternet()) cityInput1!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                Log.d(
                    TAG,
                    "beforeTextChanged1:$s"
                )
                listCity1!!.visibility = View.VISIBLE
                findViewById<View>(R.id.btn_to_trip).visibility = View.GONE
                findViewById<View>(R.id.btn_logout).visibility = View.GONE
                listCity1!!.elevation = 4 * applicationContext.resources.displayMetrics.density
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                Log.d(
                    TAG,
                    "onTextChanged1:$s"
                )
                if (s.length != 0 && !CityFrom) getCityFrom(s.toString())
                //findViewById(R.id.LLcity2).setVisibility(View.INVISIBLE);
                findViewById<View>(R.id.ListCity2).visibility = View.INVISIBLE
                findViewById<View>(R.id.LLticketOut).visibility = View.INVISIBLE
                findViewById<View>(R.id.Cross1).visibility = View.VISIBLE
            }

            override fun afterTextChanged(s: Editable) {
                Log.d(
                    TAG,
                    "afterTextChanged1:$s"
                )
            }

            fun getCityFrom(cityName: String?) {
                val builder = Retrofit.Builder()
                    .baseUrl("https://places.aviasales.ru/v2/")
                    .addConverterFactory(GsonConverterFactory.create())
                val retrofit = builder.build()
                val cityClientTravelP = retrofit.create(
                    CityClientTravelP::class.java
                )
                val call = cityClientTravelP.reposForCities(
                    cityName!!, "ru", "city", "7"
                )
                call.enqueue(object : Callback<List<City>> {
                    override fun onResponse(
                        call: Call<List<City>>,
                        response: Response<List<City>>
                    ) {
                        val cities =
                            response.body()!!
                        val citiesToListString = ArrayList<String>()
                        for (city in cities) {
                            citiesToListString.add(city.toString())
                        }
                        val adapter = ArrayAdapter(
                            this@AviaMainActivityK,
                            R.layout.item_list_avia_cities,
                            R.id.text1,
                            citiesToListString
                        )
                        Log.d(TAG, "onResponse: ")
                        listCity1!!.adapter = adapter
                        val list = listCity1
                        list!!.choiceMode = ListView.CHOICE_MODE_SINGLE
                        list.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                CityFrom = true
                                cityInput1!!.setText(citiesToListString[position])
                                fromIATA =
                                    citiesToListString[position].split(" ").toTypedArray()[0]
                                fromSTROKA =
                                    citiesToListString[position]
                                adapter.clear()
                                cityInput1!!.isEnabled = false
                                cityInput1!!.textSize = 18f
                                //findViewById(R.id.ListCity2).setVisibility(View.VISIBLE);
                                findViewById<View>(R.id.Cross2).visibility = View.VISIBLE
                                listCity1!!.visibility = View.GONE
                                findViewById<View>(R.id.btn_to_trip).visibility =
                                    View.VISIBLE
                                findViewById<View>(R.id.btn_logout).visibility =
                                    View.VISIBLE
                                listCity1!!.elevation =
                                    0 * applicationContext.resources.displayMetrics.density
                                //findViewById(R.id.LLticketOut).setVisibility(View.VISIBLE);
                            }
                    }

                    override fun onFailure(call: Call<List<City>>, t: Throwable) {
                        Log.d(TAG, "Exeption!!!! " + t.localizedMessage)
                    }
                })
            }
        })
        if (checkPermForInternet()) cityInput2!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                Log.d(
                    TAG,
                    "beforeTextChanged2:$s"
                )
                //findViewById(R.id.LLcity1).setVisibility(View.INVISIBLE);
                findViewById<View>(R.id.LLticketOut).visibility = View.INVISIBLE
                findViewById<View>(R.id.ListCity1).visibility = View.GONE
                findViewById<View>(R.id.btn_to_trip).visibility = View.GONE
                findViewById<View>(R.id.btn_logout).visibility = View.GONE
                listCity2!!.visibility = View.VISIBLE
                listCity2!!.elevation = 4 * applicationContext.resources.displayMetrics.density
                findViewById<View>(R.id.Cross2).visibility = View.VISIBLE
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                Log.d(
                    TAG,
                    "onTextChanged2:$s"
                )
                if (s.length != 0 && !CityTo) getCityTo(s.toString())
            }

            override fun afterTextChanged(s: Editable) {
                Log.d(
                    TAG,
                    "afterTextChanged2:$s"
                )
            }

            fun getCityTo(cityName: String?) {
                val builder = Retrofit.Builder()
                    .baseUrl("https://places.aviasales.ru/v2/")
                    .addConverterFactory(GsonConverterFactory.create())
                val retrofit = builder.build()
                val cityClientTravelP = retrofit.create(
                    CityClientTravelP::class.java
                )
                val call = cityClientTravelP.reposForCities(
                    cityName!!, "ru", "city", "7"
                )
                call.enqueue(object : Callback<List<City>> {
                    override fun onResponse(
                        call: Call<List<City>>,
                        response: Response<List<City>>
                    ) {
                        val cities =
                            response.body()!!
                        val citiesToListString = ArrayList<String>()
                        val adapter2 = ArrayAdapter(
                            this@AviaMainActivityK,
                            R.layout.item_list_avia_cities,
                            R.id.text1,
                            citiesToListString
                        )
                        for (city in cities) {
                            citiesToListString.add(city.toString())
                        }
                        Log.d(TAG, "onResponse: ")
                        listCity2!!.adapter = adapter2
                        val list = listCity2
                        list!!.choiceMode = ListView.CHOICE_MODE_SINGLE
                        list.onItemClickListener =
                            OnItemClickListener { parent, view, position, id ->
                                CityTo = true
                                cityInput2!!.setText(citiesToListString[position])
                                toIATA =
                                    citiesToListString[position].split(" ").toTypedArray()[0]
                                toSTROKA =
                                    citiesToListString[position]
                                adapter2.clear()
                                cityInput2!!.isEnabled = false
                                cityInput2!!.textSize = 18f
                                findViewById<View>(R.id.btn_to_trip).visibility =
                                    View.VISIBLE
                                findViewById<View>(R.id.btn_logout).visibility =
                                    View.VISIBLE
                                //findViewById(R.id.ListCity1).setVisibility(View.VISIBLE);
                                //findViewById(R.id.LLticketOut).setVisibility(View.VISIBLE);
                                findViewById<View>(R.id.Cross1).visibility = View.VISIBLE
                                listCity2!!.visibility = View.GONE
                                listCity1!!.elevation =
                                    0 * applicationContext.resources.displayMetrics.density
                            }
                    }

                    override fun onFailure(call: Call<List<City>>, t: Throwable) {
                        Log.d(TAG, "Exeption!!!! " + t.localizedMessage)
                    }
                })
            }
        })
    }

    fun findNearestAirport() {
        val builder = Retrofit.Builder()
            .baseUrl("https://places.aviasales.ru/v1/")
            .addConverterFactory(GsonConverterFactory.create())
        val retrofit = builder.build()
        val nearestCityClient = retrofit.create(
            NearestCityClient::class.java
        )
        val call = nearestCityClient.reposForGorods("ru")
        Log.d(TAG, "getData: 12321312")
        call.enqueue(object : Callback<List<City>> {
            override fun onResponse(call: Call<List<City>>, response: Response<List<City>>) {
                nearestAirport!!.text =
                    String.format("Ближайший аэропорт: %s", response.body()!![0].toString())
            }

            override fun onFailure(call: Call<List<City>>, t: Throwable) {
                Log.d(TAG, "Exeption!!!! " + t.localizedMessage)
            }
        })
    }

    fun checkPermForInternet(): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            //динамическое получение прав на INTERNET
            if (checkSelfPermission(Manifest.permission.INTERNET)
                == PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(TAG, "Permission is granted")
                return true
            } else {
                Log.d(TAG, "Permission is revoked")
                ActivityCompat.requestPermissions(
                    this@AviaMainActivityK,
                    arrayOf(Manifest.permission.INTERNET),
                    1
                )
            }
        } else {
        }
        return false
    }

    fun setId() {
        listCity1 = findViewById(R.id.ListCity1)
        listCity2 = findViewById(R.id.ListCity2)
        cityInput1 = findViewById(R.id.CityInput1)
        cityInput2 = findViewById(R.id.CityInput2)
        imgbtn1 = findViewById(R.id.Cross1)
        imgbtn2 = findViewById(R.id.Cross2)
        dateinput = findViewById(R.id.DateInput)
        nearestAirport = findViewById(R.id.GPS_location)
        txtTicketOut = findViewById(R.id.txtTicketOut)
        save = findViewById(R.id.save)
        goToSpisok = findViewById(R.id.goToSpisok)
        wantvalue = findViewById(R.id.wantval)
        btn_to_trip = findViewById(R.id.btn_to_trip)
    }

    fun setInitialValue() {
        showTicketOut(false)
        mSettings = getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE)
        dateAndTime = Calendar.getInstance()
        btn_to_trip!!.setOnClickListener {
            val intent_to_trip = Intent(this@AviaMainActivityK, TripMainActivityK::class.java)
            startActivity(intent_to_trip)
        }
        listCity2!!.adapter = null
        listCity2!!.visibility = View.GONE
        listCity2!!.elevation = 0 * applicationContext.resources.displayMetrics.density
        listCity1!!.adapter = null
        listCity1!!.visibility = View.GONE
        listCity1!!.elevation = 0 * applicationContext.resources.displayMetrics.density
    }

    override fun onClick(view: View) {
        if (view.id == findViewById<View>(R.id.Cross1).id) {
            CityFrom = false
            showTicketOut(false)
            Log.d(TAG, "Click Cross1")
            cityInput2!!.isEnabled = false
            cityInput1!!.isEnabled = true
            cityInput1!!.setText("")
            listCity1!!.adapter = null
            listCity1!!.visibility = View.GONE
            findViewById<View>(R.id.btn_to_trip).visibility = View.VISIBLE
            findViewById<View>(R.id.btn_logout).visibility = View.VISIBLE
            listCity1!!.elevation = 0 * applicationContext.resources.displayMetrics.density
        }
        if (view.id == findViewById<View>(R.id.Cross2).id) {
            CityTo = false
            showTicketOut(false)
            Log.d(TAG, "Click Cross2")
            cityInput1!!.isEnabled = false
            cityInput2!!.isEnabled = true
            cityInput2!!.setText("")
            listCity2!!.adapter = null
            listCity2!!.visibility = View.GONE
            findViewById<View>(R.id.btn_to_trip).visibility = View.VISIBLE
            findViewById<View>(R.id.btn_logout).visibility = View.VISIBLE
            listCity2!!.elevation = 0 * applicationContext.resources.displayMetrics.density
        }
        if (view.id == findViewById<View>(R.id.btnDataChooser).id) {
            showTicketOut(false)
            Log.d(TAG, "Click DataChooser")
            DateIsSet = false
            DatePickerDialog(
                this@AviaMainActivityK, WorkWithDateK.d,
                dateAndTime!![Calendar.YEAR],
                dateAndTime!![Calendar.MONTH],
                dateAndTime!![Calendar.DAY_OF_MONTH]
            )
                .show()
            DateIsSet = true
        }
        if (view.id == findViewById<View>(R.id.btnFindTicket).id) {
            if (AllFieldsAreSet()) {
                Log.d(TAG, "click find ticket")
                TicketFoundedK.range=0
                TicketFoundedK.toSTROKA=toSTROKA
                TicketFoundedK.fromSTROKA=fromSTROKA
                TicketFoundedK.toIATA=toIATA
                TicketFoundedK.fromIATA=fromIATA
                TicketFoundedK.fromDATE=dateFROM
                TicketFoundedK.wantValue =wantVal!!.toDouble()
                searchTickets()
            }
            if (!AllFieldsAreSet()) {
                Log.d(TAG, "Не все поля введены")
                val toast =
                    Toast.makeText(applicationContext, "Не все поля введены", Toast.LENGTH_SHORT)
                val v = toast.view.findViewById<View>(android.R.id.message) as TextView
                v.setTextColor(Color.RED)
                v.setBackgroundColor(0)
                toast.show()
            }
        }
        if (view.id == findViewById<View>(R.id.btnToHere).id && nearestAirport!!.text.toString().length > 20) {
            CityTo = true
            cityInput2!!.setText(nearestAirport!!.text.toString().substring(20))
            toIATA =
                nearestAirport!!.text.toString().substring(20).split(" ").toTypedArray()[0]
            toSTROKA = nearestAirport!!.text.toString().substring(20)
            listCity2!!.adapter = null
            cityInput2!!.isEnabled = false
            cityInput2!!.textSize = 18f
        }
        if (view.id == findViewById<View>(R.id.btnFromHere).id && nearestAirport!!.text.toString().length > 20) {
            CityFrom = true
            cityInput1!!.setText(nearestAirport!!.text.toString().substring(20))
            fromIATA =
                nearestAirport!!.text.toString().substring(20).split(" ").toTypedArray()[0]
            fromSTROKA = nearestAirport!!.text.toString().substring(20)
            listCity1!!.adapter = null
            cityInput1!!.isEnabled = false
            cityInput1!!.textSize = 18f
        }
        if (view.id == findViewById<View>(R.id.save).id) {
            write()
        }
        if (view.id == findViewById<View>(R.id.goToSpisok).id) {
            runSecondActivity()
        }
        if (view.id == findViewById<View>(R.id.btnWantVal).id) {
            dialog()
        }
        if (view.id == findViewById<View>(R.id.btn_logout).id) {
            val editor = mSettings!!.edit()
            editor.remove(PREFERENCES_LOGIN)
            editor.remove(PREFERENCES_PASSWORD)
            editor.apply()
            val intent = Intent(applicationContext, AuthActivityK::class.java)
            startActivity(intent)
        }
    }

    fun searchTickets() {
        val builder = Retrofit.Builder()
            .baseUrl("https://lyssa.aviasales.ru/")
            .addConverterFactory(GsonConverterFactory.create())
        var retrofit  = builder.build()
        val ticketsClient: TicketClient = retrofit.create(TicketClient::class.java)
        var call = ticketsClient.reposForTickets(
            TicketFoundedK.fromIATA.toString(),
            TicketFoundedK.toIATA.toString(), TicketFoundedK.fromDATE.toString(), TicketFoundedK.currency,
            TicketFoundedK.range.toString(), "false"
        )
        call.enqueue(object : Callback<TicketRO> {
            override fun onResponse(call: Call<TicketRO>, response: Response<TicketRO>) {
                    if ((response != null) && (response.body() != null) && (response.body()!!.prices.size > 0)) {
                    TicketFoundedK.value=response.body()!!.prices[0].value
                    TicketFoundedK.gate=response.body()!!.prices[0].gate
                    TicketFoundedK.wantValue=wantVal!!.toDouble()
                    txtTicketOut!!.text = TicketFoundedK.toString()
                    showTicketOut(true)
                } else {
                    val toast = Toast.makeText(
                        applicationContext,
                        "Билетов не найдено!",
                        Toast.LENGTH_SHORT
                    )
                    val v = toast.view.findViewById<View>(android.R.id.message) as TextView
                    v.setTextColor(Color.RED)
                    v.setBackgroundColor(0)
                    toast.show()
                }
            }

            override fun onFailure(call: Call<TicketRO>, t: Throwable) {
                Log.d(TAG, "onFailure: " + call.request())
                Log.d(TAG, "Exeption!!!! " + t.localizedMessage)
            }
        })
    }

    fun AllFieldsAreSet(): Boolean {
        return CityFrom && CityTo && DateIsSet && WantWalSsSet
    }

    fun showTicketOut(show: Boolean) {
        if (show) {
            findViewById<View>(R.id.LLticketOut).visibility = View.VISIBLE
            save!!.visibility = View.VISIBLE
        } else {
            findViewById<View>(R.id.LLticketOut).visibility = View.INVISIBLE
            save!!.visibility = View.GONE
        }
    }

    fun runSecondActivity() {
        val intent = Intent(applicationContext, TicketListActivityK::class.java)
        startActivity(intent)
    }

    fun write() {
        TicketFoundedK.toBase()
    }

    fun dialog() {
        val alert = AlertDialog.Builder(this)
        alert.setTitle("Укажите желаему цену:")
        alert.setMessage("При достижении данной цены вы будете уведомлены")
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER
        alert.setView(input)
        alert.setPositiveButton(
            "Ok"
        ) { dialog, whichButton ->
            val temporary = input.text.toString()
            wantVal = temporary
            wantvalue!!.text = temporary
            WantWalSsSet = true
        }
        alert.setNegativeButton(
            "Cancel"
        ) { dialog, whichButton ->
            // Canceled.
        }
        alert.show()
    }

    companion object {
        var TAG = "HTTP"
        var fromIATA: String? = null
        var toIATA: String? = null
        var fromSTROKA: String? = null
        var toSTROKA: String? = null
        var dateFROM: String? = null
        var wantVal: String? = null
        var CityFrom = false
        var CityTo = false
        var DateIsSet = false
        var WantWalSsSet = false
        var dateAndTime: Calendar? = null
        var dbHelper: DBHelperK? = null
        var dateinput: TextView? = null
        var nearestAirport: TextView? = null
        const val PREFERENCES_LOGIN = "LOGIN"
        const val PREFERENCES_PASSWORD = "PASSWORD"
        const val PREFERENCES_FILE = "mysettings"
    }
}