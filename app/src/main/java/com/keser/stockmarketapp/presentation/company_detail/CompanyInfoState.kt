package com.keser.stockmarketapp.presentation.company_detail

import com.keser.stockmarketapp.domain.model.CompanyInfo
import com.keser.stockmarketapp.domain.model.IntradayInfo

data class CompanyInfoState(
    val stockInfo: List<IntradayInfo> = emptyList(),
    val company: CompanyInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
