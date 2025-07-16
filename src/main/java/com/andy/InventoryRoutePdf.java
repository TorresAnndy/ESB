package com.andy;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.ByteArrayOutputStream;


public class InventoryRoutePdf extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("file:src/main/resources/data-pdf?fileName=inventariopdf.csv&noop=true")
            .routeId("erp-to-crm-route-pdf")
            .log("📥 Archivo recibido: ${header.CamelFileName}")
            .unmarshal().csv()
            .process(new Processor() {
                @Override
                public void process(Exchange exchange) throws Exception {
                    // Obtener lista de filas: List<List<String>>
                    List<List<String>> csvData = exchange.getIn().getBody(List.class);

                    // Crear PDF
                    PDDocument document = new PDDocument();
                    PDPage page = new PDPage();
                    document.addPage(page);

                    PDPageContentStream contentStream = new PDPageContentStream(document, page);

                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(25, 750);

                    for (List<String> row : csvData) {
                        String line = String.join(" | ", row);
                        contentStream.showText(line);
                        contentStream.newLineAtOffset(0, -15); // baja línea para siguiente texto
                    }

                    contentStream.endText();
                    contentStream.close();

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    document.save(baos);
                    document.close();

                    // Establecer body con contenido binario del PDF
                    exchange.getIn().setBody(baos.toByteArray());
                }
            })
            .to("file:src/main/resources/output-pdf?fileName=crm_output.pdf&fileExist=Override");
    }
}
