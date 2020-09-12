package dev.dhg.apimidias;

import dev.dhg.apimidias.controller.MediaController;
import dev.dhg.apimidias.service.MediaService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ApiMidiasApplicationTests {

	@Autowired
	private MediaController mediaController;

	@Autowired
	private MediaService mediaService;

	@Test
	void contextLoads() {
		Assertions.assertThat(mediaController).isNotNull();
		Assertions.assertThat(mediaService).isNotNull();
	}

}
