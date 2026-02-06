package examples;

import ac4y.utility.StringHandler;
import ac4y.utility.GUIDHandler;
import ac4y.utility.DateHandler;
import ac4y.base.Ac4yProcess;
import ac4y.base.Ac4yException;

import java.util.Date;

/**
 * Egyszerű példa az ac4y modulok használatára
 */
public class SimpleExample {

    public static void main(String[] args) {

        // 1. String kezelés példa
        StringHandler stringHandler = new StringHandler();
        String input = "Ékezetes Szöveg 123!";
        String simplified = stringHandler.getSimpled(input);
        System.out.println("Eredeti: " + input);
        System.out.println("Egyszerűsített: " + simplified);

        // 2. GUID generálás példa
        GUIDHandler guidHandler = new GUIDHandler();
        String uuid = guidHandler.getGUID();
        System.out.println("Generált UUID: " + uuid);

        // 3. Dátum kezelés példa
        DateHandler dateHandler = new DateHandler();
        Date today = new Date();
        Date tomorrow = dateHandler.getShiftedDate(today, 1);
        String dateString = dateHandler.getStringFromDate(tomorrow);
        System.out.println("Holnap dátuma: " + dateString);

        // 4. Process példa
        MySimpleProcess process = new MySimpleProcess();
        try {
            Object result = process.process("Input data");
            System.out.println("Process eredmény: " + result);
        } catch (Ac4yException e) {
            e.printStackTrace();
        }
    }

    /**
     * Példa Process implementáció
     */
    static class MySimpleProcess extends Ac4yProcess {

        @Override
        public Object process(Object input) throws Ac4yException {
            try {
                // Egyszerű feldolgozási logika
                String data = (String) input;

                // String műveletek
                StringHandler sh = new StringHandler();
                String processed = sh.getSimpled(data);

                return "Processed: " + processed;

            } catch (Exception e) {
                throw new Ac4yException("Processing failed: " + e.getMessage());
            }
        }
    }
}
