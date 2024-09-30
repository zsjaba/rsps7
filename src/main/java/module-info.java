module com.example.addressapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs; // Для Preferences
    requires java.xml.bind; // Для JAXB

    opens ch.makery.address.model to java.xml.bind;
    opens ch.makery.address to javafx.fxml;
    opens ch.makery.address.view to javafx.fxml; // открыть пакет с контроллерами для javafx.fxml

    exports ch.makery.address;
}
