package com.keser.stockmarketapp.common

sealed class Screen(val route: String) {
    object CompanyListingScreen: Screen("company_listing_screen")
    object CompanyInfoScreen: Screen("company_info_screen")
}
