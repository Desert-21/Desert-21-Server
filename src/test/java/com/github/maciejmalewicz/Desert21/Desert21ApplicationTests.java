package com.github.maciejmalewicz.Desert21;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Desert21ApplicationTests {

	@Value("${spring.addresses.baseUrl}")
	private String address;

	@Test
	void contextLoads() {
		System.out.println(address);
	}

}
