package ScatterAnalyzer;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Label;

import java.awt.*;
import java.io.File;
import java.io.InputStream;


public class ScatterAnalyser extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Root.fxml"));
        primaryStage.setTitle("Scatter Analyzer");
        primaryStage.getIcons().add(new Image( ScatterAnalyser.class.getResourceAsStream( "CrocoLogo.png" )));
        primaryStage.setScene(new Scene(root, 640, 480));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    public void showGraph(int spectra[], int E0, double dE, String name)
    {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("SCATTER ONE LOVE");
        dialogStage.getIcons().add(new Image( ScatterAnalyser.class.getResourceAsStream( "CrocoLogo.png" )));
        dialogStage.initModality(Modality.NONE);
        NumberAxis x = new NumberAxis();
        NumberAxis y = new NumberAxis();
        LineChart<Number, Number> spectra1 = new LineChart<Number, Number>(x,y);
        XYChart.Series series1 = new XYChart.Series();
        series1.setName(name);
        ObservableList<XYChart.Data> datas = FXCollections.observableArrayList();
        for(int i=0; i<=(int) Math.round(E0 / dE); i++){
            datas.add(new XYChart.Data(i*dE,spectra[i]));
        }

        series1.setData(datas);

        Scene scene = new Scene(spectra1, 600,600);
        spectra1.getData().add(series1);
        dialogStage.setScene(scene);
        dialogStage.show();

    }
    public void Futurama()
    {
        Class<?> clazz = this.getClass();
        Stage HelpStage = new Stage();
        HelpStage.getIcons().add(new Image( ScatterAnalyser.class.getResourceAsStream( "CrocoLogo.png" )));
        HelpStage.setTitle("You do what you gotta do!");
        HelpStage.initModality(Modality.NONE);
        InputStream input2 = clazz.getResourceAsStream("yougottado.png");
        Image image2 = new Image(input2, 600, 800, false, true);
        ImageView imageView2 = new ImageView(image2);
        FlowPane root = new FlowPane();
        //root.setPadding(new Insets(20));
        root.getChildren().add( imageView2);
        //Label notification = new Label();
        //notification.setText("Установите угол  в пределах 0<t<90");
        StackPane root1 = new StackPane();
        //root.getChildren().add(notification);
        Scene scene2 = new Scene(root,600,800);
        HelpStage.setScene(scene2);
        HelpStage.show();
    }
    public void showHelpPage()
    {
        Class<?> clazz = this.getClass();
        Stage HelpStage = new Stage();
        HelpStage.getIcons().add(new Image( ScatterAnalyser.class.getResourceAsStream( "CrocoLogo.png" )));
        HelpStage.setTitle("Справка");
        HelpStage.initModality(Modality.NONE);
        InputStream input2 = clazz.getResourceAsStream("axes.png");
        Image image2 = new Image(input2, 800, 659, false, true);
        ImageView imageView2 = new ImageView(image2);
        FlowPane root = new FlowPane();
        //root.setPadding(new Insets(20));
        root.getChildren().add( imageView2);
        //Label notification = new Label();
        //notification.setText("Установите угол  в пределах 0<t<90");
        StackPane root1 = new StackPane();
        //root.getChildren().add(notification);
        Scene scene2 = new Scene(root,800,650);
        HelpStage.setScene(scene2);
        HelpStage.show();
    }
    public void showNotification(String notificationText )
    {
        Stage NotificationStage = new Stage();
        NotificationStage.getIcons().add(new Image( ScatterAnalyser.class.getResourceAsStream( "CrocoLogo.png" )));
        NotificationStage.setTitle("Предупреждение");
        NotificationStage.initModality(Modality.NONE);
        Label notification = new Label();
        notification.setText(""+notificationText);
        StackPane root = new StackPane();
        root.getChildren().add(notification);
        Scene scene2 = new Scene(root,300,70);
        NotificationStage.setScene(scene2);
        NotificationStage.show();
    }

    public void showNotificationAboutFile()
    {
        Stage NotificationStage = new Stage();
        NotificationStage.getIcons().add(new Image( ScatterAnalyser.class.getResourceAsStream( "CrocoLogo.png" )));
        NotificationStage.setTitle("Предупреждение");
        NotificationStage.initModality(Modality.NONE);
        Label notification = new Label();
        notification.setText("Выберите файл!");
        StackPane root = new StackPane();
        root.getChildren().add(notification);
        Scene scene2 = new Scene(root,300,70);
        NotificationStage.setScene(scene2);
        NotificationStage.show();
    }
    public void showNotificationAboutFileType()
    {
        Stage NotificationStage = new Stage();
        NotificationStage.getIcons().add(new Image( ScatterAnalyser.class.getResourceAsStream( "CrocoLogo.png" )));
        NotificationStage.setTitle("Предупреждение");
        NotificationStage.initModality(Modality.NONE);
        Label notification = new Label();
        notification.setText("Это не тот файл, что вы ищете!");
        StackPane root = new StackPane();
        root.getChildren().add(notification);
        Scene scene2 = new Scene(root,300,70);
        NotificationStage.setScene(scene2);
        NotificationStage.show();
    }
}
