package vcnmb.hannah.currencyconverter

import android.util.Log
import android.os.Handler
import android.os.Looper
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.util.concurrent.Executors

class ConverterHandler {

    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())

    //method to pull all counties avalible

    private fun getCountryByName(countryName: String, callback: (Country?) -> Unit){
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
                                val countryJsonArrays: Array<CountryJson> = Gson().fromJson(countryJson, Array<CountryJson>::class.java)
                                val countryFromJson = countryJsonArrays.first()

//                                val countryName = countryFromJson.name.common
//                                val currencyName = countryFromJson.currencies.values.firstOrNull()?.name

                                val name = countryFromJson?.name?.common
                                val currency = getCurrencyFromCountry(countryFromJson!!)

                                if (name != null && currency != null) {
                                    val country = Country(name, currency)
                                    callback(country)
                                } else {
                                    callback(null)
                                }

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


    private fun getCurrencyFromCountry(countryJson: CountryJson): String?{
        val currencyName = countryJson.currencies.values.firstOrNull()?.name
        return(currencyName)
    }
}