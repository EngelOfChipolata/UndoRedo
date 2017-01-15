/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ups.m2ihm.drawingtool.macrocommand;

/**
 *
 * @author guilhem
 */
public enum MacroEvent {
    RECORD_MACRO("recordMacroProperty"),
    REGISTER_COMMAND("macroRegisterCommandProperty");
    private String propertyName;

    private MacroEvent(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }
}
