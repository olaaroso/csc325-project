module csc.project.csc325_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires eu.hansolo.tilesfx;

    opens csc.project.csc325_project to javafx.fxml;
    exports csc.project.csc325_project;
}