package com.andy;

import org.apache.camel.main.Main;

public class MainApp {
    public static void main(String[] args) throws Exception {
        Main main = new Main();
        
        var config = main.configure();
        config.addRoutesBuilder(new InventoryRoute());
        config.addRoutesBuilder(new InventoryRoutePdf());

        main.run(args);
    }
}
