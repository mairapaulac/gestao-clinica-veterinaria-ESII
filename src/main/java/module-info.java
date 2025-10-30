module br.edu.clinica.clinicaveterinaria {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    //requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;

    opens br.edu.clinica.clinicaveterinaria to javafx.fxml;
    opens br.edu.clinica.clinicaveterinaria.controller to javafx.fxml;
    exports br.edu.clinica.clinicaveterinaria;
    exports br.edu.clinica.clinicaveterinaria.controller;
}