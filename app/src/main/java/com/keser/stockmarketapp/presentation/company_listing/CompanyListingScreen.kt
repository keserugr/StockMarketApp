package com.keser.stockmarketapp.presentation.company_listing

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.keser.stockmarketapp.common.Screen
import com.keser.stockmarketapp.presentation.company_listing.components.CompanyItem

//@Destination(start = true)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CompanyListingScreen(
    //navigator: DestinationsNavigator,
    navController: NavController,
    viewModel: CompanyListingsViewModel = hiltViewModel()
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = viewModel.state.value.isRefreshing,
        onRefresh = { viewModel.onEvent(CompanyListingsEvent.Refresh) }
    )

    val state = viewModel.state.value

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = {
                viewModel.onEvent(
                    CompanyListingsEvent.OnSearchQueryChange(it)
                )
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            placeholder = {
                Text(text = "Search...")
            },
            maxLines = 1,
            singleLine = true
        )

        Box(Modifier.pullRefresh(pullRefreshState)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.companies.size) { i ->
                    val company = state.companies[i]
                    CompanyItem(
                        company = company,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Navigate
                                navController.navigate(
                                    Screen.CompanyInfoScreen.route + "/${company.symbol}"
                                )
                            }
                            .padding(16.dp)
                    )
                }
            }
            //PullRefreshIndicator(refreshing = , state = pullRefreshState)
        }
    }
}