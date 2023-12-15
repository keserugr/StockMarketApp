package com.keser.stockmarketapp.domain.repository

import com.keser.stockmarketapp.common.Resource
import com.keser.stockmarketapp.domain.model.CompanyInfo
import com.keser.stockmarketapp.domain.model.CompanyListing
import com.keser.stockmarketapp.domain.model.IntradayInfo
import kotlinx.coroutines.flow.Flow

interface StockRepository {

    fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>>

    fun getIntradayInfo(
        symbol: String
    ): Flow<Resource<List<IntradayInfo>>>

    suspend fun getCompanyInfo(
        symbol: String
    ): Resource<CompanyInfo>

}