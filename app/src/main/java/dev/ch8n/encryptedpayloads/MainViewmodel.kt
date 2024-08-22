package dev.ch8n.encryptedpayloads

import dev.ch8n.encryptedpayloads.ui.data.Note
import dev.ch8n.encryptedpayloads.ui.data.ApiManager
import dev.ch8n.encryptedpayloads.ui.data.EncryptionService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

class MainViewmodel : CoroutineScope {

    private val notesService = ApiManager.notesApiService
    val notes = MutableStateFlow(emptyList<Note>())
    val publicKey = MutableStateFlow("")

    fun refreshNotes() {
        launch {
            val _notes = notesService.getNotes()
            notes.update { _notes }

            EncryptionService.publicKey
                .onEach { key ->
                    publicKey.update { key }
                }
                .launchIn(this)
        }
    }

    fun createNote(note: String) {
        launch {
            notesService.createNote(
                Note(
                    id = Random.nextInt(),
                    value = note
                )
            )
            refreshNotes()
        }
    }

    override val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.IO

}
