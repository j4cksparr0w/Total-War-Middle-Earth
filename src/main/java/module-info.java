module com.marija.middleearthbattle {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires java.rmi;
    requires java.naming;
    requires jdk.naming.rmi;
    requires javafx.media;

    opens com.marija.middleearthbattle to javafx.fxml;

    exports com.marija.middleearthbattle;
    exports com.marija.middleearthbattle.rmi;
}