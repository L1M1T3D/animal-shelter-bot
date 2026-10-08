
package ru.skypro.animalshelter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Проверяет запуск Spring Boot контекста с тестовой базой H2.
 */
@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:animal_shelter_test;DB_CLOSE_DELAY=-1",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"telegram.bot.enabled=false"
})
class AnimalShelterApplicationTests {

	@Test
	void contextLoads() {
	}
}
