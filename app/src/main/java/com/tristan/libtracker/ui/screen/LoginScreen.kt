package com.tristan.libtracker.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tristan.libtracker.ui.theme.TransitGreen

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // App Logo Placeholder (using the same colors)
            Surface(
                modifier = Modifier.size(120.dp),
                color = TransitGreen,
                shape = RoundedCornerShape(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🚲", fontSize = 64.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "libTracker",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2F3E46),
                    letterSpacing = (-1).sp
                )
            )
            
            Text(
                text = "Parisian Vélib Tracker",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFF2F3E46).copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = { onLoginSuccess() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TransitGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Se connecter avec Google", fontWeight = FontWeight.Bold)
            }
        }
    }
}
