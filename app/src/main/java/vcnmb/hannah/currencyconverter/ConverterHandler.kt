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
    private val APIkey = "duzteRjibGK9YxwXRHTeWG8u9dvX7R"

    //method to pull all counties available
    fun getAllCountries(callback: (List<Country>) -> Unit) {
        val url = "https://restcountries.com/v3.1/all"

        executor.execute {
            url.httpGet().responseString { _, response, result ->
                handler.post {
                    if (response.statusCode != 200) {
                        callback(emptyList())
                        return@post
                    }

                    when (result) {
                        is Result.Success -> {
                            try {
                                val json = result.get()
                                val countryJsonArray: Array<CountryJson> =
                                    Gson().fromJson(json, Array<CountryJson>::class.java)

                                val countryList = countryJsonArray.mapNotNull { countryJson ->
                                    val name = countryJson.name.common
                                    val currencyName = countryJson.currencies.values.firstOrNull()?.name
                                    if (name != null && currencyName != null) {
                                        Country(name, currencyName)
                                    } else null
                                }

                                callback(countryList)

                            } catch (e: JsonSyntaxException) {
                                Log.e("GetAllCountries", "JSON parsing error: ${e.message}")
                                callback(emptyList())
                            }
                        }

                        is Result.Failure -> {
                            val ex = result.getException()
                            Log.e("GetAllCountries", "API Error: ${ex.message}")
                            callback(emptyList())
                        }
                    }
                }
            }
        }
    }

// method to get c by country name

    fun getCountryByName(countryName: String, callback: (Country?) -> Unit){
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

    //returns the currency code of the country
    private fun getCurrencyFromCountry(country: CountryJson): String?{
        return country.currencies.keys.firstOrNull()
    }


    //convert currencies
    //takes in two country objects and returns the converted rate from the first country to the second

    fun convertCurrency(countryOne: Country, countryTwo: Country, amount: Double, callback: (Double?) -> Unit) {
        val url = "https://www.amdoren.com/api/currency.php?api_key=$APIkey&from=${countryOne.CurrencyName}&to=${countryTwo.CurrencyName}&amount=$amount"

        executor.execute {
            url.httpGet().responseString { _, response, result ->
                handler.post {
                    if (response.statusCode == 404) {
                        callback(null)
                        return@post
                    }

                    when (result) {
                        is Result.Success -> {
                            try {
                                val currencyJson = result.get()
                                val currencyResponse: CurrencyJson = Gson().fromJson(currencyJson, CurrencyJson::class.java)

                                if (currencyResponse.error == 0) {
                                    val value = currencyResponse.amount
                                    callback(value.toInt())
                                } else {
                                    Log.e("ConvertCurrency", "API error: ${currencyResponse.error_message}")
                                    callback(null)
                                }
                            } catch (e: JsonSyntaxException) {
                                Log.e("ConvertCurrency", "JSON parsing error: ${e.message}")
                                callback(null)
                            }
                        }
                        is Result.Failure -> {
                            val ex = result.getException()
                            Log.e("ConvertCurrency", "API Error: ${ex.message}")
                            callback(null)
                        }
                    }
                }
            }
        }
    }




}