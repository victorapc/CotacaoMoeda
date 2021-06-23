package br.com.inux.cotacaomoeda.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import br.com.inux.cotacaomoeda.R
import br.com.inux.cotacaomoeda.databinding.ActivityMainBinding
import br.com.inux.cotacaomoeda.model.Moedas

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.moedasAu.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                Toast.makeText(this@MainActivity, "Posição: ${position}", Toast.LENGTH_LONG).show()
            }
    }

    override fun onResume() {
        super.onResume()

        carregarSpinner()
    }

    private fun carregarSpinner(){
        val lista = ArrayList<Moedas>()
        lista.add(Moedas(1, "Dólar Americano / Real Brasileiro"))
        lista.add(Moedas(2, "Dólar Americano / Real Brasileiro Turismo"))
        lista.add(Moedas(3, "Euro / Real Brasileiro"))
        lista.add(Moedas(4, "Libra Esterlina / Real Brasileiro"))

        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, lista)

        binding.moedasAu.setAdapter(arrayAdapter)
    }
}