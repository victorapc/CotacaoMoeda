package br.com.inux.cotacaomoeda.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import br.com.inux.cotacaomoeda.R
import br.com.inux.cotacaomoeda.databinding.ActivityMainBinding
import br.com.inux.cotacaomoeda.model.Moedas
import br.com.inux.cotacaomoeda.model.ValorMoedasModel
import br.com.inux.cotacaomoeda.repository.Repository
import br.com.inux.cotacaomoeda.utils.Constants.Companion.EUR_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.GBP_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.USD_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.USD_BRLT
import com.inux.retrofitapp.viewmodel.MainViewModel
import com.inux.retrofitapp.viewmodel.MainViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    val lista: ArrayList<Moedas> by lazy {
        ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        binding.moedasAu.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                carregarDadosApi(lista[position])
            }
    }

    override fun onResume() {
        super.onResume()

        carregarSpinner()
    }

    private fun carregarDadosApi(moeda: Moedas){
        CoroutineScope(Dispatchers.Main).launch {
            try {
                viewModel.getListMoeda(moeda.tipoMoeda)

                viewModel.myListResponse.observe(this@MainActivity, Observer { response ->
                    if(response.isSuccessful){
                        response.body()?.let { moedas ->
                            val moeda = moedas[0]

                            Toast.makeText(this@MainActivity, "Posição: ${moeda.name}", Toast.LENGTH_LONG).show()
                        }
                    }else{
                        when(response.code()){
                            404 -> Toast.makeText(this@MainActivity, getString(R.string.erro_404), Toast.LENGTH_LONG).show()
                            else -> {
                                Toast.makeText(this@MainActivity, getString(R.string.erro_geral), Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                })
            }catch (e: Exception){
                Toast.makeText(this@MainActivity, "Erro ao comunicar com o servidor", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun carregarSpinner(){
        lista.add(Moedas(1, "Dólar Americano / Real Brasileiro", USD_BRL))
        lista.add(Moedas(2, "Dólar Americano / Real Brasileiro Turismo", USD_BRLT))
        lista.add(Moedas(3, "Euro / Real Brasileiro", EUR_BRL))
        lista.add(Moedas(4, "Libra Esterlina / Real Brasileiro", GBP_BRL))

        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, lista)

        binding.moedasAu.setAdapter(arrayAdapter)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_update  ->
                Toast.makeText(this, "Atualizar", Toast.LENGTH_LONG).show()
            else -> false
        }
        return super.onOptionsItemSelected(item)
    }
}