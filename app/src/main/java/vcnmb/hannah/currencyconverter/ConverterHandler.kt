package vcnmb.hannah.currencyconverter

import android.util.Log
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.util.concurrent.Executors

class ConverterHandler {

    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())

    private fun getCountryByName(countryName: String){
        val url = "https://restcountries.com/v3.1/name/$countryName"

        executor.execute{
            url.httpGet().responseString(){_,response, result ->
                handler.post{
                    if(response.statusCode ==404){
                        return@post
                    }
                    when(result){
                        is Result.Success ->{
                            try{
                                val countryJson = result.get()
                                val countryArray: Array<Country> = Gson().fromJson(countryJson, Array<Country>::class.java)
                                val country = countryArray.first()

                                getCurrencyFromCountry(country)

                            } catch (e: JsonSyntaxException){
                                Log.e("GetCurrencyByCountryName", "JSON parsing error: ${e.message}")

                            }
                        }
                        is Result.Failure ->{
                            val ex = result.getException()
                            Log.e("GetCurrencyByCountryName", "API Error: ${ex.message}")

                        }
                    }
                }
            }
        }
    }


    private fun getCurrencyFromCountry(country: Country): String?{
        val currencyName = country.currencies.values.firstOrNull()?.name
        return(currencyName)
    }
}