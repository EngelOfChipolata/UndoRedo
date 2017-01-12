package fr.ups.m2ihm.drawingtool.model;

public class PaletteEvent {
     private final PaletteEventType eventType;
     private final String selectedMacroName;

    public PaletteEvent(PaletteEventType eventType) {
        this(eventType, null);
    }

    public PaletteEvent(PaletteEventType paletteEventType, String selectedMacro) {
        this.eventType = paletteEventType;
        this.selectedMacroName = selectedMacro;
    }


    public PaletteEventType getEventType() {
        return eventType;
    }
    
    public String getSelectedMacroName(){
        return selectedMacroName;
    }

   
}
