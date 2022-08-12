package test.challenge.hr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Paolo Cariaso
 * @date 8/8/2022 3:36 PM
 */
@SpringBootApplication
@ComponentScan(value = "io.challenge.hr")
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
