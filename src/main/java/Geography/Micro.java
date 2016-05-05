/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Geography;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;

/**
 *
 * @author eugenio
 */
public class Micro {
    private String meso;
    private String code;
    private String name;
    private ArrayList<Municipality> municipalities;

    public Micro(){
        municipalities = new ArrayList<>();
    }

    public String getMeso() {
        return meso;
    }

    public void setMeso(String meso) {
        this.meso = meso;
    }

    @JsonProperty("D3C")
    public String getCode() {
        return code;
    }

    @JsonProperty("D3C")
    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty("D3N")
    public String getName() {
        return name;
    }

    @JsonProperty("D3N")
    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Municipality> getMunicipalities() {
        return municipalities;
    }

    public void setMunicipalities(ArrayList<Municipality> municipalities) {
        this.municipalities = municipalities;
    }

}
