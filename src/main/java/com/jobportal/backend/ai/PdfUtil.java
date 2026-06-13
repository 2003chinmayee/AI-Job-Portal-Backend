package com.jobportal.backend.ai;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;

public class PdfUtil {

    public static String extractText(File file) {

        try (PDDocument document = Loader.loadPDF(file)) {

            PDFTextStripper pdfStripper = new PDFTextStripper();

            return pdfStripper.getText(document);

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}