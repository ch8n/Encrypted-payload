package dev.ch8n.encryptedpayloads.ui.server

import android.util.Log
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import dev.ch8n.encryptedpayloads.ui.data.Note
import dev.ch8n.encryptedpayloads.ui.data.service.ApiManager
import dev.ch8n.encryptedpayloads.ui.data.service.EncryptionService
import dev.ch8n.encryptedpayloads.ui.data.service.InMemoryDB
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.gson.gson
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.util.decodeBase64Bytes
import io.ktor.util.encodeBase64
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.Cipher
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

object EmbeddedServer : CoroutineScope {
    private var server: NettyApplicationEngine? = null
    private var serverJob: Job? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default

    fun startServer() {
        serverJob?.cancel()
        serverJob = launch {
            embeddedServer(
                Netty,
                port = 8080,
                module = Application::module
            )
                .also { it.start(wait = true) }
                .also { server = it }
        }
    }

    fun stopServer() {
        server?.stop(0, 0)
        serverJob?.cancel()
    }
}


fun Application.module() {

    install(ContentNegotiation) {
        gson()
    }

    val (publicKey, privateKey) = EncryptionService.getAsymmetricKey()

    routing {

        post(ApiManager.EndPoints.CREATE_NOTE) {
            try {
                val encryptedData = call.receive<String>()
                val decryptedData = EncryptionService.decrypt(encryptedData.decodeBase64Bytes(), privateKey)
                val note = Json.decodeFromString<Note>(decryptedData)
                InMemoryDB.add(note)
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                Log.e("ch8n", "app server create note error", e)
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        get(ApiManager.EndPoints.GET_NOTES) {
            val notes = InMemoryDB.notes
            call.respond(notes)
        }

        get(ApiManager.EndPoints.GET_KEY) {
            val encodedKey = publicKey.encoded.encodeBase64()
            val payload = mapOf("key" to encodedKey)
            call.respond(payload)
        }
    }
}
