package system.kuu.sample_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.fumiya_kume.gtfs_k.lib.GtfsData
import io.github.fumiya_kume.gtfs_k.lib.gtfsReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val coroutineScope = rememberCoroutineScope()
                var data: GtfsData? by remember { mutableStateOf(null) }

                LaunchedEffect(Unit) {
                    coroutineScope.launch(Dispatchers.IO) {
                        data =
                            gtfsReader("https://github.com/fumiya-kume/gtfs-k/raw/refs/heads/master/test-data/toyotetsu.zip")
                    }
                }

                LazyColumn(modifier = Modifier.padding(horizontal = 8.dp)) {
                    item {
                        Text("Agency", style = MaterialTheme.typography.headlineMedium)
                    }
                    items(data?.agency ?: emptyList()) { agency ->
                        OutlinedCard(modifier = Modifier.padding(16.dp)) {
                            Text(agency.toString())
                        }
                    }

                    item {
                        Text("AgencyJapan", style = MaterialTheme.typography.headlineMedium)
                    }
                    items(data?.agencyJapan ?: emptyList()) {
                        OutlinedCard(modifier = Modifier.padding(16.dp)) {
                            Text(it.toString())
                        }
                    }

                    item {
                        Text("Routed", style = MaterialTheme.typography.headlineMedium)
                    }
                    items(data?.routes?.distinctBy { it.routeShortName } ?: emptyList()) {
                        OutlinedCard(modifier = Modifier.padding(16.dp)) {
                            Text(it.routeShortName ?: "")
                        }
                    }

                    item {
                        Text("Feed info", style = MaterialTheme.typography.headlineMedium)
                    }
                    items(data?.feedInfo ?: emptyList()) {
                        OutlinedCard(
                            modifier = Modifier.padding(
                                horizontal = 16.dp,
                                vertical = 2.dp
                            )
                        ) {
                            Text(it.feedPublisherUrl ?: it.feedPublisherName ?: "")
                        }
                    }

                    item {
                        Text("Calendar", style = MaterialTheme.typography.headlineMedium)
                    }
                    items(data?.calenders ?: emptyList()) {
                        OutlinedCard(
                            modifier = Modifier.padding(
                                horizontal = 16.dp,
                                vertical = 2.dp
                            )
                        ) {
                            Text(it.monday ?: "")
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun MainPreview() {
    MaterialTheme {
        Button(onClick = { /*TODO*/ }) {
            Text("Hello, World!")
        }
    }
}