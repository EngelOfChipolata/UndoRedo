package fr.ups.m2ihm.drawingtool.model;

import fr.ups.m2ihm.drawingtool.macrocommand.MacroStateMachine;
import fr.ups.m2ihm.drawingtool.macrocommand.MacroManager;
import static fr.ups.m2ihm.drawingtool.model.PaletteEventType.DRAW_LINE;
import static fr.ups.m2ihm.drawingtool.model.PaletteEventType.DRAW_RECTANGLE;
import static fr.ups.m2ihm.drawingtool.model.PaletteEventType.DRAW_TRIANGLE;
import static fr.ups.m2ihm.drawingtool.model.PaletteEventType.REGIONAL_UNDO;
import static fr.ups.m2ihm.drawingtool.model.PaletteEventType.MACRO_COMMAND;
import static fr.ups.m2ihm.drawingtool.model.PaletteEventType.values;
import fr.ups.m2ihm.drawingtool.model.core.DefaultDrawingToolCore;
import fr.ups.m2ihm.drawingtool.model.core.DrawingToolCore;
import fr.ups.m2ihm.drawingtool.undomanager.UndoManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.EnumMap;
import java.util.Map;

public class DefaultDrawingToolModel implements DrawingToolModel {

    private final PropertyChangeListener bouncingPropertyChangeListener;
    private DrawingStateMachine currentStateMachine;
    private final DrawingStateMachine DEFAULT_LINE_STATE_MACHINE = new LineStateMachine();
    private final DrawingStateMachine DEFAULT_RECTANGLE_STATE_MACHINE = new RectangleStateMachine();
    private final DrawingStateMachine DEFAULT_TRIANGLE_STATE_MACHINE = new TriangleStateMachine();
    private final DrawingStateMachine DEFAULT_REGIONAL_UNDO_STATE_MACHINE = new RegionalUndoStateMachine();
    private final DrawingStateMachine DEFAULT_MACRO_STATE_MACHINE = new MacroStateMachine();
    private final DrawingToolCore core;
    private final PropertyChangeSupport support;
    private final UndoManager undoManager;
    private final MacroManager macroManager;

    @Override
    public void undo() {
        undoManager.undo();
        macroManager.undo();
    }

    @Override
    public void redo() {
        undoManager.redo();
    }
    
    @Override
    public void goToCommand(int i){
        System.out.println(undoManager.getRelativeCommandDistance(i));
        int n = undoManager.getRelativeCommandDistance(i);
        if (n < 0){ //An undo must be done
            undoManager.undo(-n);
            macroManager.undo(-n);
        }else if (n > 0){//A redo must be done
            undoManager.redo(n);
        }
    }

    @Override
    public void selectedMacroChanged(String name) {
        macroManager.setSelectedMacro(name);
    }

    @Override
    public void macroRecordingToogled(String recordedMacroName) {
        macroManager.recordToogle(recordedMacroName);
    }
    
    
    

    private enum PossibleState {

        DRAWING_LINE(false, true, true, true, true),
        DRAWING_RECTANGLE(true, false, true, true, true),
        DRAWING_TRIANGLE(true, true, false, true, true),
        REGIONAL_UNDOING(true, true, true, false, true),
        MACRO_COMMANDING(true, true, true, true, false);
        public final boolean lineEnabled;
        public final boolean rectangleEnabled;
        public final boolean triangleEnabled;
        public final boolean regionalUndoEnabled;
        public final boolean macroCommandEnabled;

        private PossibleState(boolean lineEnabled, boolean rectangleEnabled, boolean triangleEnabled,
                boolean regionalUndoEnabled, boolean macroCommandEnabled) {
            this.lineEnabled = lineEnabled;
            this.rectangleEnabled = rectangleEnabled;
            this.triangleEnabled = triangleEnabled;
            this.regionalUndoEnabled = regionalUndoEnabled;
            this.macroCommandEnabled = macroCommandEnabled;
        }

    }
    private PossibleState currentState;
    private final Map<PaletteEventType, Boolean> eventAvailability;
    private final Map<PossibleState, DrawingStateMachine> availableDrawingStateMachines;

    public DefaultDrawingToolModel() {
        core = new DefaultDrawingToolCore();
        undoManager = new UndoManager();
        macroManager = new MacroManager();
        support = new PropertyChangeSupport(this);
        eventAvailability = new EnumMap<>(PaletteEventType.class);
        for (PaletteEventType eventType : values()) {
            eventAvailability.put(eventType, null);
        }
        availableDrawingStateMachines = new EnumMap<>(PossibleState.class);
        availableDrawingStateMachines.put(PossibleState.DRAWING_LINE, DEFAULT_LINE_STATE_MACHINE);
        availableDrawingStateMachines.put(PossibleState.DRAWING_RECTANGLE, DEFAULT_RECTANGLE_STATE_MACHINE);
        availableDrawingStateMachines.put(PossibleState.DRAWING_TRIANGLE, DEFAULT_TRIANGLE_STATE_MACHINE);
        availableDrawingStateMachines.put(PossibleState.REGIONAL_UNDOING, DEFAULT_REGIONAL_UNDO_STATE_MACHINE);
        availableDrawingStateMachines.put(PossibleState.MACRO_COMMANDING, DEFAULT_MACRO_STATE_MACHINE);
        bouncingPropertyChangeListener = (PropertyChangeEvent evt) -> {
            firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
        };
        DEFAULT_LINE_STATE_MACHINE.addPropertyListener(bouncingPropertyChangeListener);
        DEFAULT_RECTANGLE_STATE_MACHINE.addPropertyListener(bouncingPropertyChangeListener);
        DEFAULT_TRIANGLE_STATE_MACHINE.addPropertyListener(bouncingPropertyChangeListener);
        DEFAULT_REGIONAL_UNDO_STATE_MACHINE.addPropertyListener(bouncingPropertyChangeListener);
        DEFAULT_MACRO_STATE_MACHINE.addPropertyListener(bouncingPropertyChangeListener);
        DEFAULT_LINE_STATE_MACHINE.setUndoManager(undoManager);
        DEFAULT_RECTANGLE_STATE_MACHINE.setUndoManager(undoManager);
        DEFAULT_TRIANGLE_STATE_MACHINE.setUndoManager(undoManager);
        DEFAULT_REGIONAL_UNDO_STATE_MACHINE.setUndoManager(undoManager);
        DEFAULT_MACRO_STATE_MACHINE.setUndoManager(undoManager);
        DEFAULT_LINE_STATE_MACHINE.setMacroManager(macroManager);
        DEFAULT_RECTANGLE_STATE_MACHINE.setMacroManager(macroManager);
        DEFAULT_TRIANGLE_STATE_MACHINE.setMacroManager(macroManager);
        DEFAULT_REGIONAL_UNDO_STATE_MACHINE.setMacroManager(macroManager);
        DEFAULT_MACRO_STATE_MACHINE.setMacroManager(macroManager);

        undoManager.addPropertyChangeListener(UndoManager.UNDO_COMMANDS_PROPERTY, (e) -> {
            firePropertyChange(DrawingStateMachine.SHAPES_PROPERTY, null, core.getShapes());
        });

        undoManager.addPropertyChangeListener(UndoManager.UNDO_COMMANDS_PROPERTY, (e) -> {
            firePropertyChange(UndoManager.UNDO_COMMANDS_PROPERTY, e.getOldValue(), e.getNewValue());
        });

        undoManager.addPropertyChangeListener(UndoManager.REDO_COMMANDS_PROPERTY, (e) -> {
            firePropertyChange(UndoManager.REDO_COMMANDS_PROPERTY, e.getOldValue(), e.getNewValue());
        });
        
        macroManager.addPropertyChangeListener(MacroManager.MACRO_LIST_CHANGED_PROPERTY, (e) -> {
            firePropertyChange(MacroManager.MACRO_LIST_CHANGED_PROPERTY, e.getOldValue(), e.getNewValue());
        });
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        support.firePropertyChange(propertyName, oldValue, newValue);
    }

    @Override
    public void addPropertyListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    @Override
    public void addPropertyListener(String propertyName, PropertyChangeListener listener) {
        support.addPropertyChangeListener(propertyName, listener);
    }

    @Override
    public void removePropertyListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    @Override
    public void removePropertyListener(String propertyName, PropertyChangeListener listener) {
        support.removePropertyChangeListener(propertyName, listener);
    }

    @Override
    public void init() {
        gotoState(PossibleState.DRAWING_LINE);
        undoManager.init();
        macroManager.init();
    }

    @Override
    public void handleEvent(DrawingEvent event) {
        currentStateMachine.handleEvent(event, core);
    }

    private void gotoState(PossibleState possibleState) {
        currentState = possibleState;
        currentStateMachine = availableDrawingStateMachines.get(currentState);
        currentStateMachine.init(core);
        enableEvents(currentState.lineEnabled, currentState.rectangleEnabled, currentState.triangleEnabled, currentState.regionalUndoEnabled, currentState.macroCommandEnabled);
    }

    private void enableEvents(
            boolean drawingLineEnabled,
            boolean drawingRectangleEnabled,
            boolean drawingTriangleEnabled,
            boolean regionalUndoEnabled,
            boolean macroCommandEnabled) {
        fireEventAvailabilityChanged(DRAW_LINE, drawingLineEnabled);
        fireEventAvailabilityChanged(DRAW_RECTANGLE, drawingRectangleEnabled);
        fireEventAvailabilityChanged(DRAW_TRIANGLE, drawingTriangleEnabled);
        fireEventAvailabilityChanged(REGIONAL_UNDO, regionalUndoEnabled);
        fireEventAvailabilityChanged(MACRO_COMMAND, macroCommandEnabled);
    }

    private void fireEventAvailabilityChanged(PaletteEventType paletteEventType, boolean newAvailability) {
        Boolean oldAvailability = eventAvailability.get(paletteEventType);
        eventAvailability.put(paletteEventType, newAvailability);
        firePropertyChange(paletteEventType.getPropertyName(), oldAvailability, newAvailability);
    }

    @Override
    public void handleEvent(PaletteEvent event) {
        switch (event.getEventType()) {
            case DRAW_LINE:
                drawLine();
                break;
            case DRAW_RECTANGLE:
                drawRectangle();
                break;
            case DRAW_TRIANGLE:
                drawTriangle();
                break;
            case REGIONAL_UNDO:
                regionalUndo();
                break;
            case MACRO_COMMAND:
                macroCommand(event.getSelectedMacroName());
        }
    }

    public void drawLine() {
        switch (currentState) {
            case DRAWING_LINE:
                break;
            case DRAWING_RECTANGLE:
                gotoState(PossibleState.DRAWING_LINE);
                break;
            case DRAWING_TRIANGLE:
                gotoState(PossibleState.DRAWING_LINE);
                break;
            case REGIONAL_UNDOING:
                gotoState(PossibleState.DRAWING_LINE);
                break;
            case MACRO_COMMANDING:
                gotoState(PossibleState.DRAWING_LINE);
                break;
        }
    }

    public void drawRectangle() {
        switch (currentState) {
            case DRAWING_LINE:
                gotoState(PossibleState.DRAWING_RECTANGLE);
                break;
            case DRAWING_RECTANGLE:
                break;
            case DRAWING_TRIANGLE:
                gotoState(PossibleState.DRAWING_RECTANGLE);
                break;
            case REGIONAL_UNDOING:
                gotoState(PossibleState.DRAWING_RECTANGLE);
                break;
            case MACRO_COMMANDING:
                gotoState(PossibleState.DRAWING_RECTANGLE);
                break;
        }
    }
    
    public void drawTriangle(){
        switch (currentState){
            case DRAWING_LINE:
                gotoState(PossibleState.DRAWING_TRIANGLE);
                break;
            case DRAWING_RECTANGLE:
                gotoState(PossibleState.DRAWING_TRIANGLE);
                break;
            case DRAWING_TRIANGLE:
                break;
            case REGIONAL_UNDOING:
                gotoState(PossibleState.DRAWING_TRIANGLE);
                break;            
            case MACRO_COMMANDING:
                gotoState(PossibleState.DRAWING_TRIANGLE);
                break;
        }
    }
    
    public void regionalUndo(){
        switch (currentState){
            case DRAWING_LINE:
                gotoState(PossibleState.REGIONAL_UNDOING);
                break;
            case DRAWING_RECTANGLE:
                gotoState(PossibleState.REGIONAL_UNDOING);
                break;
            case DRAWING_TRIANGLE:
                gotoState(PossibleState.REGIONAL_UNDOING);
                break;
            case REGIONAL_UNDOING:
                break;
            case MACRO_COMMANDING:
                gotoState(PossibleState.REGIONAL_UNDOING);
                break;
        }
    }
    
    public void macroCommand(String macroName){
        switch (currentState){
            case DRAWING_LINE:
                gotoState(PossibleState.MACRO_COMMANDING);
                break;
            case DRAWING_RECTANGLE:
                gotoState(PossibleState.MACRO_COMMANDING);
                break;
            case DRAWING_TRIANGLE:
                gotoState(PossibleState.MACRO_COMMANDING);
                break;
            case REGIONAL_UNDOING:
                gotoState(PossibleState.MACRO_COMMANDING);
                break;
            case MACRO_COMMANDING:
                break;            
        }
    }
    
}
