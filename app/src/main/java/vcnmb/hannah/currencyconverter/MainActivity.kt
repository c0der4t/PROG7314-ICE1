package vcnmb.hannah.currencyconverter

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var converterHandler: ConverterHandler
    private lateinit var actvSrcCountry: AutoCompleteTextView
    private lateinit var actvDestCountry: AutoCompleteTextView
    private lateinit var edtSourceAmount: EditText
    private lateinit var edtDestAmount: EditText
    private lateinit var btnSwap: Button
    private lateinit var btnConvert: Button

    private lateinit var countries: List<Country>
    private lateinit var countryNames: List<String>

    private lateinit var txtSourceCurrencyCode: TextView
    private lateinit var txtDestCurrencyCode: TextView


    private lateinit var txtSourceCountryErrorMessage: TextView
    private lateinit var txtDestCountryErrorMessage: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        converterHandler = ConverterHandler()

        // Bind Views
        actvSrcCountry = findViewById(R.id.actvSrcCountry)
        actvDestCountry = findViewById(R.id.actvDestCountry)
        edtSourceAmount = findViewById(R.id.edtSourceAmount)
        edtDestAmount = findViewById(R.id.edtDestAmount)
        btnSwap = findViewById(R.id.btnSwap)

        btnConvert = findViewById(R.id.btnConvert)

        txtSourceCountryErrorMessage = findViewById(R.id.txtSourceCountryErrorMessage)
        txtDestCountryErrorMessage = findViewById(R.id.txtDestCountryMessage)

        txtSourceCurrencyCode = findViewById(R.id.txtSrcCurrencyCode)
        txtDestCurrencyCode = findViewById(R.id.txtDestCurrencyCode)


        // Fetch countries
        converterHandler.getAllCountries { list ->
            countries = list.sortedBy { it.CountryName }
            countryNames = countries.map { it.CountryName }

            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, countryNames)
            actvSrcCountry.setAdapter(adapter)
            actvDestCountry.setAdapter(adapter)
        }

        // Convert on text entry or swap
        val triggerConversion = {
            val fromName = actvSrcCountry.text.toString().trim()
            val toName = actvDestCountry.text.toString().trim()
            val amount = edtSourceAmount.text.toString().toDoubleOrNull()

            txtSourceCountryErrorMessage.visibility = View.GONE
            txtDestCountryErrorMessage.visibility = View.GONE

            if (fromName.isNotEmpty()) {
                getCountryByName(fromName) { country ->
                    if (country != null) {
                        txtSourceCurrencyCode.text = country.CurrencyName
                    } else {
                        txtSourceCurrencyCode.text = "N/A"
                        txtSourceCountryErrorMessage.visibility = View.VISIBLE
                    }
                }
            }

            if (toName.isNotEmpty()) {
                getCountryByName(toName) { country ->
                    if (country != null) {
                        txtDestCurrencyCode.text = country.CurrencyName
                    } else {
                        txtDestCurrencyCode.text = "N/A"
                        txtDestCountryErrorMessage.visibility = View.VISIBLE
                    }
                }
            }

            if (fromName.isNotEmpty() && toName.isNotEmpty() && amount != null) {
                convertBetweenTwoCountries(fromName, toName, amount) { result ->
                    edtDestAmount.setText(result?.toString() ?: "Conversion failed")
                }
            }
        }


        // Listeners
        edtSourceAmount.setOnEditorActionListener { _, _, _ ->
            triggerConversion()
            true
        }

        actvSrcCountry.setOnItemClickListener { _, _, _, _ -> triggerConversion() }
        actvDestCountry.setOnItemClickListener { _, _, _, _ -> triggerConversion() }

        btnSwap.setOnClickListener {
            val temp = actvSrcCountry.text.toString()
            actvSrcCountry.setText(actvDestCountry.text.toString())
            actvDestCountry.setText(temp)
            triggerConversion()
        }

        btnConvert.setOnClickListener {
            val fromName = actvSrcCountry.text.toString().trim()
            val toName = actvDestCountry.text.toString().trim()
            val amountText = edtSourceAmount.text.toString()

            val amount = amountText.toDoubleOrNull()
            txtSourceCountryErrorMessage.visibility = View.GONE
            txtDestCountryErrorMessage.visibility = View.GONE

            if (fromName.isNotEmpty() && toName.isNotEmpty() && amount != null) {
                convertBetweenTwoCountries(fromName, toName, amount) { result ->
                    edtDestAmount.setText(result?.toString() ?: "Conversion failed")
                }
            }

            if (fromName.isNotEmpty()) {
                getCountryByName(fromName) { country ->
                    if (country != null) {
                        txtSourceCurrencyCode.text = country.CurrencyName
                    } else {
                        txtSourceCurrencyCode.text = "N/A"
                        txtSourceCountryErrorMessage.visibility = View.VISIBLE
                    }
                }
            }

            if (toName.isNotEmpty()) {
                getCountryByName(toName) { country ->
                    if (country != null) {
                        txtDestCurrencyCode.text = country.CurrencyName
                    } else {
                        txtDestCurrencyCode.text = "N/A"
                        txtDestCountryErrorMessage.visibility = View.VISIBLE
                    }
                }
            }
        }
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
        converterHandler.convertCurrency(fromCountry, toCountry, amount) { result ->
            if (result != null) {
                Log.d("TestConvertCurrency", "$amount ${fromCountry.CurrencyName} = $result ${toCountry.CurrencyName}")
                callback(result)
            } else {
                Log.e("TestConvertCurrency", "Currency conversion failed.")
                callback(null)
            }
        }
    }

    private fun convertBetweenTwoCountries(fromCountryName: String, toCountryName: String, amountToConvert: Double, callback: (Double?) -> Unit) {
        // Hide previous errors
        txtSourceCountryErrorMessage.visibility = View.GONE
        txtDestCountryErrorMessage.visibility = View.GONE

        getCountryByName(fromCountryName) { countryOne ->
            if (countryOne == null) {
                txtSourceCountryErrorMessage.visibility = View.VISIBLE
                callback(null)
                return@getCountryByName
            }

            getCountryByName(toCountryName) { countryTwo ->
                if (countryTwo == null) {
                    txtDestCountryErrorMessage.visibility = View.VISIBLE
                    callback(null)
                    return@getCountryByName
                }

                convertCurrency(countryOne, countryTwo, amountToConvert) { result ->
                    callback(result)
                }
            }
        }
    }
}
