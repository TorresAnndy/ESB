package com.andy;

import java.util.List;

import org.apache.camel.builder.RouteBuilder;

public class InventoryRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("file:src/main/resources/data-txt?fileName=inventariotxt.csv&noop=true")
                .routeId("erp-to-crm-route-txt")
                .log("📥 Archivo recibido: ${header.CamelFileName}")
                .unmarshal().csv()
                .split(body())
                .process(exchange -> {
                    // cada body es una List<String> que representa una fila
                    List<String> row = exchange.getIn().getBody(List.class);
                    String line = String.join(",", row);
                    exchange.getIn().setBody(line + System.lineSeparator());
                })
                .log("📤 Enviando al CRM: ${body}")
                .to("file:src/main/resources/output-txt?fileName=crm_output.txt&fileExist=Append")
                .end();

    }
}