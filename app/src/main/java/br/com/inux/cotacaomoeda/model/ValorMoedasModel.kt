package br.com.inux.cotacaomoeda.model

data class ValorMoedasModel(
    val ask: Double,
    val bid: Double,
    val code: String,
    val codein: String,
    val create_date: String,
    val high: Double,
    val low: Double,
    val name: String,
    val pctChange: Double,
    val timestamp: String,
    val varBid: Double
)