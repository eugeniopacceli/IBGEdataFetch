package com.mycompany.ibge_crawler;

import Geography.Meso;
import Geography.Micro;
import Geography.Municipality;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class DataFetcher implements Initializable {

    @FXML
    private TextArea output;
    private Stage stage;
    private String xlsFile;

    // Reads a Excel input file and populates the Meso regions represented by Java objects.
    // http://www.sidra.ibge.gov.br/bda/territorio/download/meso.xls
    private ArrayList<Meso> readMesoSheet(Workbook wb) {
        ArrayList<Meso> mesos = new ArrayList<>();
        Sheet sheet = wb.getSheetAt(0);
        int row = 2;

        do {
            Meso meso = new Meso();
            meso.setUF(sheet.getRow(row).getCell(0).getStringCellValue());
            meso.setCode(Integer.toString((int) sheet.getRow(row).getCell(1).getNumericCellValue()));
            meso.setName(sheet.getRow(row).getCell(2).getStringCellValue());
            mesos.add(meso);
            writeToOutput(meso.getUF() + "\t" + meso.getCode() + "\t" + meso.getName() + "\n");
            row++;
        } while (sheet.getRow(row) != null
                && !sheet.getRow(row).getCell(0).getStringCellValue().isEmpty());
        writeToOutput("\n<-> End of spreadsheet <->\n\n");
        return mesos;
    }

    // Given a Meso region code (aka macro region in this code), fetches the JSON of it's Micro regions from IBGE'S SIDRA.
    private void fetchMicrosFromMesoToFile(String macroCode) throws IOException {
        String filePath = "queries/microPerMeso/" + macroCode + ".json";
        String requestUrl = "http://www.sidra.ibge.gov.br/api/values/t/1301/p/2010/v/615/N9/in%20N8%20" + macroCode;

        File f = new File(filePath);
        if (!(f.exists() && !f.isDirectory())) {
            writeToOutput("Fetching >> " + requestUrl + "\n");
            FileUtils.copyURLToFile(new URL(requestUrl), new File(filePath));
        } else {
            writeToOutput("Fetching nothing >> " + filePath + " already exists.\n");
        }
    }

    // Given an array of Meso regions, fetches all their Micro regions from IBGE'S SIDRA to JSON files.
    private void fetchAllMicrosFromAllMesosToFiles(ArrayList<Meso> mesos) throws IOException {
        for (Meso m : mesos) {
            fetchMicrosFromMesoToFile(m.getCode());
        }
        writeToOutput("\n<-> End of micro regions queries <->\n\n");
    }

    // Given a Micro region code (and the Micro's Meso parent)), fetches the JSON of it's Municipalities from IBGE'S SIDRA.
    private void fetchMunicipalitiesFromMicrosToFile(String macroCode, String microCode) throws IOException {
        String filePath = "queries/municipalitiesPerMicro/" + macroCode + "/" + microCode + ".json";
        String requestUrl = "http://www.sidra.ibge.gov.br/api/values/t/1301/p/2010/v/615/N6/in%20N9%20" + microCode;

        File f = new File(filePath);
        if (!(f.exists() && !f.isDirectory())) {
            writeToOutput("Fetching >> " + requestUrl + "\n");
            FileUtils.copyURLToFile(new URL(requestUrl), new File(filePath));
        } else {
            writeToOutput("Fetching nothing >> " + filePath + " already exists.\n");
        }
    }

    // Given an array of Micro regions, fetches all their Municipalities from IBGE'S SIDRA to JSON files.
    private void fetchAllMunicipalitiesFromAllMicrosToFiles(ArrayList<Meso> mesos) throws IOException {
        for (Meso m : mesos) {
            for (Micro mi : m.getMicroRegions()) {
                fetchMunicipalitiesFromMicrosToFile(m.getCode(), mi.getCode());
            }
        }
        writeToOutput("\n<-> End of municipalities queries <->\n\n");
    }

     // Populates the Micro regions Java objects with their municipalities, avaliable in JSON files, fetched from IBGE's SIDRA.
     private void populateMicrosWithMunicipalitiesFromFiles(ArrayList<Meso> mesos) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        StringBuilder mapString = new StringBuilder();

        for (Meso m : mesos) {
            for(Micro mi : m.getMicroRegions()){
                mi.setMunicipalities((ArrayList<Municipality>) mapper.readValue(new File( "queries/municipalitiesPerMicro/" + m.getCode() + "/" + mi.getCode() + ".json"),
                                                                                                               mapper.getTypeFactory().constructCollectionType(ArrayList.class, Municipality.class)));
                // First is columns's titles row
                mi.getMunicipalities().remove(0);
                for (Municipality mu : mi.getMunicipalities()) {
                    mu.setMicro(mi.getCode());
                    mapString.append(mi.getName() + "\t(" + mi.getCode() + ") \t contains \t" + mu.getName() + "\t(" + mu.getCode() + ") as a municipality.\n");
                }
            }
        }
        writeToOutput(mapString.toString() + "\n<-> End of linking micro regions with their municipalities. <->\n\n");
    }
    
    // Populates the Meso regions Java objects with their Micro regions, avaliable as JSON files, fetched from IBGE's SIDRA.
    private void populateMesosWithMicrosFromFiles(ArrayList<Meso> mesos) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        StringBuilder mapString = new StringBuilder();

        for (Meso m : mesos) {
            m.setMicroRegions((ArrayList<Micro>) mapper.readValue(new File("queries/microPerMeso/" + m.getCode() + ".json"),
                                                                                                mapper.getTypeFactory().constructCollectionType(ArrayList.class, Micro.class)));
            // First is columns's titles row
            m.getMicroRegions().remove(0);
            for (Micro mi : m.getMicroRegions()) {
                mi.setMeso(m.getCode());
                mapString.append(m.getName() + "\t(" + m.getCode() + ") \t contains \t" + mi.getName() + "\t(" + mi.getCode() + ") as a micro region.\n");
            }
        }
        writeToOutput(mapString + "\n<-> End of linking macro regions with their micro regions. <->\n\n");
    }

    // MAIN LOGIC FLOW
    // Fetches data to files, then populates the actual objects with data from the files.
    // @TODO: get those Java objects persisted to a MySQL database.
    private void beginQuery() {
        ArrayList<Meso> mesos;

        int i = 0;
        try {
            if (xlsFile != null && !xlsFile.isEmpty()) {
                mesos = readMesoSheet(WorkbookFactory.create(new File(xlsFile)));
                
                fetchAllMicrosFromAllMesosToFiles(mesos);
                populateMesosWithMicrosFromFiles(mesos);
                
                fetchAllMunicipalitiesFromAllMicrosToFiles(mesos);
                populateMicrosWithMunicipalitiesFromFiles(mesos);
                
                writeToOutput("<-> End of computation. <->");
            } else {
                writeToOutput("No spreadsheet as source specified.");
            }
        } catch (Exception ex) {
            StringWriter errors = new StringWriter();

            writeToOutput("No can do >>\n" + ex.getMessage() + "\n\n");
            ex.printStackTrace(new PrintWriter(errors));
            writeToOutput(errors.toString());
        }
    }

    @FXML
    private void beginQueryButton(ActionEvent event) {
        // Does the job in another thread so the interface is not halted until completation.
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                beginQuery();
                return null;
            }
        };

        Thread th = new Thread(task);
        th.start();
    }

    @FXML
    private void loadMesoSheet(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Find xls file");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Excel sheet", "*.xls"));
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            xlsFile = selectedFile.getAbsolutePath();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        xlsFile = "";
        writeToOutput("Files will be saved to ./queries/\n\n");
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    // Permits the worker thread to write output to the interface thread.
    public void writeToOutput(String text){
        Platform.runLater(() -> { output.appendText(text); });
    }
}
