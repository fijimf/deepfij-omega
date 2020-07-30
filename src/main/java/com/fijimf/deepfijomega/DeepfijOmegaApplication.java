package com.fijimf.deepfijomega;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

@SpringBootApplication
public class DeepfijOmegaApplication {

	public static void main(String[] args) {
		for (Map.Entry p: System.getProperties().entrySet()){
			System.out.println(p);
		}
		SpringApplication.run(DeepfijOmegaApplication.class, args);
	}

}
