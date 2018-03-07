package functlyser;

import functlyser.repository.ArangoOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    @Autowired
    private ArangoOperation operation;

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }
}