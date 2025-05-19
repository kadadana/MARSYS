module com.marsys.marsys {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires com.zaxxer.hikari;
    requires annotations;

    opens com.marsys.marsys to javafx.fxml;
    exports com.marsys.marsys;
    exports com.marsys.marsys.Controllers;
    exports com.marsys.marsys.Models;
    opens com.marsys.marsys.Controllers to javafx.fxml;


}