package br.com.DnotasTecnologia.Dnotas.activities;

public class Transacao {
    private String valor1;
    private String valor2;
    private String valor3;
    private String valor4;
    private String valor5;
    private String valor6;
    private String valor7;
    private String valor8;
    private String stoneCode;
    private String authorizationCode;

    public Transacao(String valor1, String valor2, String valor3, String valor4, String valor5, String valor6, String valor7, String valor8, String stoneCode, String authorizationCode) {
        this.valor1 = valor1;
        this.valor2 = valor2;
        this.valor3 = valor3;
        this.valor4 = valor4;
        this.valor5 = valor5;
        this.valor6 = valor6;
        this.valor7 = valor7;
        this.valor8 = valor8;
        this.stoneCode = stoneCode;
        this.authorizationCode = authorizationCode;
    }

    // Getters e Setters
    public String getValor1() { return valor1; }
    public void setValor1(String valor1) { this.valor1 = valor1; }

    public String getValor2() { return valor2; }
    public void setValor2(String valor2) { this.valor2 = valor2; }

    public String getValor3() { return valor3; }
    public void setValor3(String valor3) { this.valor3 = valor3; }

    public String getValor4() { return valor4; }
    public void setValor4(String valor4) { this.valor4 = valor4; }

    public String getValor5() { return valor5; }
    public void setValor5(String valor5) { this.valor5 = valor5; }

    public String getValor6() { return valor6; }
    public void setValor6(String valor6) { this.valor6 = valor6; }

    public String getValor7() { return valor7; }
    public void setValor7(String valor7) { this.valor7 = valor7; }

    public String getValor8() { return valor8; }
    public void setValor8(String valor8) { this.valor8 = valor8; }

    public String getStoneCode() { return stoneCode; }
    public void setStoneCode(String stoneCode) { this.stoneCode = stoneCode; }

    public String getauthorizationCode() { return authorizationCode; }
    public void setauthorizationCode(String authorizationCode) { this.authorizationCode = authorizationCode; }
}
