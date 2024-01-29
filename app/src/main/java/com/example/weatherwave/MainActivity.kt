package com.example.weatherwave

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherwave.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchWeatherData("Nagpur")
        searchCity()

        binding.lottieAnimationView.setFailureListener {
            Toast.makeText(this@MainActivity,it.message.toString(),Toast.LENGTH_SHORT).show()
            Log.d("TAG","OnResponse: $it.message.toString()")
        }
    }

    private fun searchCity() {
        val searchView= binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName:String){
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(WeatherApi::class.java)

        val response = retrofit.getWeatherData(cityName,"d5b858a759e34d068f11f69414cc2e08","metric")
        response.enqueue(object  :Callback<WeatherData>{
            override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                val responseBody = response.body()

                if(response.isSuccessful && responseBody != null){
                    val temperature= responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val conditiion = responseBody.weather.firstOrNull()?.main?: "Unknown"
                    val maxtemp = responseBody.main.tempMax
                    val mintemp = responseBody.main.tempMin
//                    Log.d("TAG", "OnResponse: $temperature")
                    binding.txtCityName.text = "$cityName"
                    binding.txtTemprature.text = "$temperature °C"
                    binding.txtHumidity.text = "$humidity %"
                    binding.txtWindSpeed.text ="$windSpeed m/s"
                    binding.txtSunrise.text = "${time(sunRise)}"
                    binding.txtSunset.text ="${time(sunSet)}"
                    binding.txtSea.text =  "$seaLevel hPa"
                    binding.txtCondition.text = "$conditiion"
                    binding.txtWeatherCondition.text = "$conditiion"
                    binding.txtMInTemp.text ="Min Temp: $mintemp °C"
                    binding.txtMaxTemp.text = "Max Temp: $maxtemp °C"
                    binding.txtDay.text =dayName(System.currentTimeMillis())
                    binding.txtDate.text = dates()

                    changeImageOnCondition(conditiion)

                }
            }

            override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                Toast.makeText(this@MainActivity,t.message.toString(),Toast.LENGTH_SHORT).show()
            }

        })

    }
    fun changeImageOnCondition(conditions: String){
        when(conditions){
            "Clear sky","Sunny","Clear" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Partly Clouds","Clouds","OverCast","Mist","Foggy"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light Snow","Moderate Snow", "Heavy Snow", "Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }

        binding.lottieAnimationView.playAnimation()


    }
    fun time(timeStamp : Long): String {
        val sdf = SimpleDateFormat("HH:MM", Locale.getDefault())
        return  sdf.format(Date(timeStamp*1000))
    }
    fun dates(): CharSequence?{
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return  sdf.format(Date())
    }

    fun dayName(timeStamp : Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return  sdf.format(Date())
    }

}