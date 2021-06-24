package br.com.inux.cotacaomoeda.repository

import br.com.inux.cotacaomoeda.api.RetrofitInstance
import br.com.inux.cotacaomoeda.model.ValorMoedasModel
import retrofit2.Response

class Repository {
    suspend fun getListMoeda(moedaStr: String): Response<List<ValorMoedasModel>> {
        return RetrofitInstance.api.getListMoeda(moedaStr)
    }
}