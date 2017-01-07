/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ups.m2ihm.drawingtool.model;

import static fr.ups.m2ihm.drawingtool.model.DrawingEventType.BEGIN_DRAW;
import static fr.ups.m2ihm.drawingtool.model.DrawingEventType.CANCEL_DRAW;
import static fr.ups.m2ihm.drawingtool.model.DrawingEventType.DRAW;
import static fr.ups.m2ihm.drawingtool.model.DrawingEventType.END_DRAW;
import static fr.ups.m2ihm.drawingtool.model.DrawingEventType.values;
import fr.ups.m2ihm.drawingtool.model.core.DrawingToolCore;
import fr.ups.m2ihm.drawingtool.model.core.Rectangle;
import fr.ups.m2ihm.drawingtool.model.core.Triangle;
import fr.ups.m2ihm.drawingtool.undomanager.Command;
import fr.ups.m2ihm.drawingtool.undomanager.UndoManager;
import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.EnumMap;
import java.util.Map;

/**
 *
 * @author guilhem
 */
public class TriangleStateMachine implements DrawingStateMachine {

    private final PropertyChangeSupport support;
    private Triangle ghost;
    private Point p0;
    private Point p1;
    private final Map<DrawingEventType, Boolean> eventAvailability;
    private UndoManager undoManager;

    private enum PossibleState {
        IDLE(true, false, false, false),
        FIRST_POINT_BEGIN(false, true, true, true),
        FIRST_POINT_END(true, false, false, true),
        SECOND_POINT_BEGIN(false, true, true, true),
        SECOND_POINT_END(true, false, false, true),
        THIRD_POINT_BEGIN(false, true, true, true);
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

    public TriangleStateMachine() {
        support = new PropertyChangeSupport(this);
        ghost = null;
        eventAvailability = new EnumMap<>(DrawingEventType.class);
        for (DrawingEventType eventType : values()) {
            eventAvailability.put(eventType, null);
        }
    }

    public UndoManager getUndoManager() {
        return undoManager;
    }

    @Override
    public void setUndoManager(UndoManager undoManager) {
        this.undoManager = undoManager;
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

    @Override
    public void init(DrawingToolCore core) {
        Triangle oldGhost = ghost;
        ghost = null;
        gotoState(PossibleState.IDLE);
        firePropertyChange(GHOST_PROPERTY, oldGhost, null);
        firePropertyChange(SHAPES_PROPERTY, null, core.getShapes());
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
                endDraw(event.getPoint(), core);
                break;
        }    }

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
    
    private Point findLastPointForEquilateral(Point p1, Point p2){
        double x = (p2.x - p1.x) * 1/2 - (p2.y - p1.y) * 0.866 + p1.x;
        double y = (p2.x - p1.x) * 0.866 + (p2.y - p1.y) * 1/2 + p1.y;
        return new Point((int)Math.round(x), (int)Math.round(y));
        
    }
    
    
    private void beginDraw(Point point, DrawingToolCore core) {
        Triangle oldGhost;
        switch (currentState) {
            case IDLE:
                gotoState(PossibleState.FIRST_POINT_BEGIN);
                oldGhost = ghost;
                Point ptemp = new Point (point.x + 50, point.y + 50);
                ghost = new Triangle(point, ptemp, findLastPointForEquilateral(point, ptemp));
                firePropertyChange(GHOST_PROPERTY, oldGhost, ghost);
                break;
            case FIRST_POINT_BEGIN:
                break;
            case FIRST_POINT_END:
                gotoState(PossibleState.SECOND_POINT_BEGIN);
                oldGhost = ghost;
                ghost = new Triangle(p0, point, findLastPointForEquilateral(p0, point));
                firePropertyChange(GHOST_PROPERTY, oldGhost, ghost);
                break;
            case SECOND_POINT_BEGIN:
                break;
            case SECOND_POINT_END:
                gotoState(PossibleState.THIRD_POINT_BEGIN);
                oldGhost = ghost;
                ghost = new Triangle(p0, p1, point);
                firePropertyChange(GHOST_PROPERTY, oldGhost, ghost);
                break;
            case THIRD_POINT_BEGIN:
                break;
        }    
    }
    
    private void draw(Point point, DrawingToolCore core){
        Triangle oldGhost;
        switch(currentState){
            case IDLE:
                break;
            case FIRST_POINT_BEGIN:
                gotoState(PossibleState.FIRST_POINT_BEGIN);
                oldGhost = ghost;
                Point ptemp = new Point (point.x + 50, point.y + 50);
                ghost = new Triangle(point, ptemp, findLastPointForEquilateral(point, ptemp));
                firePropertyChange(GHOST_PROPERTY, oldGhost, ghost);
                break;
            case FIRST_POINT_END:
                break;
            case SECOND_POINT_BEGIN:
                gotoState(PossibleState.SECOND_POINT_BEGIN);
                oldGhost = ghost;
                ghost = new Triangle(p0, point, findLastPointForEquilateral(p0, point));
                firePropertyChange(GHOST_PROPERTY, oldGhost, ghost);
                break;
            case SECOND_POINT_END:
                break;
            case THIRD_POINT_BEGIN:
                gotoState(PossibleState.THIRD_POINT_BEGIN);
                oldGhost = ghost;
                ghost = new Triangle(p0, p1, point);
                firePropertyChange(GHOST_PROPERTY, oldGhost, ghost);
                break;
            
        }
    }
    
    public void endDraw(Point point, DrawingToolCore core){
        Triangle oldGhost;
        switch(currentState){
            case IDLE:
                break;
            case FIRST_POINT_BEGIN:
                gotoState(PossibleState.FIRST_POINT_END);
                p0 = point;
                break;
            case FIRST_POINT_END:
                break;
            case SECOND_POINT_BEGIN:
                gotoState(PossibleState.SECOND_POINT_END);
                p1 = point;
                break;
            case SECOND_POINT_END:
                break;
            case THIRD_POINT_BEGIN:
                oldGhost = ghost;
                ghost = null;

               //core.createShape(oldGhost);
                Command com = new CreateShapeCommand(core, oldGhost);
                getUndoManager().registerCommand(com);

                gotoState(PossibleState.IDLE);
                firePropertyChange(GHOST_PROPERTY, oldGhost, ghost);
                firePropertyChange(SHAPES_PROPERTY, null, core.getShapes());
                break;
            default:
                throw new AssertionError(currentState.name());
            
        }
    }
    
    public void cancelDraw(DrawingToolCore core){
        switch(currentState){
            case IDLE:
                break;
            case FIRST_POINT_BEGIN:
                break;
            case FIRST_POINT_END:
                break;
            case SECOND_POINT_BEGIN:
                break;
            case SECOND_POINT_END:
                break;
            case THIRD_POINT_BEGIN:
                break;
            default:
                throw new AssertionError(currentState.name());
            
        }
    }

}
