package entities;


public class Cliente {
    private String cpf;
    private String nome;
    private double rendaMensal;

    // Construtor vazio necessário para o Gson
    public Cliente() {}

    // Construtor com parâmetros
    public Cliente(String nome, String cpf, double rendaMensal) {
        this.nome = nome;
        this.cpf = cpf;
        this.rendaMensal = rendaMensal;
    }

    // Getters e Setters para todos os campos
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public double getRendaMensal() { return rendaMensal; }
    public void setRendaMensal(double rendaMensal) { this.rendaMensal = rendaMensal; }
}
