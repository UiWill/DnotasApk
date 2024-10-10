package br.com.DnotasTecnologia.Dnotas.activities;

public class CupomRequest {
    private String stoneCode;
    private String cpfCnpj;
    private String valor;

    public CupomRequest(String stoneCode, String cpfCnpj, String valor) {
        this.stoneCode = stoneCode;
        this.cpfCnpj = cpfCnpj;
        this.valor = valor;
    }

    public String getStoneCode() {
        return stoneCode;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public String getValor() {
        return valor;
    }
}
