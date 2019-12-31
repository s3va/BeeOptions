package tk.kvakva.beeoptions

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import com.squareup.moshi.JsonClass

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.net.SocketTimeoutException
import java.net.UnknownHostException


const val SECRETPREFFFN = "secret_shared_prefs"

const val SAVETOPREF = "SavetoPref"
const val BEEPASS = "BeePass"
const val BEEUSER = "BeeUser"
const val BEETOKN = "BeeTokn"

// https://my.beeline.ru/api/1.0/auth?login=9012345678&password=XXXXXX
const val BASEURL = "https://my.beeline.ru/api/1.0/"
// https://my.beeline.ru/api/1.0/info/serviceList?ctn=9012345678&token=1234567890ABCDEFGHIJKLMNOPQRSTUV

class PassUserViewModel(application: Application) : AndroidViewModel(application) {

    private var _isPassHere = MutableLiveData<Boolean>()
    val isPassHere: LiveData<Boolean>
        get() = _isPassHere

    private var _showPassUserLayout = MutableLiveData<Boolean>()
    val showPassUserLayout: LiveData<Boolean>
        get() = _showPassUserLayout

    fun setShowPassUserLayout() {
        _showPassUserLayout.value = !(showPassUserLayout.value ?: false)
    }

    var saveToPref = MutableLiveData<Boolean>()

    var beePass = MutableLiveData<String>()

    var beeUser = MutableLiveData<String>()

    var beeTokn = MutableLiveData<String>()

    var data = MutableLiveData<List<BeeOptionsData.Service>>()
    var databu = MutableLiveData<List<BeeOptionsData.Service>>()

    val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    val sharedPreferences = EncryptedSharedPreferences.create(
        SECRETPREFFFN,
        masterKeyAlias,
        application.applicationContext,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    val contxt = application.applicationContext

    init {
        Log.d(
            "M_PassUserViewModel",
            "AndroidViewModel init.  Application.packageName: ${application.packageName}"
        )

        val ex_pass = sharedPreferences.getString(BEEPASS, "").isNullOrEmpty()
        val ex_user = sharedPreferences.getString(BEEUSER, "").isNullOrBlank()
        val ex_tokn = sharedPreferences.getString(BEETOKN, "").isNullOrBlank()
        val ex_satp = sharedPreferences.getBoolean(SAVETOPREF, false)

        _isPassHere.value = !((ex_pass or ex_user) and ex_tokn)
        if (!(isPassHere.value ?: false)) {
            _showPassUserLayout.value = true
        } else {
            if (ex_satp) {
                saveToPref.value = sharedPreferences.getBoolean(SAVETOPREF, true)
                beePass.value = sharedPreferences.getString(BEEPASS, "")
                beeUser.value = sharedPreferences.getString(BEEUSER, "")
                beeTokn.value = sharedPreferences.getString(BEETOKN, "")
            } else {
                sharedPreferences.edit {
                    remove(BEEUSER)
                    remove(BEEPASS)
                    remove(BEETOKN)
                }
                _showPassUserLayout.value = true
            }
        }


        /*       sharedPreferences.edit {
                   putString("qweqwe", "asdasdasd")
                   putString("zxcvzxc", "oiuoiuoiu")
                   commit()
               }*/

    }


    suspend fun getTokenFromBee() {
        if (beeUser.value.isNullOrBlank() or beePass.value.isNullOrBlank()) {
            Log.d(
                "M_PassUserViewModel",
                "beeUser.value.isNullOrBlank() or beePass.value.isNullOrBlank() !!!!!!!!!!!!!!!!!!!"
            )
            return
        }

        val beelineToken: BeelineToken? = try {
            BeelineApi.retrofitService.getBeeToken(beeUser.value!!, beePass.value!!)

        } catch (e: Exception) {
            e.printStackTrace()
            when (e) {
                is SocketTimeoutException ->
                    Toast.makeText(contxt, "NETWORK TIME OUT!!!!", Toast.LENGTH_LONG).show()
                is UnknownHostException ->
                    Toast.makeText(contxt, "UnknownHostException!!!! ", Toast.LENGTH_LONG).show()
            }
            null
        }

        if ((beelineToken?.meta?.status == "OK") and !beelineToken?.token.isNullOrBlank()) {
            Log.d("M_PassUserViewModel", "!!!!!!!!!!!!!!!! TOKENTT ${beelineToken?.token}")

            val beeOptionsData =
                BeelineApi.retrofitService.getBeeOptions(beeUser.value!!, beelineToken?.token!!)

            if (beeOptionsData.meta.status == "OK") {
                data.value = beeOptionsData.services?.toList()
                databu.value = beeOptionsData.services?.toList()
            }
            else
                Toast.makeText(contxt, "Cannot get options from beeline", Toast.LENGTH_LONG).show()

            return
        } else if (beelineToken?.meta?.message == "AUTH_ERROR") {
            Log.d(
                "M_PassUserViewModel",
                " Toast.makeText(contxt,AUTH_ERROR: login (phone number) or password is wrong,Toast.LENGTH_LONG).show()"
            )
            Toast.makeText(
                contxt,
                "AUTH_ERROR: login (phone number) or password is wrong",
                Toast.LENGTH_LONG
            ).show()
        } else if (beelineToken?.meta?.message?.contains("LOGIN_TRY_NUMBER_EXCEED") == true) {
            Log.d(
                "M_PassUserViewModel",
                " Toast.makeText(contxt,AUTH_ERROR: login (phone number) or password is wrong,Toast.LENGTH_LONG).show()"
            )
            Toast.makeText(
                contxt,
                beelineToken.meta.message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun onClinkGetBT() {
        Log.d(
            "M_PassUserViewModel",
            "showPassUserLayout: ${showPassUserLayout.value} | beeUser: ${beeUser.value} | beePass: ${beePass.value} | beeTokn: ${beeTokn.value} | saveToPref: ${saveToPref.value} | isPassHere: ${isPassHere.value}"
        )

        if (saveToPref.value == true)
            sharedPreferences.edit(commit = true) {
                putBoolean(SAVETOPREF, saveToPref.value ?: false) //?: must not ever been happened
                putString(BEETOKN, beeTokn.value)
                putString(BEEUSER, beeUser.value)
                putString(BEEPASS, beePass.value)
            }
        else
            sharedPreferences.edit(commit = true) {
                putBoolean(SAVETOPREF, saveToPref.value ?: false)
                remove(BEEPASS)
                remove(BEEUSER)
                remove(BEETOKN)
            }
        _showPassUserLayout.value = false

        viewModelScope.launch {
            getTokenFromBee()
        }


    }

}

@JsonClass(generateAdapter = true)
data class BeelineToken(
    @Json(name = "meta")
    val meta: Meta,
    @Json(name = "token")
    val token: String?
) {
    @JsonClass(generateAdapter = true)
    data class Meta(
        @Json(name = "code")
        val code: Int,
        @Json(name = "message")
        val message: String?,
        @Json(name = "status")
        val status: String
    )
}

private val moshi = Moshi.Builder()
    ///.add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    //  .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .client(OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .callTimeout(10, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        )
        .build()
    )
    .baseUrl(BASEURL)
    .build()

interface BeelineApiService {

    @GET("auth")
    suspend fun getBeeToken(@Query("login") number: String, @Query("password") password: String):
    // The Coroutine Call Adapter allows us to return a Deferred, a Job with a result
            BeelineToken

    @GET("info/serviceList")
    suspend fun getBeeOptions(@Query("ctn") number: String, @Query("token") token: String):
            BeeOptionsData
}

object BeelineApi {
    val retrofitService: BeelineApiService by lazy { retrofit.create(BeelineApiService::class.java) }
}

@JsonClass(generateAdapter = true)
data class BeeOptionsData(
    @Json(name = "meta")
    val meta: Meta,
    @Json(name = "services")
    val services: List<Service>?
) {
    @JsonClass(generateAdapter = true)
    data class Meta(
        @Json(name = "code")
        val code: Int,
        @Json(name = "message")
        val message: String?,
        @Json(name = "status")
        val status: String
    )

    @JsonClass(generateAdapter = true)
    data class Service(
        @Json(name = "archiveInd")
        val archiveInd: Boolean,
        @Json(name = "baseFeatures")
        val baseFeatures: List<BaseFeature>,
        @Json(name = "category")
        val category: String?,
        @Json(name = "effDate")
        val effDate: String,
        @Json(name = "entityDesc")
        val entityDesc: String?,
        @Json(name = "entityName")
        val entityName: String,
        @Json(name = "expDate")
        val expDate: String?,
        @Json(name = "name")
        val name: String,
        @Json(name = "rcRate")
        val rcRate: Double?,
        @Json(name = "rcRatePeriod")
        val rcRatePeriod: String?,
        @Json(name = "rcRatePeriodText")
        val rcRatePeriodText: String?,
        @Json(name = "removeInd")
        val removeInd: String,
        @Json(name = "sdbSize")
        val sdbSize: Int,
        @Json(name = "viewInd")
        val viewInd: String
    ) {
        @JsonClass(generateAdapter = true)
        data class BaseFeature(
            @Json(name = "code")
            val code: String
        )
    }
}