/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ups.m2ihm.drawingtool.macrocommand;

import fr.ups.m2ihm.drawingtool.macrocommand.MacroManager;
import fr.ups.m2ihm.drawingtool.macrocommand.MacroCommand;
import fr.ups.m2ihm.drawingtool.model.DrawingEvent;
import fr.ups.m2ihm.drawingtool.model.DrawingEventType;
import fr.ups.m2ihm.drawingtool.model.DrawingStateMachine;
import static fr.ups.m2ihm.drawingtool.model.DrawingEventType.BEGIN_DRAW;
import static fr.ups.m2ihm.drawingtool.model.DrawingEventType.CANCEL_DRAW;
import static fr.ups.m2ihm.drawingtool.model.DrawingEventType.DRAW;
import static fr.ups.m2ihm.drawingtool.model.DrawingEventType.END_DRAW;
import static fr.ups.m2ihm.drawingtool.model.DrawingEventType.NO_DRAW;
import static fr.ups.m2ihm.drawingtool.model.DrawingEventType.values;
import fr.ups.m2ihm.drawingtool.model.core.DrawingToolCore;
import fr.ups.m2ihm.drawingtool.model.core.Shape;
import fr.ups.m2ihm.drawingtool.undomanager.Command;
import fr.ups.m2ihm.drawingtool.undomanager.UndoManager;
import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author guilhem
 */
public class MacroStateMachine implements DrawingStateMachine{
    
    private final PropertyChangeSupport support;
    private final Map<DrawingEventType, Boolean> eventAvailability;
    private UndoManager undoManager;
    private MacroManager macroManager;
    private PossibleState currentState;
    
    private enum PossibleState {
        IDLE(true, false, false, false, false), BEGIN(false, true, true, true, false);
        public final boolean beginDrawEnabled;
        public final boolean endDrawEnabled;
        public final boolean drawEnabled;
        public final boolean cancelDrawEnabled;
        public final boolean noDrawEnabled;

        private PossibleState(boolean beginDrawEnabled, boolean endDrawEnabled, boolean drawEnabled, boolean cancelDrawEnabled, boolean noDrawEnabled) {
            this.beginDrawEnabled = beginDrawEnabled;
            this.endDrawEnabled = endDrawEnabled;
            this.drawEnabled = drawEnabled;
            this.cancelDrawEnabled = cancelDrawEnabled;
            this.noDrawEnabled = noDrawEnabled;
        }
    }
    
    public MacroStateMachine() {
        support = new PropertyChangeSupport(this);
        eventAvailability = new EnumMap<>(DrawingEventType.class);
        for (DrawingEventType eventType : values()) {
            eventAvailability.put(eventType, null);
        }
    }
    
    public MacroManager getMacroManager(){
        return macroManager;
    }
    
    public UndoManager getUndoManager() {
        return undoManager;
    }

    @Override
    public void init(DrawingToolCore core) {
        gotoState(PossibleState.IDLE);
        firePropertyChange(GHOST_PROPERTY, null, null);
        firePropertyChange(SHAPES_PROPERTY, null, core.getShapes());    }

    @Override
    public void handleEvent(DrawingEvent event, DrawingToolCore core) {
        switch (event.getEventType()){
            case BEGIN_DRAW:
                beginDraw();
                break;
            case DRAW:
                draw();
                break;
            case CANCEL_DRAW:
                cancelDraw();
                break;
            case END_DRAW:
                endDraw(event.getPoint(), core);
                break;
            case NO_DRAW:
                //Nothing
                break;
            default:
                throw new AssertionError(event.getEventType().name());
            
        }
    }
    
    private void beginDraw(){
        switch (currentState){
            case IDLE:
                //Display a ghost ?
                gotoState(PossibleState.BEGIN);
                break;
            case BEGIN:
                break;
            default:
                throw new AssertionError(currentState.name());
        }
    }
    
    private void draw(){
        switch(currentState){
            case IDLE:
                break;
            case BEGIN:
                //Update ghost ?
                gotoState(PossibleState.BEGIN);
                break;
            default:
                throw new AssertionError(currentState.name());
        }
    }
    
    private void endDraw(Point point, DrawingToolCore core){
        switch(currentState){
            case IDLE:
                break;
            case BEGIN:
                gotoState(PossibleState.IDLE);
                Command com = getMacroManager().getSelectedMacro();
                ((MacroCommand)com).setCreationPoint(point);
                getUndoManager().registerCommand(com);
                getMacroManager().registerCommand(com);
                //Delete ghost ?
                firePropertyChange(SHAPES_PROPERTY, null, core.getShapes());
                break;
            default:
                throw new AssertionError(currentState.name());
            
        }
    }
    
    private void cancelDraw(){
        switch(currentState){
            case IDLE:
                break;
            case BEGIN:
                //Delete ghost
                gotoState(PossibleState.IDLE);
                break;
            default:
                throw new AssertionError(currentState.name());
            
        }
    }

    @Override
    public void setUndoManager(UndoManager undoManager) {
        this.undoManager = undoManager;
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
    public void setMacroManager(MacroManager macroManager) {
        this.macroManager = macroManager;
    }
    
        private void gotoState(PossibleState possibleState) {
        currentState = possibleState;
        enableEvents(currentState.beginDrawEnabled, currentState.endDrawEnabled, currentState.drawEnabled, currentState.cancelDrawEnabled, currentState.noDrawEnabled);
    }

    private void enableEvents(
            boolean beginDrawEnabled,
            boolean endDrawEnabled,
            boolean drawEnabled,
            boolean cancelDrawEnabled,
            boolean noDrawEnabled) {
        fireEventAvailabilityChanged(BEGIN_DRAW, beginDrawEnabled);
        fireEventAvailabilityChanged(CANCEL_DRAW, cancelDrawEnabled);
        fireEventAvailabilityChanged(DRAW, drawEnabled);
        fireEventAvailabilityChanged(END_DRAW, endDrawEnabled);
        fireEventAvailabilityChanged(NO_DRAW, noDrawEnabled);

    }

    private void fireEventAvailabilityChanged(DrawingEventType drawingEventType, boolean newAvailability) {
        Boolean oldAvailability = eventAvailability.get(drawingEventType);
        eventAvailability.put(drawingEventType, newAvailability);
        firePropertyChange(drawingEventType.getPropertyName(), oldAvailability, newAvailability);
    }
    
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        support.firePropertyChange(propertyName, oldValue, newValue);
    }
    
}
