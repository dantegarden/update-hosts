package com.dantegarden.updatehosts;

import com.dantegarden.updatehosts.dao.DomainRepository;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;


@SpringBootApplication
public class UpdateHostsApplication extends Application {

    private static ConfigurableApplicationContext applicationContext;
    public static Stage primaryStage;

    public static void main(String[] args) {
        applicationContext = SpringApplication.run(UpdateHostsApplication.class, args);
        Application.launch(args); //启动javafx
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        UpdateHostsApplication.primaryStage = primaryStage;
        primaryStage.setTitle("更新hosts小工具");
        primaryStage.setScene(new Scene(root(), 900, 300));
        primaryStage.show(); //显示窗口
    }

    public Parent root() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(UpdateHostsApplication.class.getResource("/main.fxml"));
        loader.setControllerFactory(applicationContext::getBean); //得到上下文中的MainController
        //窗体内容通过加载fxml实现
        return loader.load();
    }
}
