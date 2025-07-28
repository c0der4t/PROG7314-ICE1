package vcnmb.hannah.currencyconverter

//data class Country(
//    val countryName : String,
//    val currencyName : String
//)

data class CountryJson(
    val name: Name,
    val tld: List<String>,
    val cca2: String,
    val ccn3: String,
    val cioc: String?,
    val independent: Boolean,
    val status: String,
    val unMember: Boolean,
    val currencies: Map<String, Currency>,
    val idd: IDD,
    val capital: List<String>,
    val altSpellings: List<String>,
    val region: String,
    val subregion: String,
    val languages: Map<String, String>,
    val latlng: List<Double>,
    val landlocked: Boolean,
    val area: Double,
    val demonyms: Map<String, Demonym>,
    val cca3: String,
    val translations: Map<String, Translation>,
    val flag: String,
    val maps: Maps,
    val population: Long,
    val gini: Map<String, Double>?,
    val fifa: String?,
    val car: Car,
    val timezones: List<String>,
    val continents: List<String>,
    val flags: ImageInfo,
    val coatOfArms: ImageInfo,
    val startOfWeek: String,
    val capitalInfo: CapitalInfo,
    val postalCode: PostalCode
)

data class Name(
    val common: String,
    val official: String,
    val nativeName: Map<String, Translation>
)

data class Currency(
    val symbol: String,
    val name: String
)

data class IDD(
    val root: String,
    val suffixes: List<String>
)

data class Demonym(
    val f: String,
    val m: String
)

data class Translation(
    val official: String,
    val common: String
)

data class Maps(
    val googleMaps: String,
    val openStreetMaps: String
)

data class Car(
    val signs: List<String>,
    val side: String
)

data class ImageInfo(
    val png: String,
    val svg: String,
    val alt: String? = null // Some don't have "alt"
)

data class CapitalInfo(
    val latlng: List<Double>
)

data class PostalCode(
    val format: String,
    val regex: String
)


data class Country(
    val CountryName: String,
    val CurrencyName: String
)

