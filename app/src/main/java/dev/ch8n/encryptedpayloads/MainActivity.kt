package dev.ch8n.encryptedpayloads

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import dev.ch8n.encryptedpayloads.server.EmbeddedServer
import dev.ch8n.encryptedpayloads.ui.theme.EncryptedPayloadsTheme
import kotlin.random.Random


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        EmbeddedServer.startServer()
        val viewmodel = MainViewmodel()

        setContent {
            EncryptedPayloadsTheme {
                val notes by viewmodel.notes.collectAsState()
                val publicKey by viewmodel.publicKey.collectAsState()

                Column(
                    modifier = Modifier
                        .safeContentPadding()
                        .fillMaxSize()
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewmodel.createNote(
                                LoremIpsum(Random.nextInt(1, 4))
                                    .values.joinToString("")
                            )
                        }
                    ) {
                        Text(text = "Add Random Note")
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewmodel.refreshNotes()
                        }
                    ) {
                        Text(text = "Refresh")
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "key : $publicKey",
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .border(2.dp, Color.Magenta),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 4
                        )

                        notes.forEach {
                            Text(
                                text = it.toString(),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        EmbeddedServer.stopServer()
        super.onDestroy()
    }
}
