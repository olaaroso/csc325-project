module csc.project.csc325_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires eu.hansolo.tilesfx;

    requires firebase.admin;
    requires com.google.auth;
    requires com.google.auth.oauth2;
    requires google.cloud.firestore;
    requires google.cloud.core;
    requires com.google.api.apicommon;
    requires java.net.http;
    requires com.google.gson;

    exports com.group4.macromanager;
    exports com.group4.macromanager.controller;
    exports com.group4.macromanager.model;

    opens com.group4.macromanager to javafx.fxml;
    opens com.group4.macromanager.controller to javafx.fxml;
    opens com.group4.macromanager.model;
    // opens com.group4.macromanager.model to com.google.gson, com.google.cloud.firestore;
}