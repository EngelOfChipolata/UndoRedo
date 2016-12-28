package fr.ups.m2ihm.drawingtool.model;

import static fr.ups.m2ihm.drawingtool.model.DrawingEventType.BEGIN_DRAW;
import static fr.ups.m2ihm.drawingtool.model.DrawingEventType.CANCEL_DRAW;
import static fr.ups.m2ihm.drawingtool.model.DrawingEventType.DRAW;
import static fr.ups.m2ihm.drawingtool.model.DrawingEventType.END_DRAW;
import static fr.ups.m2ihm.drawingtool.model.DrawingEventType.values;
import fr.ups.m2ihm.drawingtool.model.core.DrawingToolCore;
import fr.ups.m2ihm.drawingtool.model.core.Rectangle;
import fr.ups.m2ihm.drawingtool.undomanager.Command;
import fr.ups.m2ihm.drawingtool.undomanager.UndoManager;
import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.EnumMap;
import java.util.Map;

public class RectangleStateMachine implements DrawingStateMachine {

    private final PropertyChangeSupport support;
    private Rectangle ghost;
    private Point p0;
    private final Map<DrawingEventType, Boolean> eventAvailability;
    private UndoManager undoManager;

    public UndoManager getUndoManager() {
        return undoManager;
    }

    public void setUndoManager(UndoManager undoManager) {
        this.undoManager = undoManager;
    }
    

    private void beginDraw(Point point, DrawingToolCore core) {
        switch (currentState) {
            case IDLE:
                gotoState(PossibleState.BEGIN);
                p0 = point;
                break;
            case BEGIN:
                break;
            case RECTANGLE:
                break;
        }
    }

    private void cancelDraw(DrawingToolCore core) {
        switch (currentState) {
            case IDLE:
                break;
            case BEGIN:
                gotoState(PossibleState.IDLE);
                break;
            case RECTANGLE:
                Rectangle oldGhost = ghost;
                ghost = null;
                gotoState(PossibleState.IDLE);
                firePropertyChange(GHOST_PROPERTY, oldGhost, ghost);
                break;
        }
    }

    private void draw(Point point, DrawingToolCore core) {
        Rectangle oldGhost;
        switch (currentState) {
            case IDLE:
                break;
            case BEGIN:
                oldGhost = ghost;
                ghost = new Rectangle(p0, point);
                gotoState(PossibleState.RECTANGLE);
                firePropertyChange(GHOST_PROPERTY, oldGhost, ghost);
                break;
            case RECTANGLE:
                oldGhost = ghost;
                ghost = new Rectangle(p0, point);
                gotoState(PossibleState.RECTANGLE);
                firePropertyChange(GHOST_PROPERTY, oldGhost, ghost);
                break;
        }
    }

    private void endDraw(DrawingToolCore core) {
        switch (currentState) {
            case IDLE:
                break;
            case BEGIN:
                gotoState(PossibleState.IDLE);
                break;
            case RECTANGLE:
                Rectangle oldGhost = ghost;
                ghost = null;

               //core.createShape(oldGhost);
                Command com = new CreateShapeCommand(core, oldGhost);
                getUndoManager().registerCommand(com);

                gotoState(PossibleState.IDLE);
                firePropertyChange(GHOST_PROPERTY, oldGhost, ghost);
                firePropertyChange(SHAPES_PROPERTY, null, core.getShapes());
                break;
        }
    }

    private void gotoState(PossibleState possibleState) {
        currentState = possibleState;
        enableEvents(currentState.beginDrawEnabled, currentState.endDrawEnabled, currentState.drawEnabled, currentState.cancelDrawEnabled);
    }

    private void enableEvents(
            boolean beginDrawEnabled,
            boolean endDrawEnabled,
            boolean drawEnabled,
            boolean cancelDrawEnabled) {
        fireEventAvailabilityChanged(BEGIN_DRAW, beginDrawEnabled);
        fireEventAvailabilityChanged(CANCEL_DRAW, cancelDrawEnabled);
        fireEventAvailabilityChanged(DRAW, drawEnabled);
        fireEventAvailabilityChanged(END_DRAW, endDrawEnabled);

    }

    private void fireEventAvailabilityChanged(DrawingEventType drawingEventType, boolean newAvailability) {
        Boolean oldAvailability = eventAvailability.get(drawingEventType);
        eventAvailability.put(drawingEventType, newAvailability);
        firePropertyChange(drawingEventType.getPropertyName(), oldAvailability, newAvailability);
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

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        support.firePropertyChange(propertyName, oldValue, newValue);
    }

    private enum PossibleState {
        IDLE(true, false, false, false), BEGIN(false, true, true, true), RECTANGLE(false, true, true, true);
        public final boolean beginDrawEnabled;
        public final boolean endDrawEnabled;
        public final boolean drawEnabled;
        public final boolean cancelDrawEnabled;

        private PossibleState(boolean beginDrawEnabled, boolean endDrawEnabled, boolean drawEnabled, boolean cancelDrawEnabled) {
            this.beginDrawEnabled = beginDrawEnabled;
            this.endDrawEnabled = endDrawEnabled;
            this.drawEnabled = drawEnabled;
            this.cancelDrawEnabled = cancelDrawEnabled;
        }
    }

    private PossibleState currentState;

    public RectangleStateMachine() {
        support = new PropertyChangeSupport(this);
        ghost = null;
        eventAvailability = new EnumMap<>(DrawingEventType.class);
        for (DrawingEventType eventType : values()) {
            eventAvailability.put(eventType, null);
        }
    }

    @Override
    public void handleEvent(DrawingEvent event, DrawingToolCore core) {
        switch (event.getEventType()) {
            case BEGIN_DRAW:
                beginDraw(event.getPoint(), core);
                break;
            case CANCEL_DRAW:
                cancelDraw(core);
                break;
            case DRAW:
                draw(event.getPoint(), core);
                break;
            case END_DRAW:
                endDraw(core);
                break;
        }
    }

    @Override
    public void init(DrawingToolCore core) {
        Rectangle oldGhost = ghost;
        ghost = null;
        gotoState(PossibleState.IDLE);
        firePropertyChange(GHOST_PROPERTY, oldGhost, null);
        firePropertyChange(SHAPES_PROPERTY, null, core.getShapes());
    }

}
