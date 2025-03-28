package services;

import java.util.List;

/*
 * Interface para cálculo de parcelas e amortização de financiamentos.
 */
public interface Amortizacao {

    List<Double> calculaParcela(double valorFinanciamento, double taxaJuros, int prazo);
    List<Double> calculaAmortizacao(double valorFinanciamento, double taxaJuros, int prazo);
}