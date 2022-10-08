package com.vitor.pdf.reactivepdfencryptor.handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/api")
public class EncryptionController {

    @GetMapping("/encrypt")
    public ResponseEntity<Mono<Resource>> encrypt(@RequestParam("file") MultipartFile pdfFile,
            @RequestParam("uPwd") String userPwd, @RequestParam("oPwd") String ownPwd) {

        System.out.println("uPwd: " + userPwd);
        System.out.println("ownPwd: " + ownPwd);

        Mono<ByteArrayInputStream> response = Mono.fromCallable(() -> {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfDocument pdfDoc = new PdfDocument(
                    new PdfReader(pdfFile.getInputStream()),
                    new PdfWriter(out, new WriterProperties().setStandardEncryption(
                            userPwd.getBytes(),
                            ownPwd.getBytes(),
                            EncryptionConstants.ALLOW_PRINTING,
                            EncryptionConstants.ENCRYPTION_AES_128 | EncryptionConstants.DO_NOT_ENCRYPT_METADATA)));
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            pdfDoc.close();
            return in;
        }).subscribeOn(Schedulers.boundedElastic());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + pdfFile.getName())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .body(response.flatMap(x -> {
                    Resource resource = new InputStreamResource(x);
                    return Mono.just(resource);
                }));
    }
}
