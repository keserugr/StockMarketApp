package com.keser.stockmarketapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.keser.stockmarketapp.common.Screen
import com.keser.stockmarketapp.presentation.company_detail.CompanyInfoScreen
import com.keser.stockmarketapp.presentation.company_listing.CompanyListingScreen
import com.keser.stockmarketapp.ui.theme.StockMarketAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StockMarketAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    //DestinationsNavHost(navGraph = NavGraphs.root)
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.CompanyListingScreen.route
                    ) {
                        composable(route = Screen.CompanyListingScreen.route) {
                            CompanyListingScreen(navController = navController)
                        }
                        composable(
                            route = Screen.CompanyInfoScreen.route +"/"+
                                    "{companySymbol}",
                            arguments = listOf(
                                navArgument(
                                    name = "companySymbol"
                                ) {
                                    type = NavType.StringType
                                    defaultValue = ""
                                }
                            )
                        ) {
                            val symbol = remember { it.arguments?.getString("companySymbol") }
                            CompanyInfoScreen(navController = navController, symbol = symbol ?: "")
                        }
                    }
                }
            }
        }
    }
}