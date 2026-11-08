package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.model.NavTab
import com.example.ui.GarageStrings
import com.example.ui.theme.GarageOrange
import com.example.ui.theme.GarageRed
import com.example.ui.theme.NavyPrimary

@Composable
fun GarageBottomNav(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    lowStockCount: Int,
    language: Language
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        // Tab 1: Home
        NavigationBarItem(
            selected = currentTab == NavTab.HOME,
            onClick = { onTabSelected(NavTab.HOME) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = GarageStrings.get("navHome", language)
                )
            },
            label = {
                Text(
                    text = GarageStrings.get("navHome", language),
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == NavTab.HOME) FontWeight.Bold else FontWeight.Medium
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GarageOrange,
                selectedTextColor = NavyPrimary,
                indicatorColor = NavyPrimary.copy(alpha = 0.1f)
            )
        )

        // Tab 2: Bill
        NavigationBarItem(
            selected = currentTab == NavTab.BILL,
            onClick = { onTabSelected(NavTab.BILL) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = GarageStrings.get("navBill", language)
                )
            },
            label = {
                Text(
                    text = GarageStrings.get("navBill", language),
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == NavTab.BILL) FontWeight.Bold else FontWeight.Medium
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GarageOrange,
                selectedTextColor = NavyPrimary,
                indicatorColor = NavyPrimary.copy(alpha = 0.1f)
            )
        )

        // Tab 3: Stock
        NavigationBarItem(
            selected = currentTab == NavTab.STOCK,
            onClick = { onTabSelected(NavTab.STOCK) },
            icon = {
                BadgedBox(
                    badge = {
                        if (lowStockCount > 0) {
                            Badge(
                                containerColor = GarageRed,
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = lowStockCount.toString(),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Inventory,
                        contentDescription = GarageStrings.get("navStock", language)
                    )
                }
            },
            label = {
                Text(
                    text = GarageStrings.get("navStock", language),
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == NavTab.STOCK) FontWeight.Bold else FontWeight.Medium
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GarageOrange,
                selectedTextColor = NavyPrimary,
                indicatorColor = NavyPrimary.copy(alpha = 0.1f)
            )
        )

        // Tab 4: Reports
        NavigationBarItem(
            selected = currentTab == NavTab.REPORTS,
            onClick = { onTabSelected(NavTab.REPORTS) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Assessment,
                    contentDescription = GarageStrings.get("navReports", language)
                )
            },
            label = {
                Text(
                    text = GarageStrings.get("navReports", language),
                    fontSize = 10.sp,
                    fontWeight = if (currentTab == NavTab.REPORTS) FontWeight.Bold else FontWeight.Medium
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GarageOrange,
                selectedTextColor = NavyPrimary,
                indicatorColor = NavyPrimary.copy(alpha = 0.1f)
            )
        )

        // Tab 5: Settings
        NavigationBarItem(
            selected = currentTab == NavTab.SETTINGS,
            onClick = { onTabSelected(NavTab.SETTINGS) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = GarageStrings.get("navSettings", language)
                )
            },
            label = {
                Text(
                    text = GarageStrings.get("navSettings", language),
                    fontSize = 10.sp,
                    fontWeight = if (currentTab == NavTab.SETTINGS) FontWeight.Bold else FontWeight.Medium
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GarageOrange,
                selectedTextColor = NavyPrimary,
                indicatorColor = NavyPrimary.copy(alpha = 0.1f)
            )
        )
    }
}
