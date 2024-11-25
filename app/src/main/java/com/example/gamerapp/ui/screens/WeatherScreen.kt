package com.example.gamerapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WeatherScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // City selection
        OutlinedTextField(
            value = "New York", // This should be state-managed in a real app
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter city name") },
            leadingIcon = { Icon(Icons.Default.LocationOn, "Location") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Main weather card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "72°F",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Sunny",
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    WeatherDetail(Icons.Default.WaterDrop, "Humidity", "65%")
                    WeatherDetail(Icons.Default.Air, "Wind", "8 mph")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Hourly forecast
        Text(
            text = "Hourly Forecast",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            repeat(6) { hour ->
                HourlyForecastItem(hour)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Daily forecast
        Text(
            text = "7-Day Forecast",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(7) { day ->
                DailyForecastItem(day)
            }
        }
    }
}

@Composable
private fun WeatherDetail(icon: ImageVector, label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = label)
        Text(text = label)
        Text(
            text = value,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun HourlyForecastItem(hour: Int) {
    Card(
        modifier = Modifier.width(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "${(hour + 1)}:00")
            Icon(Icons.Default.WbSunny, contentDescription = "Weather")
            Text(text = "${70 + hour}°F")
        }
    }
}

@Composable
private fun DailyForecastItem(day: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when(day) {
                    0 -> "Today"
                    1 -> "Tomorrow"
                    else -> "Day ${day + 1}"
                }
            )
            Icon(Icons.Default.WbSunny, contentDescription = "Weather")
            Text(text = "${75 - day}°F / ${65 - day}°F")
        }
    }
} 