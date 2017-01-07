/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ups.m2ihm.drawingtool.model;

import fr.ups.m2ihm.drawingtool.model.core.DrawingToolCore;
import fr.ups.m2ihm.drawingtool.model.core.Line;
import fr.ups.m2ihm.drawingtool.model.core.Rectangle;
import fr.ups.m2ihm.drawingtool.model.core.Triangle;
import fr.ups.m2ihm.drawingtool.model.core.Shape;
import fr.ups.m2ihm.drawingtool.undomanager.Command;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author David Navarre
 */
public class CreateShapeCommand implements Command {

    private final DrawingToolCore core;
    private final Shape shape;

    public CreateShapeCommand(DrawingToolCore core, Shape shape) {
        this.core = core;
        this.shape = shape;
    }

    @Override
    public void execute() {
        core.createShape(shape);
    }

    @Override
    public void undo() {
        core.removeShape(shape);
    }

    @Override
    public String toString() {
        return "Create " + shape.toString();
    }
    
    
    
    public boolean isInside(Rectangle rectangle){
        Point upperLeftBoundary = rectangle.getUpperLeftCorner();
        Point lowerRightBoundary = rectangle.getLowerRightCorner();
        if (shape instanceof Rectangle){
            Point shapeUpperLeft = ((Rectangle) shape).getUpperLeftCorner();
            Point shapeLowerRight = ((Rectangle) shape).getLowerRightCorner();

            return (shapeUpperLeft.getX() >= upperLeftBoundary.getX() &&
                    shapeUpperLeft.getY() >= upperLeftBoundary.getY() &&
                    shapeLowerRight.getX() <= lowerRightBoundary.getX() &&
                    shapeLowerRight.getY() <= lowerRightBoundary.getY());
        }
        if (shape instanceof Line){
            Line line = (Line) shape;
            return (line.getSource().getX() >= upperLeftBoundary.getX() &&
                    line.getSource().getX() <= lowerRightBoundary.getX() &&
                    line.getSource().getY() >= upperLeftBoundary.getY() &&
                    line.getSource().getY() <= lowerRightBoundary.getY() &&
                    line.getDestination().getX() >= upperLeftBoundary.getX() &&
                    line.getDestination().getX() <= lowerRightBoundary.getX() &&
                    line.getDestination().getY() >= upperLeftBoundary.getY() &&
                    line.getDestination().getY() <= lowerRightBoundary.getY());
        }
        if (shape instanceof Triangle){
            Point a = ((Triangle) shape).getA();
            Point b = ((Triangle) shape).getB();
            Point c = ((Triangle) shape).getC();
            
            return (a.getX() >= upperLeftBoundary.getX() &&
                    a.getX() <= lowerRightBoundary.getX() &&
                    a.getY() >= upperLeftBoundary.getY() &&
                    a.getY() <= lowerRightBoundary.getY() &&
                    b.getX() >= upperLeftBoundary.getX() &&
                    b.getX() <= lowerRightBoundary.getX() &&
                    b.getY() >= upperLeftBoundary.getY() &&
                    b.getY() <= lowerRightBoundary.getY() &&
                    c.getX() >= upperLeftBoundary.getX() &&
                    c.getX() <= lowerRightBoundary.getX() &&
                    c.getY() >= upperLeftBoundary.getY() &&
                    c.getY() <= lowerRightBoundary.getY()
                    );
        }
        throw new UnsupportedOperationException("Shape has not been recognized.");
        
    }

    public DrawingToolCore getCore() {
        return core;
    }

    public Shape getShape() {
        return shape;
    }
    
    

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CreateShapeCommand other = (CreateShapeCommand) obj;
        return (shape.equals(other.getShape()) && core.equals(other.getCore()));
    }
    
   
}
