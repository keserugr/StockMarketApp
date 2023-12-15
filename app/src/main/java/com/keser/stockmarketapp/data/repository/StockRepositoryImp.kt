package com.keser.stockmarketapp.data.repository

import com.keser.stockmarketapp.common.Resource
import com.keser.stockmarketapp.data.csv.CSVParser
import com.keser.stockmarketapp.data.local.StockDao
import com.keser.stockmarketapp.data.mapper.toCompanyInfo
import com.keser.stockmarketapp.data.mapper.toCompanyListing
import com.keser.stockmarketapp.data.mapper.toCompanyListingEntity
import com.keser.stockmarketapp.data.remote.StockApi
import com.keser.stockmarketapp.domain.model.CompanyInfo
import com.keser.stockmarketapp.domain.model.CompanyListing
import com.keser.stockmarketapp.domain.model.IntradayInfo
import com.keser.stockmarketapp.domain.repository.StockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class StockRepositoryImp @Inject constructor(
    val api: StockApi,
    val dao: StockDao,
    val companyListingsParser: CSVParser<CompanyListing>,
    val intradayInfoParser: CSVParser<IntradayInfo>
) : StockRepository {

    override fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {
            emit(Resource.Loading(true))

            val localListings = dao.searchCompanyListing(query)
            emit(Resource.Success(
                data = localListings.map { it.toCompanyListing() }
            ))

            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote
            if (shouldJustLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }

            val remoteListings = try {
                val response = api.getListings()
                companyListingsParser.parse(response.byteStream())
            } catch (e: IOException) {
                //Some parsing went wrong!
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                null
            } catch (e: HttpException) {
                //There is an invalid response
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                null
            }

            remoteListings?.let { listings ->
                dao.clearCompanyListing()
                dao.insertCompanyListing(listings.map { it.toCompanyListingEntity() })
                emit(Resource.Success(
                    data = dao
                        .searchCompanyListing("")
                        .map { it.toCompanyListing() }
                ))
                emit(Resource.Loading(false))
            }

        }
    }

    override fun getIntradayInfo(symbol: String): Flow<Resource<List<IntradayInfo>>> {
        return flow {
            emit(Resource.Loading(true))

            val remoteListing = try {
                val response = api.getIntradayInfo(symbol)
                intradayInfoParser.parse(response.byteStream())
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error(message = "Couldn't load intraday info"))
                null
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error(message = "Couldn't load intraday info"))
                null
            }

            remoteListing?.let {
                emit(Resource.Success(data = it))
            }
            emit(Resource.Loading(false))
        }
    }

    override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfo> {
        return try {
            val result = api.getCompanyInfo(symbol)
            Resource.Success(result.toCompanyInfo())
        } catch (e: IOException) {
            e.printStackTrace()
            Resource.Error("Couldn't load company info")
        } catch (e: HttpException) {
            e.printStackTrace()
            Resource.Error("Couldn't load company info")
        }
    }
}