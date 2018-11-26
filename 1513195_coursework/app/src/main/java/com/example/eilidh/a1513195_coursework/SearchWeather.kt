package com.example.eilidh.a1513195_coursework

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_search_weather.*
import javax.security.auth.callback.Callback
import com.android.volley.Response

class SearchWeather : AppCompatActivity(), Callback, AdapterView.OnItemSelectedListener  {

    lateinit var fb: FillDatabase
    private var mDb: WeatherDatabase? = null
    private var mDbWorkerThread: DbWorkerThread = DbWorkerThread("dbWorkerThread")

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        Log.i("debug", "Opening Search Weather Activity")
        mDb = WeatherDatabase.getInstance(this)
        mDbWorkerThread = DbWorkerThread("dbWorkerThreadSearch")
        mDbWorkerThread.start()

        setContentView(R.layout.activity_search_weather)

        val spinner: Spinner = findViewById(R.id.search_spinner)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
                this,
                R.array.search_weather_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = this

        //setContentView(R.layout.user_preferences)

    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        val selection = parent.getItemAtPosition(pos)
        val units = this.findViewById<TextView>(R.id.search_units)
        val degree = Typography.degree

        when (selection) {
            "Temperature" -> units.setText(degree + "c")
            "Humidity" -> units.setText("%")
            "Pressure" -> units.setText("mBar")
            "Windspeed" -> units.setText("M/ph")
            "UV Index" -> units.setText("UV")
            "Visibility" -> units.setText("%")
            else -> {
                units.setText("")
            }
        }
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }

    fun displaySearchResults(view: View) {

        if(checkRadioOptionSelected()) {
          //  val task = Runnable {
                var attribute = search_spinner.selectedItem.toString()
                attribute = getDatabaseAttribute(attribute)
                val operator = this.findViewById<RadioGroup>(R.id.RGroup).checkedRadioButtonId
                val value = this.findViewById<EditText>(R.id.search_value).text.toString().toFloat()

                val emptyWeather = WeatherData()
                Log.i("debug", "operator: $operator")
                var searchData: List<WeatherData> = listOf(emptyWeather)
                when (operator) {
                    R.id.radio_less -> {
                        val task = kotlinx.coroutines.Runnable {
                            Log.i("debug", "calling less than with $attribute and $value")

                            searchData = mDb!!.weatherDao().getSearchDataLessThan(attribute, value)
                            Log.i("debug", searchData.toString())
                            showSearchResults(searchData)

                        }
                        mDbWorkerThread.postTask(task)
                    }
                    R.id.radio_equal -> {
                        val task = kotlinx.coroutines.Runnable {
                            Log.i("debug", "calling equal to with $attribute and $value")

                            searchData = mDb!!.weatherDao().getSearchDataEqualTo(attribute, value)
                            Log.i("debug", searchData.toString())
                            showSearchResults(searchData)


                        }
                        mDbWorkerThread.postTask(task)
                    }
                    R.id.radio_greater -> {
                        val task = kotlinx.coroutines.Runnable {
                            Log.i("debug", "calling greater than with $attribute and $value")
                            searchData = mDb!!.weatherDao().getSearchDataGreaterThan(attribute, value)
                            showSearchResults(searchData)


                        }
                        mDbWorkerThread.postTask(task)
                        Log.i("debug", searchData.toString())

                    }
                    else -> {
                        searchData = listOf(emptyWeather)
                    }
                }
                Log.i("debug", searchData.toString())
          //  } mDbWorkerThread.postTask(task)
        }
        else {
            displayNoCheckedRadioOptionError()
        }


    }

    fun checkRadioOptionSelected(): Boolean {
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

    fun showSearchResults(data: List<WeatherData>) {
        val builder = AlertDialog.Builder(this@SearchWeather)

        with(builder)
        {
            setTitle("Search Results")
            val str = "Resulty"
            setMessage(str)

            setPositiveButton("OK") { dialog, which -> null }
            show()
        }
    }


}
