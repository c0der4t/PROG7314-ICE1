package vcnmb.hannah.currencyconverter

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var converterHandler: ConverterHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize converterHandler
        converterHandler = ConverterHandler()

    }

    private fun getCountryByName(countryName: String, callback: (Country?) -> Unit) {
        converterHandler.getCountryByName(countryName) { country ->
            if (country != null) {
                callback(country)
                Log.d("TestGetCountryByName", "Found Country: ${country.CountryName}, Currency: ${country.CurrencyName}")
            } else {
                Log.e("TestGetCountryByName", "Country '$countryName' not found or missing currency.")
                callback(null)
            }
        }
    }


    private fun convertCurrency(fromCountry: Country, toCountry: Country, amount: Double, callback: (Double?) -> Unit) {
                if (fromCountry != null && toCountry != null) {
                    converterHandler.convertCurrency(fromCountry, toCountry, amount) { result ->
                        if (result != null) {
                            Log.d("TestConvertCurrency", "$amount ${fromCountry.CurrencyName} = $result ${toCountry.CurrencyName}")
                            callback(result)
                        } else {
                            Log.e("TestConvertCurrency", "Currency conversion failed.")
                            callback(result)
                        }
                    }
                } else {
                    Log.e("TestConvertCurrency", "Could not retrieve one or both countries.")
                    callback(null)
                }
    }

    private fun convertBetweenTwoCountries(fromCountryName: String, toCountryName: String, amountToConvert: Double, callback: (Double?) -> Unit) {

        //methods have to be called inside eachother as they are asychenouse
        
        //get first country from name
        getCountryByName(fromCountryName) { countryOne ->
            if (countryOne != null) {
                Log.d("GetCountryByName", "Country-One found: ${countryOne.CountryName}")

                //get second country from name
                getCountryByName(toCountryName) { countryTwo ->
                    if (countryTwo != null) {
                        Log.d("GetCountryByName", "Country-Two found: ${countryTwo.CountryName}")

                        // Convert amounts
                        convertCurrency(countryOne, countryTwo, amountToConvert) { convertedAmount ->
                            if (convertedAmount != null) {
                                Log.d(
                                    "FinalResult",
                                    "$amountToConvert ${countryOne.CurrencyName} = $convertedAmount ${countryTwo.CurrencyName}"
                                )
                                callback(convertedAmount)
                            } else {
                                Log.e("FinalResult", "Conversion failed.")
                                callback(null)
                            }
                        }

                    } else {
                        Log.e("GetCountryByName", "Country-Two not found.")
                        callback(null)
                    }
                }

            } else {
                Log.e("GetCountryByName", "Country-One not found.")
                callback(null)
            }
        }
    }

}



