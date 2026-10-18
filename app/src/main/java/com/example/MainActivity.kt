package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppTab
import com.example.ui.HyruleViewModel
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    private val viewModel: HyruleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val currentTab by viewModel.currentTab.collectAsState()

                // BackHandler returns to Home screen if on another tab
                if (currentTab != AppTab.HOME) {
                    BackHandler {
                        viewModel.selectTab(AppTab.HOME)
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = SheikahSlateBg,
                    bottomBar = {
                        NavigationBar(
                            containerColor = SheikahSlateCard,
                            tonalElevation = 8.dp,
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = SheikahSlateBorder,
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                                )
                                .testTag("bottom_nav_bar")
                        ) {
                            AppTab.entries.forEach { tab ->
                                val isSelected = currentTab == tab
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { viewModel.selectTab(tab) },
                                    icon = { Text(tab.icon, fontSize = 20.sp) },
                                    label = {
                                        Text(
                                            text = tab.title,
                                            fontSize = 9.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            maxLines = 1
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = SheikahCyanBright,
                                        selectedTextColor = SheikahCyanBright,
                                        unselectedIconColor = Color.White.copy(alpha = 0.5f),
                                        unselectedTextColor = Color.White.copy(alpha = 0.5f),
                                        indicatorColor = SheikahCyan.copy(alpha = 0.2f)
                                    ),
                                    modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentTab) {
                            AppTab.HOME -> HomeScreen(viewModel = viewModel)
                            AppTab.COMPENDIUM -> CompendiumScreen(viewModel = viewModel)
                            AppTab.COOKING -> CookingScreen(viewModel = viewModel)
                            AppTab.SHRINES -> ShrinesScreen(viewModel = viewModel)
                            AppTab.RUNES -> RunesScreen(viewModel = viewModel)
                            AppTab.ARMOR -> ArmorScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
