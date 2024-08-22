package dev.ch8n.encryptedpayloads.ui.data.service

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn
import okio.ByteString.Companion.decodeBase64
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

object EncryptionService {

    val publicKey = flow<String> {
        val payload = ApiManager.encryptionApiService.getKey()
        emit(payload.get("key") ?: "")
    }.shareIn(GlobalScope, SharingStarted.Lazily, 1)

    fun getAsymmetricKey(): Pair<PublicKey, PrivateKey> {
        val keyGen = KeyPairGenerator.getInstance("RSA")
        keyGen.initialize(2048)
        val keyPair = keyGen.genKeyPair()
        return keyPair.public to keyPair.private
    }

    fun decrypt(data: ByteArray, privateKey: PrivateKey): String {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        return String(cipher.doFinal(data), Charsets.UTF_8)
    }

    fun encrypt(data: String, publicKey: PublicKey): ByteArray {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinal(data.toByteArray(Charsets.UTF_8))
    }

    fun stringToPublicKey(publicKeyString: String): PublicKey {
        val keyBytes = publicKeyString.decodeBase64()?.toByteArray()
        val keySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(keySpec)
    }

}