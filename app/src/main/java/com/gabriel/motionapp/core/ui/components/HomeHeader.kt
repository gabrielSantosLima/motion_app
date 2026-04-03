package com.gabriel.motionapp.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.gabriel.motionapp.R
import com.gabriel.motionapp.core.ui.Route

@Preview(showBackground = true)
@Composable
fun HomeHeaderPreview() {
    val navController = rememberNavController()
    HomeHeader(navController)
}

@Composable
fun HomeHeader(navController: NavController) {
    var expanded by remember { mutableStateOf(false) }

    fun changeMenuVisibility(visibility: Boolean = !expanded) {
        expanded = visibility
    }

    Header(horizontalArrangement = Arrangement.End) {
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            onClick = { changeMenuVisibility() }
        ) {
            Icon(
                painter = painterResource(R.drawable.list_24px),
                contentDescription = "List",
                tint = Color.Black
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { changeMenuVisibility(false) }
            ) {
                DropdownMenuItem(text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            4.dp,
                            alignment = Alignment.CenterHorizontally
                        )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.code_24px),
                            contentDescription = "Code"
                        )
                        Text("Camera Debugger")
                    }
                }, onClick = { navController.navigate(Route.CameraDebugger.route) })
            }
        }
    }
}