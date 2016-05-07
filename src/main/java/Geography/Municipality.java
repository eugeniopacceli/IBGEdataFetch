/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Geography;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author eugenio
 */
public class Municipality {
    private String micro;
    private String code;
    private String name;

    public Municipality(){
    }

    public String getMicro() {
        return micro;
    }

    public void setMicro(String micro) {
        this.micro = micro;
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
}