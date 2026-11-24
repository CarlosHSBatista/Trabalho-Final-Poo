package com.loja.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javafx.scene.control.Label;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Cria o texto que vai aparecer na janela
        Label mensagem = new Label("Bem-vindo a loja!");

        StackPane caixa = new StackPane(mensagem);

        //cria uma caixa
        Scene cena = new Scene(caixa, 600,400);

        //vai coloca a cena na janela, da titulo pra janela e mostrar a janela
        stage.setScene(cena);
        stage.setTitle("Bem-vindo a Loja BumBum");
        stage.show();
    }

}
