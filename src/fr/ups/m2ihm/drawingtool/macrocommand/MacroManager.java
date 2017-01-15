/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ups.m2ihm.drawingtool.macrocommand;

import fr.ups.m2ihm.drawingtool.macrocommand.MacroCommand;
import fr.ups.m2ihm.drawingtool.undomanager.Command;
import fr.ups.m2ihm.drawingtool.undomanager.UndoManager;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author guilhem
 */
public class MacroManager{
    
    public final static String MACRO_LIST_CHANGED_PROPERTY = "macroListChanged";
    private final PropertyChangeSupport support;
    
    private List<MacroCommand> macroCommands;
    private MacroCommand recordingMacro;
    private MacroCommand selectedMacro;
    private final Map<String, Boolean> eventAvailability;

    
    public MacroManager(){
        support = new PropertyChangeSupport(this);
        macroCommands = new ArrayList<>();
        eventAvailability = new HashMap<>();
        eventAvailability.put(MacroEvent.REGISTER_COMMAND.getPropertyName(), null);
        eventAvailability.put(MacroEvent.RECORD_MACRO.getPropertyName(), null);
        eventAvailability.put(MACRO_LIST_CHANGED_PROPERTY, null);
    }
    
    private enum PossibleState {

        IDLE, RECORDING
    }
    private PossibleState currentState;

    private void gotoState(PossibleState state) {
        currentState = state;
        switch (currentState) {
            case IDLE:
                enableEvents(true, false);
                break;
            case RECORDING:
                enableEvents(true, true);
                break;
            default:
                throw new AssertionError(currentState.name());
        }
    }
    
    public void recordToogle(String macroNameField){
        switch (currentState){
            case IDLE:
                gotoState(PossibleState.RECORDING);
                recordingMacro = new MacroCommand();
                break;
            case RECORDING:
                gotoState(PossibleState.IDLE);
                if (recordingMacro.commandSize() > 0){
                    recordingMacro.setName(macroNameField);
                    recordingMacro.normalize();
                    macroCommands.add(recordingMacro);
                    firePropertyChange(MACRO_LIST_CHANGED_PROPERTY, null, macroCommands);
                }
                break;
            default:
                throw new AssertionError(currentState.name());
            
        }
    }
    
    public void registerCommand(Command c){
        switch(currentState){
            case IDLE:
                break;
            case RECORDING:
                recordingMacro.add(c);
                break;
            default:
                throw new AssertionError(currentState.name());  
        }
    }
    
    public void undo(){
        switch (currentState){
            case IDLE:
                break;
            case RECORDING:
                if (recordingMacro.commandSize() >= 1){
                    recordingMacro.removeLast();
                }
        }
    }
    
    public void undo(int n){
        switch (currentState){
            case IDLE:
                break;
            case RECORDING:
                for (int i = 0; i <= Math.max(n, recordingMacro.commandSize() - 1); i++){
                    recordingMacro.removeLast();
                }
        }
    }

    public MacroCommand getSelectedMacro() {
        return new MacroCommand(selectedMacro);
    }

    public void setSelectedMacro(MacroCommand selectedMacro) {
        this.selectedMacro = selectedMacro;
    }
    
    public void setSelectedMacro(String name){
        for(MacroCommand m: macroCommands){
            if (m.getName().equals(name)){
                setSelectedMacro(m);
            }
        }
    }
    
    public void addPropertyChangeListener(PropertyChangeListener li){
        support.addPropertyChangeListener(li);
    }
    public void addPropertyChangeListener(String name, PropertyChangeListener li){
        support.addPropertyChangeListener(name, li);
    }
    
    public void removePropertyChangeListener(String name, PropertyChangeListener li){
        support.removePropertyChangeListener(name, li);
    }
    public void removePropertyChangeListener(PropertyChangeListener li){
        support.removePropertyChangeListener(li);
    }
    
    public void init(){
        gotoState(PossibleState.IDLE);
        firePropertyChange(MACRO_LIST_CHANGED_PROPERTY, null, Collections.unmodifiableList(macroCommands));
    }
    
    public void firePropertyChange(String name, Object oldValue, Object newValue){
        support.firePropertyChange(name, oldValue, newValue);
    }
    
        private void enableEvents(
            boolean recordEnabled,
            boolean registerEnabled) {
        fireEventAvailabilityChanged(MacroEvent.RECORD_MACRO.getPropertyName(), recordEnabled);
        fireEventAvailabilityChanged(MacroEvent.REGISTER_COMMAND.getPropertyName(), registerEnabled);

    }

    private void fireEventAvailabilityChanged(String propertyName, boolean newAvailability) {
        Boolean oldAvailability = eventAvailability.get(propertyName);
        eventAvailability.put(propertyName, newAvailability);
        firePropertyChange(propertyName, oldAvailability, newAvailability);
    }
    
    
}
