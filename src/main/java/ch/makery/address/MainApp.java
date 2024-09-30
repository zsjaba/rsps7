package ch.makery.address;

import java.io.IOException;
import java.io.File;  // Для класса File

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import ch.makery.address.model.Club;
import ch.makery.address.model.ClubListWrapper;
import ch.makery.address.view.ClubOverviewController;
import ch.makery.address.view.ClubEditDialogController;
import ch.makery.address.view.RootLayoutController;

import java.util.prefs.Preferences;  // Для класса Preferences
import javax.xml.bind.JAXBContext;  // Для JAXBContext
import javax.xml.bind.Marshaller;  // Для Marshaller
import javax.xml.bind.Unmarshaller;  // Для Unmarshaller


public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;

    /**
     * The data as an observable list of Clubs.
     */
    private ObservableList<Club> ClubData = FXCollections.observableArrayList();

    /**
     * Constructor
     */
    public MainApp() {
        // Add some sample data
        ClubData.add(new Club("МБОУ СОШ №1", "Клуб робототехники \"ТехноЛидеры\""));
        ClubData.add(new Club("МБОУ СОШ №2", "Спортивный клуб \"Звезда\""));
        ClubData.add(new Club("МБОУ СОШ №3", "Театральный кружок \"Сцена\""));
        ClubData.add(new Club("МБОУ СОШ №4", "Литературный клуб \"Слово\""));
        ClubData.add(new Club("МБОУ СОШ №5", "Клуб программирования \"Кодеры\""));
        ClubData.add(new Club("МБОУ СОШ №1", "Художественный кружок \"Мастера кисти\""));
        ClubData.add(new Club("МБОУ СОШ №2", "Шахматный клуб \"Гроссмейстер\""));
        ClubData.add(new Club("МБОУ СОШ №3", "Клуб фотоискусства \"Взгляд\""));
        ClubData.add(new Club("МБОУ СОШ №4", "Картинговый кружок \"Красный - значит быстрый\" "));
    }

    /**
     * Returns the data as an observable list of Clubs.
     * @return
     */
    public ObservableList<Club> getClubData() {
        return ClubData;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("ClubApp");

        this.primaryStage.getIcons().add(new Image("2124225_card_essential_app_credit_icon.png"));

        initRootLayout();

        showClubOverview();
    }

    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/ch/makery/address/viev/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            //Scene scene = new Scene(rootLayout);
            //primaryStage.setScene(scene);
            primaryStage.setScene(new Scene(rootLayout, 1200 , 520));
            primaryStage.setResizable(false);
            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Пытается загрузить последний открытый файл с адресатами.
        File file = getClubFilePath();
        if (file != null) {
            loadClubDataFromFile(file);
        }
    }

    /**
     * Shows the Club overview inside the root layout.
     */
    public void showClubOverview() {
        try {
            // Load Club overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/ch/makery/address/viev/ClubOverview.fxml"));
            AnchorPane ClubOverview = (AnchorPane) loader.load();

            // Set Club overview into the center of root layout.
            rootLayout.setCenter(ClubOverview);

            // Give the controller access to the main app.
            ClubOverviewController controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Открывает диалоговое окно для изменения деталей указанного адресата.
     * Если пользователь кликнул OK, то изменения сохраняются в предоставленном
     * объекте адресата и возвращается значение true.
     *
     * @param club - объект адресата, который надо изменить
     * @return true, если пользователь кликнул OK, в противном случае false.
     */
    public boolean showClubEditDialog(Club club) {
        try {
            // Загружаем fxml-файл и создаём новую сцену
            // для всплывающего диалогового окна.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/ch/makery/address/viev/ClubEditDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Создаём диалоговое окно Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Club");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Передаём адресата в контроллер.
            ClubEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setClub(club);

            // Отображаем диалоговое окно и ждём, пока пользователь его не закроет
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Возвращает preference файла адресатов, то есть, последний открытый файл.
     * Этот preference считывается из реестра, специфичного для конкретной
     * операционной системы. Если preference не был найден, то возвращается null.
     *
     * @return
     */
    public File getClubFilePath() {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        String filePath = prefs.get("filePath", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    /**
     * Задаёт путь текущему загруженному файлу. Этот путь сохраняется
     * в реестре, специфичном для конкретной операционной системы.
     *
     * @param file - файл или null, чтобы удалить путь
     */
    public void setClubFilePath(File file) {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        if (file != null) {
            prefs.put("filePath", file.getPath());
            System.out.println("File path saved: " + file.getPath());

            // Обновление заглавия сцены.
            primaryStage.setTitle("ClubApp - " + file.getName());
        } else {
            prefs.remove("filePath");
            System.out.println("File path removed");

            // Обновление заглавия сцены.
            primaryStage.setTitle("ClubApp");
        }
    }
    /**
     * Загружает информацию об адресатах из указанного файла.
     * Текущая информация об адресатах будет заменена.
     *
     * @param file
     */
    public void loadClubDataFromFile(File file) {
        try {
            JAXBContext context = JAXBContext
                    .newInstance(ClubListWrapper.class);
            Unmarshaller um = context.createUnmarshaller();

            // Чтение XML из файла и демаршализация.
            ClubListWrapper wrapper = (ClubListWrapper) um.unmarshal(file);

            ClubData.clear();
            ClubData.addAll(wrapper.getClubs());

            // Сохраняем путь к файлу в реестре.
            setClubFilePath(file);

        } catch (Exception e) { // catches ANY exception
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load data");
            alert.setContentText("Could not load data from file:\n" + file.getPath());

            alert.showAndWait();
        }
    }

    /**
     * Сохраняет текущую информацию об адресатах в указанном файле.
     *
     * @param file
     */
    public void saveClubDataToFile(File file) {
        try {
            JAXBContext context = JAXBContext.newInstance(ClubListWrapper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Обёртываем наши данные об адресатах.
            ClubListWrapper wrapper = new ClubListWrapper();
            wrapper.setClubs(ClubData);

            // Маршаллируем и сохраняем XML в файл.
            m.marshal(wrapper, file);

            // Сохраняем путь к файлу в реестре.
            setClubFilePath(file);
        } catch (Exception e) {
            // Вывод информации об ошибке в консоль
            e.printStackTrace();

            // Создание уведомления об ошибке
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not save data");
            alert.setContentText("Could not save data to file:\n" + file.getPath() + "\nError: " + e.getMessage());

            alert.showAndWait();
        }
    }


    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}