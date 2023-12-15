package com.keser.stockmarketapp.presentation.company_detail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keser.stockmarketapp.common.Resource
import com.keser.stockmarketapp.domain.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyInfoViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: StockRepository
) : ViewModel() {

    private val _state = mutableStateOf(CompanyInfoState())
    val state: State<CompanyInfoState> = _state

    init {
        viewModelScope.launch {
            val symbol = savedStateHandle.get<String>("companySymbol") ?: return@launch
            _state.value = state.value.copy(isLoading = true)
            val companyInfoResult = async { repository.getCompanyInfo(symbol) }
            repository.getIntradayInfo(symbol).collect() { result ->
                when (result){
                    is Resource.Error -> {
                        _state.value = state.value.copy(
                            isLoading = false,
                            error = result.message,
                            company = null
                        )
                    }

                    is Resource.Loading -> Unit
                    is Resource.Success -> {
                        _state.value = state.value.copy(
                            stockInfo = result.data ?: emptyList(),
                            isLoading = false,
                            error = null
                        )
                    }
                }
            }

            when (val result = companyInfoResult.await()) {
                is Resource.Error -> {
                    _state.value = state.value.copy(
                        isLoading = false,
                        error = result.message,
                        company = null
                    )
                }

                is Resource.Loading -> Unit
                is Resource.Success -> {
                    _state.value = state.value.copy(
                        company = result.data,
                        isLoading = false,
                        error = null
                    )
                }
            }
        }
    }
}