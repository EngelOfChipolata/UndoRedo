package fr.ups.m2ihm.drawingtool.model;

public class PaletteEvent {
     private final PaletteEventType eventType;

    public PaletteEvent(PaletteEventType eventType) {
        this.eventType = eventType;
    }


    public PaletteEventType getEventType() {
        return eventType;
    }

   
}
