package com.keser.stockmarketapp.presentation.company_listing

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keser.stockmarketapp.common.Resource
import com.keser.stockmarketapp.domain.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyListingsViewModel @Inject constructor(
    val repository: StockRepository
) : ViewModel() {

    private val _state = mutableStateOf(CompanyListingsState())
    val state: State<CompanyListingsState> = _state

    private var searchJob: Job? = null

    init {
        getCompanyListings(fetchFromRemote = true)
    }

    fun onEvent(event: CompanyListingsEvent) {
        when (event) {
            is CompanyListingsEvent.OnSearchQueryChange -> {
                _state.value = state.value.copy(searchQuery = event.query)
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(500L)
                    getCompanyListings()
                }
            }

            is CompanyListingsEvent.Refresh -> {
                getCompanyListings(fetchFromRemote = true)
            }
        }
    }

    private fun getCompanyListings(
        query: String = _state.value.searchQuery.lowercase(),
        fetchFromRemote: Boolean = false
    ) {
        viewModelScope.launch {
            repository.getCompanyListings(fetchFromRemote, query)
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { listings ->
                                _state.value = state.value.copy(
                                    companies = listings
                                )
                            }
                        }

                        is Resource.Error -> Unit
                        is Resource.Loading -> {
                            _state.value = state.value.copy(isLoading = result.isLoading)
                        }
                    }
                }
        }
    }
}