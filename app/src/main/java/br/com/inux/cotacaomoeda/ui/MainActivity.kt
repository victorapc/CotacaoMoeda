package br.com.inux.cotacaomoeda.ui

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import br.com.inux.cotacaomoeda.R
import br.com.inux.cotacaomoeda.databinding.ActivityMainBinding
import br.com.inux.cotacaomoeda.model.Moedas
import br.com.inux.cotacaomoeda.repository.Repository
import br.com.inux.cotacaomoeda.utils.Constants.Companion.AED_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.ARS_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.AUD_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.BOB_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.BTC_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.CAD_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.CHF_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.CLP_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.CNY_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.COP_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.DKK_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.DOGE_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.ETH_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.EUR_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.GBP_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.HKD_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.ILS_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.INR_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.JPY_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.LTC_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.MXN_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.NOK_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.NZD_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.PEN_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.PLN_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.PYG_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.RUB_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.SAR_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.SEK_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.SGD_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.THB_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.TRY_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.TWD_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.USD_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.USD_BRLT
import br.com.inux.cotacaomoeda.utils.Constants.Companion.UYU_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.XRP_BRL
import br.com.inux.cotacaomoeda.utils.Constants.Companion.ZAR_BRL
import br.com.inux.cotacaomoeda.utils.MetodosGlobais
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.inux.retrofitapp.viewmodel.MainViewModel
import com.inux.retrofitapp.viewmodel.MainViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    // Objetos.
    private lateinit var globais: MetodosGlobais
    private lateinit var moeda: Moedas

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private var valorMoeda: Double = 0.0
    private var posicao: Int = -1
    val lista: ArrayList<Moedas> by lazy {
        ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // Instanciando objetos
        globais = MetodosGlobais(this@MainActivity)

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        // Deixando inativo o campo de digitar valor até selecionar uma moeda
        habilitarDesabilitarCampos(false)

        binding.moedasAu.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                posicao = position
                moeda = lista[position]

                carregarDadosMoeda()
            }

        binding.valorEdt.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calcular()
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.adicionarBtn.setOnClickListener {
            calcularBotoes(true)
        }

        binding.removerBtn.setOnClickListener {
            calcularBotoes(false)
        }

        posicao = -1;

        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        binding.publicidade.loadAd(adRequest)
    }

    override fun onResume() {
        super.onResume()

        carregarSpinner()
    }

    private fun habilitarDesabilitarCampos(tipo: Boolean){
        binding.valorEdt.isEnabled = tipo
        binding.adicionarBtn.isEnabled = tipo
        binding.removerBtn.isEnabled = tipo
    }

    private fun carregarDadosMoeda(){
        val connectivity = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if(globais.existeConexao(connectivity)){
            carregarDadosApi(moeda)
        } else {
            Toast.makeText(
                this@MainActivity,
                "É necessário uma conexão para buscar a cotação das moedas.",
                Toast.LENGTH_LONG).show()
        }
    }

    private fun carregarDadosApi(moeda: Moedas){
        CoroutineScope(Dispatchers.Main).launch {
            try {
                binding.recylerProgressBar.visibility = View.VISIBLE

                viewModel.getListMoeda(moeda.tipoMoeda)

                viewModel.myListResponse.observe(this@MainActivity, Observer { response ->
                    if(response.isSuccessful){
                        response.body()?.let { moedas ->
                            val moeda = moedas[0]

                            valorMoeda = globais.formataValor(moeda.bid).toDouble()
                            binding.compraVlrTxt.text = "R$ ${globais.formataValor(valorMoeda)}"
                            binding.vendaVlrTxt.text = "R$ ${globais.formataValor(moeda.ask)}"
                            binding.altaVlrTxt.text = "R$ ${globais.formataValor(moeda.high)}"
                            binding.baixaVlrTxt.text = "R$ ${globais.formataValor(moeda.low)}"

                            binding.valorEdt.setText("1.00")
                            habilitarDesabilitarCampos(true)

                            binding.recylerProgressBar.visibility = View.GONE
                        }
                    }else{
                        binding.recylerProgressBar.visibility = View.GONE
                        habilitarDesabilitarCampos(false)

                        when(response.code()){
                            404 -> Toast.makeText(this@MainActivity, getString(R.string.erro_404), Toast.LENGTH_LONG).show()
                            else -> {
                                Toast.makeText(this@MainActivity, getString(R.string.erro_geral), Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                })
            }catch (e: Exception){
                binding.recylerProgressBar.visibility = View.GONE
                habilitarDesabilitarCampos(false)

                Toast.makeText(this@MainActivity, getString(R.string.erro_geral), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun calcular(){
        var valorDigitado: Double

        valorDigitado = if(binding.valorEdt.text?.isEmpty() == true
            || binding.valorEdt.text?.toString().equals("")
            || binding.valorEdt.text?.toString().equals(".")){
            1.0
        } else {
            binding.valorEdt.text.toString().replace(",", ".").toDouble()
        }

        var totalValorMoeda: Double = valorMoeda * valorDigitado

        binding.valorResultadoTxt.text = "R$ ${globais.formataValor(totalValorMoeda)}"
    }

    private fun calcularBotoes(somar: Boolean){
        var valorDigitado: Double

        valorDigitado = if(binding.valorEdt.text?.isEmpty() == true
            || binding.valorEdt.text?.toString().equals("")
            || binding.valorEdt.text?.toString().equals(".")){
            1.0
        } else {
            binding.valorEdt.text.toString().replace(",", ".").toDouble()
        }

        if(somar){
            valorDigitado = valorDigitado + 1
        }else{
            valorDigitado = valorDigitado - 1

            if(valorDigitado <= 0){
                valorDigitado = 1.0
            }
        }

        binding.valorEdt.setText(globais.formataValor(valorDigitado))
    }

    private fun carregarSpinner(){
        lista.add(Moedas(1, "Dólar Americano / Real Brasileiro", USD_BRL))
        lista.add(Moedas(2, "Dólar Americano / Real Brasileiro Turismo", USD_BRLT))
        lista.add(Moedas(3, "Dólar Canadense / Real Brasileiro", CAD_BRL))
        lista.add(Moedas(4, "Euro / Real Brasileiro", EUR_BRL))
        lista.add(Moedas(5, "Libra Esterlina / Real Brasileiro", GBP_BRL))
        /*lista.add(Moedas(6, "Bitcoin / Real Brasileiro", BTC_BRL))
        lista.add(Moedas(7, "Dogecoin / Real Brasileiro", DOGE_BRL))
        lista.add(Moedas(8, "Ethereum / Real Brasileiro", ETH_BRL))
        lista.add(Moedas(9, "Litecoin/Real Brasileiro", LTC_BRL))*/
        lista.add(Moedas(10, "Peso Argentino / Real Brasileiro", ARS_BRL))
        lista.add(Moedas(11, "Peso Chileno / Real Brasileiro", CLP_BRL))
        lista.add(Moedas(12, "Peso Uruguaio / Real Brasileiro", UYU_BRL))
        lista.add(Moedas(13, "Guarani Paraguaio / Real Brasileiro", PYG_BRL))
        lista.add(Moedas(14, "Peso Colombiano / Real Brasileiro", COP_BRL))
        lista.add(Moedas(15, "Boliviano / Real Brasileiro", BOB_BRL))
        lista.add(Moedas(16, "Dólar Australiano / Real Brasileiro", AUD_BRL))
        lista.add(Moedas(17, "Peso Mexicano / Real Brasileiro", MXN_BRL))
        lista.add(Moedas(18, "Sol do Peru / Real Brasileiro", PEN_BRL))
        lista.add(Moedas(19, "Iene Japonês / Real Brasileiro", JPY_BRL))
        lista.add(Moedas(20, "Franco Suíço / Real Brasileiro", CHF_BRL))
        lista.add(Moedas(21, "Yuan Chinês / Real Brasileiro", CNY_BRL))
        lista.add(Moedas(22, "Novo Shekel Israelense / Real Brasileiro", ILS_BRL))
        lista.add(Moedas(23, "XRP / Real Brasileiro", XRP_BRL))
        lista.add(Moedas(24, "Dólar de Cingapura / Real Brasileiro", SGD_BRL))
        lista.add(Moedas(25, "Dirham dos Emirados / Real Brasileiro", AED_BRL))
        lista.add(Moedas(26, "Coroa Dinamarquesa / Real Brasileiro", DKK_BRL))
        lista.add(Moedas(27, "Dólar de Hong Kong / Real Brasileiro", HKD_BRL))
        lista.add(Moedas(28, "Coroa Norueguesa / Real Brasileiro", NOK_BRL))
        lista.add(Moedas(29, "Dólar Neozelandês / Real Brasileiro", NZD_BRL))
        lista.add(Moedas(30, "Zlóti Polonês / Real Brasileiro", PLN_BRL))
        lista.add(Moedas(31, "Riyal Saudita / Real Brasileiro", SAR_BRL))
        lista.add(Moedas(32, "Coroa Sueca / Real Brasileiro", SEK_BRL))
        lista.add(Moedas(33, "Baht Tailandês / Real Brasileiro", THB_BRL))
        lista.add(Moedas(34, "Nova Lira Turca / Real Brasileiro", TRY_BRL))
        lista.add(Moedas(35, "Dólar Taiuanês / Real Brasileiro", TWD_BRL))
        lista.add(Moedas(36, "Rand Sul-Africano / Real Brasileiro", ZAR_BRL))
        lista.add(Moedas(37, "Rublo Russo / Real Brasileiro", RUB_BRL))
        lista.add(Moedas(38, "Rúpia Indiana / Real Brasileiro", INR_BRL))

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
                if(posicao >= 0){
                    carregarDadosMoeda()
                } else {
                    Toast.makeText(this,
                        "Informe uma moeda para cotação.",
                        Toast.LENGTH_LONG).show()
                }
            else -> false
        }
        return super.onOptionsItemSelected(item)
    }
}