package fr.ups.m2ihm.drawingtool.model;

import fr.ups.m2ihm.drawingtool.macrocommand.MacroManager;
import fr.ups.m2ihm.drawingtool.model.core.DrawingToolCore;
import fr.ups.m2ihm.drawingtool.undomanager.UndoManager;
import java.beans.PropertyChangeListener;

public interface DrawingStateMachine {
    public static final String SHAPES_PROPERTY = "shapesChanged";
    public static final String GHOST_PROPERTY = "ghostChanged";

    void init(DrawingToolCore core);
    void handleEvent(DrawingEvent event, DrawingToolCore core);
    void setUndoManager(UndoManager undoManager);
    void addPropertyListener(PropertyChangeListener listener);
    void addPropertyListener(String propertyName, PropertyChangeListener listener);
    void removePropertyListener(PropertyChangeListener listener);
    void removePropertyListener(String propertyName, PropertyChangeListener listener);
    void setMacroManager(MacroManager macroManager);
}
