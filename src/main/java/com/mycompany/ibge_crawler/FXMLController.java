package com.mycompany.ibge_crawler;

import Geography.Meso;
import Geography.Micro;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class FXMLController implements Initializable {
    
    @FXML
    private Button beginQueryButton;
    @FXML
    private TextArea output;
    private Stage stage;
    private String xlsFile;
    
    private ArrayList<Meso> readMesoSheet(Workbook wb){
        ArrayList<Meso> mesos = new ArrayList<>();
        Sheet sheet = wb.getSheetAt(0);
        int row = 2;
        
        do{
            Meso meso = new Meso();
            meso.setUF(sheet.getRow(row).getCell(0).getStringCellValue());
            meso.setCode(Integer.toString((int)sheet.getRow(row).getCell(1).getNumericCellValue()));
            meso.setName(sheet.getRow(row).getCell(2).getStringCellValue());
            mesos.add(meso);
            output.appendText(meso.getUF()+"\t"+meso.getCode()+"\t"+meso.getName()+"\n");
            row++;
        }while(sheet.getRow(row) != null &&
               !sheet.getRow(row).getCell(0).getStringCellValue().isEmpty());
        output.appendText("\n<-> End of spreadsheet <->\n\n");
        return mesos;
    }

    private void fetchMicrosFromMesoToFile(String macroCode) throws IOException{
        String requestUrl = "http://www.sidra.ibge.gov.br/api/values/t/1301/p/2010/v/615/N9/in%20N8%20"+macroCode;
        output.appendText("Fetching >> "+requestUrl+"\n");
        FileUtils.copyURLToFile(new URL(requestUrl), new File("queries/microPerMeso/"+macroCode+".json"));
    }
    
    private void fetchAllMicrosFromAllMesosToFiles(ArrayList<Meso> mesos) throws IOException{
        for(Meso m : mesos){
            fetchMicrosFromMesoToFile(m.getCode());
        }
        output.appendText("\n<-> End of micro regions queries <->\n\n");
    }
        
    private void populateMicrosWithMesosFromFiles(ArrayList<Meso> mesos) throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        for(Meso m : mesos){
            m.setMicroRegions((ArrayList<Micro>)mapper.readValue(new File("queries/microPerMeso/"+m.getCode()+".json"),
                                                                 mapper.getTypeFactory().constructCollectionType(ArrayList.class, Micro.class)));
        }
    }
    
    @FXML
    private void beginQuery(ActionEvent event) {
        ArrayList<Meso> mesos;

        int i = 0;
        try {
            if(xlsFile != null && !xlsFile.isEmpty()){
                mesos = readMesoSheet(WorkbookFactory.create(new File(xlsFile)));
                fetchAllMicrosFromAllMesosToFiles(mesos);
                populateMicrosWithMesosFromFiles(mesos);
            }else{
                output.appendText("No spreadsheet as source specified.");
            }
        } catch (Exception ex) {
            output.appendText("No can do >>\n"+ex.getMessage());
        }
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
        output.appendText("Files will be saved to ./queries/\n\n");
    }    

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
