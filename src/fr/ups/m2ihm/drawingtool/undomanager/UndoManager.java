/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package fr.ups.m2ihm.drawingtool.undomanager;

import fr.ups.m2ihm.drawingtool.ihm.DrawingShape;
import fr.ups.m2ihm.drawingtool.model.core.Rectangle;
import fr.ups.m2ihm.drawingtool.model.CreateShapeCommand;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 *
 * @author David
 */
public class UndoManager {

    public final static String REGISTER_AVAILABLE_PROPERTY = "registerAvailable";
    public final static String UNDO_COMMANDS_PROPERTY = "undo";
    public final static String REDO_COMMANDS_PROPERTY = "redo";
    private final PropertyChangeSupport support;
    private final Map<String, Boolean> eventAvailability;
    private final Stack<Command> undoableCommands;
    private final Stack<Command> redoableCommands;

    public Boolean isUndoEnabled() {
        return PossibleState.UNDO_ONLY.equals(currentState) || PossibleState.UNDO_REDOABLE.equals(currentState);
    }

    public Boolean isRedoEnabled() {
        return PossibleState.REDO_ONLY.equals(currentState) || PossibleState.UNDO_REDOABLE.equals(currentState);
    }

    public int getRelativeCommandDistance(int i) {
        //The total number of command is undoableCommand.size() + redoableCommand.size()
        //The absolute command lastly executed is undoableCommand.size() - 1
        return -(undoableCommands.size() - i);

    }

    private enum PossibleState {

        IDLE, UNDO_ONLY, REDO_ONLY, UNDO_REDOABLE
    }
    private PossibleState currentState;

    private void gotoState(PossibleState state) {
        currentState = state;
        switch (currentState) {
            case IDLE:
                enableEvents(true, false, false);
                break;
            case UNDO_ONLY:
                enableEvents(true, true, false);
                break;
            case REDO_ONLY:
                enableEvents(true, false, true);
                break;
            case UNDO_REDOABLE:
                enableEvents(true, true, true);
                break;
        }
    }

    public void init() {
        gotoState(PossibleState.IDLE);
        firePropertyChange(UNDO_COMMANDS_PROPERTY, null, Collections.unmodifiableList(undoableCommands));
        firePropertyChange(REDO_COMMANDS_PROPERTY, null, Collections.unmodifiableList(redoableCommands));
    }

    public UndoManager() {
        undoableCommands = new Stack<>();
        redoableCommands = new Stack<>();
        support = new PropertyChangeSupport(this);
        eventAvailability = new HashMap<>();
        eventAvailability.put(REGISTER_AVAILABLE_PROPERTY, null);
        eventAvailability.put(UndoEvent.UNDO.getPropertyName(), null);
        eventAvailability.put(UndoEvent.REDO.getPropertyName(), null);
        eventAvailability.put(UNDO_COMMANDS_PROPERTY, null);
        eventAvailability.put(REDO_COMMANDS_PROPERTY, null);
    }

    public void registerCommand(Command command) {
        switch (currentState) {
            case IDLE:
                gotoState(PossibleState.UNDO_ONLY);
                command.execute();
                undoableCommands.push(command);
                redoableCommands.clear();
                firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                break;
            case UNDO_ONLY:
                gotoState(PossibleState.UNDO_ONLY);
                command.execute();
                undoableCommands.push(command);
                redoableCommands.clear();
                firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                break;
            case REDO_ONLY:
                gotoState(PossibleState.UNDO_ONLY);
                command.execute();
                undoableCommands.push(command);
                redoableCommands.clear();
                firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                break;
            case UNDO_REDOABLE:
                gotoState(PossibleState.UNDO_ONLY);
                command.execute();
                undoableCommands.push(command);
                redoableCommands.clear();
                firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                break;
        }
    }

    private Command findLastShapeCommandIn(Rectangle rect) {
        Command lastShapeCommandInRectangle = null;
        for (Command c : undoableCommands) {
            if (c instanceof CreateShapeCommand && ((CreateShapeCommand) c).isInside(rect)) {
                lastShapeCommandInRectangle = c;
            }
        }
        return lastShapeCommandInRectangle;
    }

    public void regionalUndo(Rectangle rectangle) {
        Command undoneCommand;
        switch (currentState) {
            case IDLE:
                break;
            case UNDO_ONLY:
                undoneCommand = findLastShapeCommandIn(rectangle);
                if (undoableCommands.size() == 1 && undoneCommand != null) {
                    gotoState(PossibleState.REDO_ONLY);
                    undoableCommands.remove(undoneCommand);
                    undoneCommand.undo();
                    redoableCommands.push(undoneCommand);
                    firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                    firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                } else if (undoableCommands.size() > 1 && undoneCommand != null) {
                    gotoState(PossibleState.UNDO_REDOABLE);
                    undoableCommands.remove(undoneCommand);
                    undoneCommand.undo();
                    redoableCommands.push(undoneCommand);
                    firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                    firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                } else {
                    gotoState(PossibleState.UNDO_ONLY);
                }
                break;
            case REDO_ONLY:
                break;
            case UNDO_REDOABLE:
                undoneCommand = findLastShapeCommandIn(rectangle);
                if (undoableCommands.size() == 1 && undoneCommand != null) {
                    gotoState(PossibleState.REDO_ONLY);
                    undoableCommands.remove(undoneCommand);
                    undoneCommand.undo();
                    redoableCommands.push(undoneCommand);
                    firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                    firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                } else if (undoableCommands.size() > 1 && undoneCommand != null) {
                    gotoState(PossibleState.UNDO_REDOABLE);
                    undoableCommands.remove(undoneCommand);
                    undoneCommand.undo();
                    redoableCommands.push(undoneCommand);
                    firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                    firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                } else {
                    gotoState(PossibleState.UNDO_REDOABLE);
                }
                break;
        }
    }

    public void undo() {
        Command undoneCommand;
        switch (currentState) {
            case IDLE:
                break;
            case UNDO_ONLY:
                if (undoableCommands.size() == 1) {
                    gotoState(PossibleState.REDO_ONLY);
                    undoneCommand = undoableCommands.pop();
                    undoneCommand.undo();
                    redoableCommands.push(undoneCommand);
                    firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                    firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                } else if (undoableCommands.size() > 1) {
                    gotoState(PossibleState.UNDO_REDOABLE);
                    undoneCommand = undoableCommands.pop();
                    undoneCommand.undo();
                    redoableCommands.push(undoneCommand);
                    firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                    firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                }
                break;
            case REDO_ONLY:
                break;
            case UNDO_REDOABLE:
                if (undoableCommands.size() == 1) {
                    gotoState(PossibleState.REDO_ONLY);
                    undoneCommand = undoableCommands.pop();
                    undoneCommand.undo();
                    redoableCommands.push(undoneCommand);
                    firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                    firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                } else if (undoableCommands.size() > 1) {
                    gotoState(PossibleState.UNDO_REDOABLE);
                    undoneCommand = undoableCommands.pop();
                    undoneCommand.undo();
                    redoableCommands.push(undoneCommand);
                    firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                    firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                }
                break;
        }
    }

    public void undo(int n) {
        Command undoneCommand;
        switch (currentState) {
            case IDLE:
                break;
            case UNDO_ONLY:
                if (undoableCommands.size() == n) {
                    gotoState(PossibleState.REDO_ONLY);
                    for (int i = 0; i < n; i++) {
                        undoneCommand = undoableCommands.pop();
                        undoneCommand.undo();
                        redoableCommands.push(undoneCommand);
                    }
                    firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                    firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                } else if (undoableCommands.size() > 1) {
                    gotoState(PossibleState.UNDO_REDOABLE);
                    for (int i = 0; i < n; i++) {
                        undoneCommand = undoableCommands.pop();
                        undoneCommand.undo();
                        redoableCommands.push(undoneCommand);
                    }
                    firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                    firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                } else {
                    throw new IndexOutOfBoundsException("Too many commands to Undo : " + n);
                }
                break;
            case REDO_ONLY:
                break;
            case UNDO_REDOABLE:
                if (undoableCommands.size() == n) {
                    gotoState(PossibleState.REDO_ONLY);
                    for (int i = 0; i < n; i++) {
                        undoneCommand = undoableCommands.pop();
                        undoneCommand.undo();
                        redoableCommands.push(undoneCommand);
                    }
                    firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                    firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                } else if (undoableCommands.size() > n) {
                    gotoState(PossibleState.UNDO_REDOABLE);
                    for (int i = 0; i < n; i++) {
                        undoneCommand = undoableCommands.pop();
                        undoneCommand.undo();
                        redoableCommands.push(undoneCommand);
                    }
                    firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                    firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                } else {
                    throw new IndexOutOfBoundsException("Too many commands to Undo : " + n);
                }
                break;
        }
    }

    public void redo() {
        Command redoneCommand;
        switch (currentState) {
            case IDLE:
                break;
            case UNDO_ONLY:
                break;
            case REDO_ONLY:
                if (redoableCommands.size() == 1) {
                    gotoState(PossibleState.UNDO_ONLY);
                    redoneCommand = redoableCommands.pop();
                    redoneCommand.execute();
                    undoableCommands.push(redoneCommand);
                    firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                    firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                } else if (redoableCommands.size() > 1) {
                    gotoState(PossibleState.UNDO_REDOABLE);
                    redoneCommand = redoableCommands.pop();
                    redoneCommand.execute();
                    undoableCommands.push(redoneCommand);
                    firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                    firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                }
                break;
            case UNDO_REDOABLE:
                if (redoableCommands.size() == 1) {
                    gotoState(PossibleState.UNDO_ONLY);
                    redoneCommand = redoableCommands.pop();
                    redoneCommand.execute();
                    undoableCommands.push(redoneCommand);
                    firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                    firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                } else if (redoableCommands.size() > 1) {
                    gotoState(PossibleState.UNDO_REDOABLE);
                    redoneCommand = redoableCommands.pop();
                    redoneCommand.execute();
                    undoableCommands.push(redoneCommand);
                    firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                    firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                }
                break;
        }
    }

    public void redo(int n) {
        Command redoneCommand;
        switch (currentState) {
            case IDLE:
                break;
            case UNDO_ONLY:
                break;
            case REDO_ONLY:
                if (redoableCommands.size() == n) {
                    gotoState(PossibleState.UNDO_ONLY);
                    for (int i = 0; i < n; i++) {
                        redoneCommand = redoableCommands.pop();
                        redoneCommand.execute();
                        undoableCommands.push(redoneCommand);
                    }
                    firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                    firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                } else if (redoableCommands.size() > n) {
                    gotoState(PossibleState.UNDO_REDOABLE);
                    for (int i = 0; i < n; i++) {
                        redoneCommand = redoableCommands.pop();
                        redoneCommand.execute();
                        undoableCommands.push(redoneCommand);
                    }
                    firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                    firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                } else {
                    throw new IndexOutOfBoundsException("Too many commands to Redo : " + n);
                }
                break;
            case UNDO_REDOABLE:
                if (redoableCommands.size() == n) {
                    gotoState(PossibleState.UNDO_ONLY);
                    for (int i = 0; i < n; i++) {
                        redoneCommand = redoableCommands.pop();
                        redoneCommand.execute();
                        undoableCommands.push(redoneCommand);
                    }
                    firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                    firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                } else if (redoableCommands.size() > n) {
                    gotoState(PossibleState.UNDO_REDOABLE);
                    for (int i = 0; i < n; i++) {
                        redoneCommand = redoableCommands.pop();
                        redoneCommand.execute();
                        undoableCommands.push(redoneCommand);
                    }
                    firePropertyChange(UNDO_COMMANDS_PROPERTY, null, undoableCommands);
                    firePropertyChange(REDO_COMMANDS_PROPERTY, null, redoableCommands);
                } else {
                    throw new IndexOutOfBoundsException("Too many commands to Redo : " + n);
                }
                break;
        }

    }

    private void enableEvents(
            boolean registerEnabled,
            boolean undoEnabled,
            boolean redoEnabled) {
        fireEventAvailabilityChanged(REGISTER_AVAILABLE_PROPERTY, registerEnabled);
        fireEventAvailabilityChanged(UndoEvent.UNDO.getPropertyName(), undoEnabled);
        fireEventAvailabilityChanged(UndoEvent.REDO.getPropertyName(), redoEnabled);

    }

    private void fireEventAvailabilityChanged(String propertyName, boolean newAvailability) {
        Boolean oldAvailability = eventAvailability.get(propertyName);
        eventAvailability.put(propertyName, newAvailability);
        firePropertyChange(propertyName, oldAvailability, newAvailability);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        support.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        support.removePropertyChangeListener(propertyName, listener);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        support.firePropertyChange(propertyName, oldValue, newValue);
    }
}
