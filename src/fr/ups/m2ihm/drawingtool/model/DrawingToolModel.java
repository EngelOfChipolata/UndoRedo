package fr.ups.m2ihm.drawingtool.model;


import java.beans.PropertyChangeListener;

public interface DrawingToolModel {
    void init();
    void handleEvent(DrawingEvent event);
    void handleEvent(PaletteEvent event);
    void undo();
    void redo();
    void goToCommand(int i);
    void selectedMacroChanged(String name);
    void macroRecordingToogled(String recordedMacroName);
    void addPropertyListener(PropertyChangeListener listener);
    void addPropertyListener(String propertyName, PropertyChangeListener listener);
    void removePropertyListener(PropertyChangeListener listener);
    void removePropertyListener(String propertyName, PropertyChangeListener listener);
}
