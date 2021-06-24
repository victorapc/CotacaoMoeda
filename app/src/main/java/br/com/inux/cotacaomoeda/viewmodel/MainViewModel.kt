package com.inux.retrofitapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.inux.cotacaomoeda.model.ValorMoedasModel
import br.com.inux.cotacaomoeda.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel(private val repository: Repository) : ViewModel() {
    val myResponse: MutableLiveData<Response<ValorMoedasModel>> = MutableLiveData()
    val myListResponse: MutableLiveData<Response<List<ValorMoedasModel>>> = MutableLiveData()

    // GET -----------------------------------------------------------------------------------------
    fun getListMoeda(moedaStr: String){
        viewModelScope.launch {
            val response = repository.getListMoeda(moedaStr)
            myListResponse.value = response
        }
    }
}