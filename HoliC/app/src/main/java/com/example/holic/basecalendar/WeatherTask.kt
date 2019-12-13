package com.example.holic.basecalendar

import android.os.AsyncTask
import com.example.holic.R
import org.json.JSONObject
import java.net.URL

class WeatherTask() : AsyncTask<String, Void, String>() {

    var arrayList = arrayListOf<Int>()
    var baseCalendar = BaseCalendar()

    override fun doInBackground(vararg params: String?): String? {
        var response: String?
        try {
            response =
                URL("https://api.openweathermap.org/data/2.5/forecast?q=seoul&units=metric&appid=d0433b4805b4fa4baeed565de879f3c1").readText(
                    Charsets.UTF_8
                )

        } catch (e: Exception) {
            response = null
        }

        val jsonObj = JSONObject(response)
        val jArray =
            jsonObj.getJSONArray("list")//.getJSONObject(0).getJSONArray("weather").getJSONObject(0)
        for (i in 0 until jArray.length() step 8) {
            val description = jArray.getJSONObject(i).getJSONArray("weather").getJSONObject(0)
                .getString("description")
            val icon = jArray.getJSONObject(i).getJSONArray("weather").getJSONObject(0)
                .getString("icon")
//                Log.d("heo", i.toString() + " : " + icon)
//                Log.d("heo", i.toString() + " : " + description)

            var setIconText: Int = 0
            if (icon.equals("01d") || icon.equals("01n"))
                setIconText = R.drawable.d01
            else if (icon.equals("02d") || icon.equals("02n"))
                setIconText = R.drawable.d02
            else if (icon.equals("03d") || icon.equals("03n"))
                setIconText = R.drawable.d03
            else if (icon.equals("04d") || icon.equals("04n"))
                setIconText = R.drawable.d03
            else if (icon.equals("09d") || icon.equals("09n"))
                setIconText = R.drawable.d09
            else if (icon.equals("10d") || icon.equals("10n"))
                setIconText = R.drawable.d09
            else if (icon.equals("11d") || icon.equals("11n"))
                setIconText = R.drawable.d11
            else if (icon.equals("13d") || icon.equals("13n"))
                setIconText = R.drawable.d13
            else if (icon.equals("50d") || icon.equals("50n"))
                setIconText = R.drawable.d50

            arrayList.add(setIconText)
        }
        //Log.d("heo",arrayList.toString())
        return arrayList.toString()
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        try {

        } catch (e: Exception) {
        }
    }
}
