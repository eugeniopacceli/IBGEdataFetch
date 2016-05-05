/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Geography;

import java.util.ArrayList;

/**
 *
 * @author eugenio
 */
public class Meso {
    private String UF;
    private String code;
    private String name;
    private ArrayList<Micro> microRegions;

    public Meso(){
        microRegions = new ArrayList<>();
    }
    
    public String getUF() {
        return UF;
    }

    public void setUF(String UF) {
        this.UF = UF;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Micro> getMicroRegions() {
        return microRegions;
    }

    public void setMicroRegions(ArrayList<Micro> microRegions) {
        this.microRegions = microRegions;
    }
}
