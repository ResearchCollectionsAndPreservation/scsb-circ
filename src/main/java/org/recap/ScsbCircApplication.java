package org.recap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import brave.sampler.Sampler;

/**
 * The type SCSB Circulation Application.
 */
@PropertySource("classpath:application.properties")
@SpringBootApplication
public class ScsbCircApplication {

	/**
	 * The entry point of application.
	 *
	 * @param args the input arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(ScsbCircApplication.class, args);
	}
	
    @Bean
    public Sampler defaultSampler() {
          return Sampler.ALWAYS_SAMPLE;
    }
}
