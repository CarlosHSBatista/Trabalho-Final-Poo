package com.loja.model;

public class Item {
    protected int quantidade;
    protected double precoUnitario;

    public double calcularSubTotal(){
        return quantidade * precoUnitario;
    }
}
