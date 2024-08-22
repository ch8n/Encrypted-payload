package dev.ch8n.encryptedpayloads.ui.data

import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import kotlin.random.Random

object InMemoryDB {

    val notes = mutableListOf<Note>().apply {
        add(
            Note(
                id = Random.nextInt(),
                value = LoremIpsum(5).values.joinToString()
            )
        )

        add(
            Note(
                id = Random.nextInt(),
                value = LoremIpsum(5).values.joinToString()
            )
        )
    }

    fun add(note: Note) {
        notes.add(note)
    }
}