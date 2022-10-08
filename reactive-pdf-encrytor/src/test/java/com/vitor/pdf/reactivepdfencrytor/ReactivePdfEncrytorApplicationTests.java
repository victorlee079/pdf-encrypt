package com.vitor.pdf.reactivepdfencrytor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;

@ExtendWith(SpringExtension.class)
@WebFluxTest
class ReactivePdfEncrytorApplicationTests {
	@Autowired
	private WebTestClient webClient;

	@Test
	void shouldEncryptPdf() throws FileNotFoundException, IOException {
		File file = new File(
				"/Users/victorlee/Documents/GitHub/pdf-encrypt/reactive-pdf-encrytor/src/test/resources/spring-reference.pdf");
		MultipartFile multipartFile = new MockMultipartFile("spring-reference.pdf", new FileInputStream(file));
		MultipartBodyBuilder builder = new MultipartBodyBuilder();
		builder.part("file", multipartFile.getResource());
		builder.part("uPwd", "Abcd123");
		builder.part("oPwd", "Abcd1234");
		System.out.println("----- Testing by WebClient -----");
		webClient.post()
				.uri("/api/encrpyt")
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.body(BodyInserters.fromMultipartData(builder.build()))
				.exchange()
				.expectBody();
	}

}
