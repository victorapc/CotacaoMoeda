package br.com.inux.cotacaomoeda.model

data class Moedas (
    val id: Int,
    val moeda: String,
    val tipoMoeda: String
) {
    override fun toString(): String {
        return moeda
    }
}