package br.com.inux.cotacaomoeda.api

import br.com.inux.cotacaomoeda.model.ValorMoedasModel
import retrofit2.Response
import retrofit2.http.*

interface SimpleApi {

    @GET("json/{moeda}")
    suspend fun getListMoeda(@Path("moeda") moedaStr: String): Response<List<ValorMoedasModel>>
}