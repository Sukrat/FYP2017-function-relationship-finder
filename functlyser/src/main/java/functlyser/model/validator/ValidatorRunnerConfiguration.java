package functlyser.model.validator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidatorRunnerConfiguration {

    @Bean
    public ValidatorRunner<DataValidator> dataValidator() {
        return new ValidatorRunner<>(new DataValidator());
    }
}
