package com.example.ProjetBatch.service;

import com.example.ProjetBatch.model.Employee;
import com.example.ProjetBatch.model.Payslip;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

@Service
public class PayslipPdfService {

    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("MM/yyyy");

    public String generatePdf(Employee emp, Payslip payslip) throws IOException {
       
       
        String outputDir = "/app/payslips";
        new File(outputDir).mkdirs();
        
        String fileName = "payslip-" + emp.getId() + "-" + payslip.getMonth() + ".pdf";
        String filePath = outputDir + "/" + fileName;

        // 2. Initialisation du document
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                
                float y = 750; // Position verticale de départ (haut de page)
                
         
                try {
                    InputStream imageStream = getClass().getResourceAsStream("/logo.png");
                    if (imageStream != null) {
                        PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, imageStream.readAllBytes(), "logo");
                        // Affiche l'image en haut à gauche, redimensionnée (width=100)
                        cs.drawImage(pdImage, 50, y, 100, 50); 
                    }
                } catch (Exception e) {
                    System.err.println("Logo non trouvé ou erreur: " + e.getMessage());
                }

                // --- B. EN-TÊTE ENTREPRISE ---
                drawText(cs, "ENTREPRISE IMA", 400, y + 30, PDType1Font.HELVETICA_BOLD, 14, Color.BLACK);
                drawText(cs, "3 Place André Leroy", 400, y + 15, PDType1Font.HELVETICA, 10, Color.GRAY);
                drawText(cs, " 49000 Angers", 400, y, PDType1Font.HELVETICA, 10, Color.GRAY);
                drawText(cs, "SIRET: 123 456 789 00012", 400, y - 15, PDType1Font.HELVETICA, 10, Color.GRAY);

                y -= 60; // On descend

                // --- C. TITRE ---
                drawCenteredText(cs, "BULLETIN DE PAIE", 800, y, PDType1Font.HELVETICA_BOLD, 18, Color.DARK_GRAY);
                y -= 30;
                drawCenteredText(cs, "Période : " + payslip.getMonth().format(dateFmt), 800, y, PDType1Font.HELVETICA_BOLD, 12, Color.BLACK);

                y -= 50;

                // --- D. CADRE EMPLOYÉ ---
                // On dessine un rectangle gris clair pour l'employé
                cs.setNonStrokingColor(Color.LIGHT_GRAY);
                cs.addRect(40, y - 60, 515, 70); 
                // (Note: addRect x, y, width, height -> attention y est le bas du rectangle)
                cs.stroke(); // Dessine le contour seulement
                cs.setNonStrokingColor(Color.BLACK); // Reset couleur texte

                drawText(cs, "Matricule : " + emp.getId(), 50, y, PDType1Font.HELVETICA, 10, Color.BLACK);
                drawText(cs, "Nom Prénom : " + emp.getLastName().toUpperCase() + " " + emp.getFirstName(), 50, y - 15, PDType1Font.HELVETICA_BOLD, 12, Color.BLACK);
                drawText(cs, "Email : " + emp.getEmail(), 50, y - 30, PDType1Font.HELVETICA, 10, Color.BLACK);
                
                // Gestion du poste (avec sécurité null)
                String nomPoste = (emp.getPoste() != null) ? emp.getPoste().getName() : "Non Défini";
                drawText(cs, "Poste : " + nomPoste, 300, y - 15, PDType1Font.HELVETICA_BOLD, 12, Color.BLACK);

                y -= 100;

                // --- E. TABLEAU DE SALAIRE ---
                // En-têtes du tableau
                drawTableLine(cs, y, "RUBRIQUE", "BASE", "TAUX", "GAIN", "RETENUE", true);
                y -= 20;

                // 1. Salaire de base
                double tauxHoraire = payslip.getHourlyRate();
                double heures = payslip.getTotalHours();
                double brut = payslip.getGross();
                
                drawTableLine(cs, y, "Salaire de base", df.format(heures), df.format(tauxHoraire), df.format(brut), "", false);
                y -= 15;

           
                y -= 10;

               
                
                double sante = brut * 0.07;    // 7%
                double retraite = brut * 0.10; // 10%
                double csg = brut * 0.05;      // 5%
                
                drawTableLine(cs, y, "Sécurité Sociale - Maladie", df.format(brut), "7.00 %", "", df.format(sante), false);
                y -= 15;
                drawTableLine(cs, y, "Retraite Complémentaire", df.format(brut), "10.00 %", "", df.format(retraite), false);
                y -= 15;
                drawTableLine(cs, y, "CSG / CRDS", df.format(brut), "5.00 %", "", df.format(csg), false);
                y -= 30;

                // Ligne de séparation Totaux
                cs.moveTo(40, y);
                cs.lineTo(555, y);
                cs.stroke();
                y -= 25;

                // --- G. TOTAUX ET NET ---
                drawText(cs, "Total Brut :", 350, y, PDType1Font.HELVETICA, 12, Color.BLACK);
                drawRightText(cs, df.format(brut) + " €", 550, y, PDType1Font.HELVETICA, 12);
                y -= 20;

                // Grosse boite pour le Net
                cs.setNonStrokingColor(new Color(220, 220, 220)); // Gris très clair
                cs.addRect(340, y - 15, 215, 30);
                cs.fill();
                cs.setNonStrokingColor(Color.BLACK);

                drawText(cs, "NET À PAYER :", 350, y, PDType1Font.HELVETICA_BOLD, 14, Color.BLACK);
                drawRightText(cs, df.format(payslip.getNet()) + " €", 550, y, PDType1Font.HELVETICA_BOLD, 14);

                y -= 40;

                // --- I. SECTION CONGÉS / ABSENCES ---
                drawText(cs, "DÉTAIL DES ABSENCES / CONGÉS :", 50, y, PDType1Font.HELVETICA_BOLD, 10, Color.BLACK);
                y -= 15;
                
                String info = payslip.getLeavesInfo();
                if (info != null) {
                    // On découpe par ligne (\n) pour afficher proprement
                    for (String line : info.split("\n")) {
                        drawText(cs, line, 50, y, PDType1Font.HELVETICA, 10, Color.DARK_GRAY);
                        y -= 12;
                    }}

                // --- H. PIED DE PAGE ---
                y = 50;
                drawCenteredText(cs, "Pour faire valoir ce que de droit.", 800, y, PDType1Font.HELVETICA_OBLIQUE, 10, Color.GRAY);
                drawCenteredText(cs, "Document généré automatiquement par ProjetRH.", 800, y - 15, PDType1Font.HELVETICA, 8, Color.LIGHT_GRAY);
            }

            document.save(filePath);
        }

        return filePath;
    }

    // --- MÉTHODES UTILITAIRES POUR DESSINER PROPREMENT ---

    private void drawText(PDPageContentStream cs, String text, float x, float y, PDType1Font font, int size, Color color) throws IOException {
        cs.beginText();
        cs.setFont(font, size);
        cs.setNonStrokingColor(color);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
    }

    private void drawCenteredText(PDPageContentStream cs, String text, float pageHeight, float y, PDType1Font font, int size, Color color) throws IOException {
        float textWidth = font.getStringWidth(text) / 1000 * size;
        float pageWidth = PDRectangle.A4.getWidth();
        float x = (pageWidth - textWidth) / 2;
        drawText(cs, text, x, y, font, size, color);
    }

    private void drawRightText(PDPageContentStream cs, String text, float xEnd, float y, PDType1Font font, int size) throws IOException {
        float textWidth = font.getStringWidth(text) / 1000 * size;
        drawText(cs, text, xEnd - textWidth, y, font, size, Color.BLACK);
    }

    // Dessine une ligne du tableau (Header ou Data)
    private void drawTableLine(PDPageContentStream cs, float y, String c1, String c2, String c3, String c4, String c5, boolean isHeader) throws IOException {
        int size = isHeader ? 11 : 10;
        PDType1Font font = isHeader ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA;
        Color color = isHeader ? Color.DARK_GRAY : Color.BLACK;

        // Positions X des colonnes
        float col1 = 50;  // Rubrique
        float col2 = 250; // Base
        float col3 = 320; // Taux
        float col4 = 400; // Gain
        float col5 = 480; // Retenue

        drawText(cs, c1, col1, y, font, size, color); // Gauche
        if(!c2.isEmpty()) drawRightText(cs, c2, col2 + 40, y, font, size); // Alignement Droite
        if(!c3.isEmpty()) drawRightText(cs, c3, col3 + 40, y, font, size);
        if(!c4.isEmpty()) drawRightText(cs, c4, col4 + 60, y, font, size);
        if(!c5.isEmpty()) drawRightText(cs, c5, col5 + 60, y, font, size);

        // Petite ligne grise sous chaque ligne
        cs.setStrokingColor(Color.LIGHT_GRAY);
        cs.moveTo(40, y - 5);
        cs.lineTo(555, y - 5);
        cs.stroke();
    }
}