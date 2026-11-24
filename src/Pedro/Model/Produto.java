package Pedro.Model;

public abstract class Produto {
    protected int id;
    protected String nome;
    protected String descricao;

    //Construtor que ira armazenar os dados do Produto
    public Produto(int id, String nome, String descricao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
    }

    //metodos abstratos que irá ser usado para calcular o preco e mexer no estoque
    public abstract double calcularPreco();

    public abstract boolean verificarEstoque(int quantidade);

    public abstract void diminuirEstoque(int quantidade);

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

}
