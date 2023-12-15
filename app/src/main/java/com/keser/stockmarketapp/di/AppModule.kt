package com.keser.stockmarketapp.di

import android.app.Application
import androidx.room.Room
import com.keser.stockmarketapp.common.Consts.BASE_URL
import com.keser.stockmarketapp.common.Consts.DATABASE_NAME
import com.keser.stockmarketapp.data.csv.CSVParser
import com.keser.stockmarketapp.data.csv.CompanyListingParser
import com.keser.stockmarketapp.data.csv.IntradayInfoParser
import com.keser.stockmarketapp.data.local.StockDatabase
import com.keser.stockmarketapp.data.remote.StockApi
import com.keser.stockmarketapp.data.repository.StockRepositoryImp
import com.keser.stockmarketapp.domain.model.CompanyListing
import com.keser.stockmarketapp.domain.model.IntradayInfo
import com.keser.stockmarketapp.domain.repository.StockRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): StockDatabase {
        return Room.databaseBuilder(
            app,
            StockDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideStockApi(): StockApi {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(StockApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCompanyListingParser(): CSVParser<CompanyListing> {
        return CompanyListingParser()
    }

    @Provides
    @Singleton
    fun provideIntradayInfoParser(): CSVParser<IntradayInfo> {
        return IntradayInfoParser()
    }

    @Provides
    @Singleton
    fun provideStockRepository(
        api: StockApi,
        db: StockDatabase,
        companyListng: CSVParser<CompanyListing>,
        intradayInfo: CSVParser<IntradayInfo>
    ): StockRepository {
        return StockRepositoryImp(
            api = api,
            dao = db.dao,
            companyListingsParser = companyListng,
            intradayInfoParser = intradayInfo
        )
    }

}