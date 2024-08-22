package dev.ch8n.encryptedpayloads.ui.data

import android.util.Log
import io.ktor.util.encodeBase64
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

object ApiManager {

    object EndPoints {
        const val GET_NOTES = "/chetan/notes"
        const val CREATE_NOTE = "/chetan/notes/create"
        const val GET_KEY = "/chetan/key"
    }

    object HttpClient {

        private val loggingInterceptor by lazy {
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        }

        private val encryptionInterceptor by lazy {
            Interceptor { chain ->
                val originalRequest = chain.request()

                // convert body to string
                val originalBodyAsString = originalRequest.body?.let { body ->
                    val buffer = Buffer()
                    body.writeTo(buffer)
                    buffer.readUtf8()
                } ?: ""

                if (originalBodyAsString.isEmpty()) {
                    return@Interceptor chain.proceed(originalRequest)
                }

                // Get Encrypted Data
                val publicKey = runBlocking { EncryptionService.publicKey.first() }
                Log.e("ch8n", "encryptionInterceptor: $publicKey")
                val encryptionKey = EncryptionService.stringToPublicKey(publicKey)

                // Encrypt the payload
                val encryptedBodyAsByteArray =
                    EncryptionService.encrypt(originalBodyAsString, encryptionKey)
                val base64Encoded = encryptedBodyAsByteArray.encodeBase64()

                // Create a new request with the encrypted body
                val newRequest = originalRequest.newBuilder()
                    .post(base64Encoded.toRequestBody(originalRequest.body?.contentType()))
                    .build()

                return@Interceptor chain.proceed(newRequest)
            }
        }


        private val httpClient: OkHttpClient by lazy {
            OkHttpClient.Builder()
                .addInterceptor(encryptionInterceptor)
                .addInterceptor(loggingInterceptor)
                .build()
        }

        private val BASE_URL = "http://localhost:8080/"

        val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

    }


    interface NoteApiService {

        @GET(EndPoints.GET_NOTES)
        suspend fun getNotes(): List<Note>

        @POST(EndPoints.CREATE_NOTE)
        suspend fun createNote(@Body note: Note)
    }

    interface EncryptionApiService {

        @GET(EndPoints.GET_KEY)
        suspend fun getKey(): Map<String, String>
    }

    val notesApiService: NoteApiService by lazy {
        HttpClient.retrofit.create(NoteApiService::class.java)
    }

    val encryptionApiService: EncryptionApiService by lazy {
        HttpClient.retrofit.create(EncryptionApiService::class.java)
    }
}