package com.example.eilidh.a1513195_coursework

import android.arch.persistence.db.SimpleSQLiteQuery
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_search_weather.*
import javax.security.auth.callback.Callback
import java.lang.IllegalStateException

class SearchWeather : AppCompatActivity(), Callback, AdapterView.OnItemSelectedListener  {

    /*
    Functions for the 'Search Weather' activity - returning custom data from the weather database.
     */

    lateinit var fb: FillDatabase
    private var mDb: WeatherDatabase? = null
    var prefs: UserPreferences? = null
    private var mDbWorkerThread: DbWorkerThread = DbWorkerThread("dbWorkerThread")

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        prefs = UserPreferences(this)
        mDb = WeatherDatabase.getInstance(this)
        mDbWorkerThread = DbWorkerThread("dbWorkerThreadSearch")
        mDbWorkerThread.start()
        fb = FillDatabase(mDb!!, mDbWorkerThread, prefs!!, this@SearchWeather, applicationContext)

        setContentView(R.layout.activity_search_weather)

        // create spinner of values
        val spinner: Spinner = findViewById(R.id.search_spinner)
        ArrayAdapter.createFromResource(
                this,
                R.array.search_weather_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = this
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        // update UI to reflect selected spinner value
        val selection = parent.getItemAtPosition(pos)
        val units = this.findViewById<TextView>(R.id.search_units)
        val degree = Typography.degree

        when (selection) {
            "Temperature" -> units.setText(degree + "c")
            "Humidity" -> units.setText("/1")
            "Pressure" -> units.setText("mBar")
            "Windspeed" -> units.setText("M/ph")
            "UV Index" -> units.setText("UV")
            "Visibility" -> units.setText("/10")
            else -> {
                units.setText("")
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // do nothing
    }

    fun displaySearchResults(view: View) {
        var operatorString = ""
        try {
            if (checkRadioOptionSelected()) {
                var attribute = search_spinner.selectedItem.toString()
                attribute = getDatabaseAttribute(attribute)

                val operator = this.findViewById<RadioGroup>(R.id.RGroup).checkedRadioButtonId
                val value = this.findViewById<EditText>(R.id.search_value).text.toString().toDouble()
                val emptyWeather = WeatherData()
                var searchData: List<WeatherData> = listOf(emptyWeather)
                val task = kotlinx.coroutines.Runnable {
                    when (operator) {
                        R.id.radio_less -> {
                            operatorString = "<"
                        }
                        R.id.radio_equal -> {
                            operatorString = "="
                        }
                        R.id.radio_greater -> {
                            operatorString = ">"
                        }
                        else -> {
                            searchData = listOf(emptyWeather)
                        }
                    }
                    // create raw search query with parameters
                    val query = SimpleSQLiteQuery(
                            "SELECT place_string, time, uid, hour, $attribute FROM weatherData WHERE $attribute $operatorString $value")
                    // do the search
                    searchData = mDb!!.weatherDao().getSearchQuery(query)
                    // display results to the user
                    showSearchResults(searchData, attribute, operatorString, value)
                }
                mDbWorkerThread.postTask(task)
            } else {
                displayNoCheckedRadioOptionError()
            }
        } catch (e: IllegalStateException) {
            showInvalidInputError()
        }
    }

    fun checkRadioOptionSelected(): Boolean {
        // return false if user hasn't selected any radio buttons
        val lessThan = this.findViewById<RadioButton>(R.id.radio_less)
        val equalTo = this.findViewById<RadioButton>(R.id.radio_equal)
        val greaterThan = this.findViewById<RadioButton>(R.id.radio_greater)
        return (lessThan.isChecked || equalTo.isChecked || greaterThan.isChecked )
    }

    fun displayNoCheckedRadioOptionError() {
        val builder = AlertDialog.Builder(this@SearchWeather)
        with(builder)
        {
            setTitle("Search Parameter Error")
            setMessage("Please select a value for 'less than', 'equal to' or 'greater than', and try again.")
            setPositiveButton("OK") { dialog, which -> null }
            show()
        }
    }

    fun getDatabaseAttribute(att: String): String {
        // convert spinner value to database field name
        var str = ""
        when (att) {
            "Temperature" -> str = "temperature"
            "Humidity" -> str = "humidity"
            "Pressure" -> str = "pressure"
            "Windspeed" -> str = "windspeed"
            "UV Index" -> str = "uvIndex"
            "Visibility" -> str = "visibility"
            else -> {
                str = ""
            }
        }
        return str
    }

    fun showSearchResults(data: List<WeatherData>, attribute: String, operator: String, value: Double) {
        // display results to user
        if (data!!.isEmpty()) {
            this@SearchWeather.runOnUiThread(
                object : Runnable {
                    override fun run() {
                        showNoResultsError()
                    }
                }
            )
        } else {
            val builder = AlertDialog.Builder(this@SearchWeather)
            with(builder)
            {
                setTitle("Search Results")
                val sb = StringBuilder()
                sb.append(attribute + " " + operator + " " + value.toString() + ": \n\n")
                var data = data.sortedBy { it.time }
                // for each data result, add it to the output
                for (d in data) {
                    var time = fb.getHourString(d.time!!)
                    sb.append(d.placeString + ": time: " + time + "\n")
                }
                setMessage(sb.toString())
                setPositiveButton("OK") { dialog, which -> null }
                show()
                //  Math.round(temp)
            }
        }
    }

    fun showNoResultsError() {
        // display answer for no results returned
        val builder = AlertDialog.Builder(this@SearchWeather)
        with(builder)
        {
            setTitle("Search Results")
            val str = "No results found for your query!"
            setMessage(str)
            setPositiveButton("OK") { dialog, which -> null }
            show()
        }
    }

    fun showInvalidInputError() {
        val builder = AlertDialog.Builder(this@SearchWeather)
        with(builder)
        {
            setTitle("Search Error")
            val str = "Invalid input for your value - numbers accepted only."
            setMessage(str)
            setPositiveButton("OK") { dialog, which -> null }
            show()
        }
    }
}
