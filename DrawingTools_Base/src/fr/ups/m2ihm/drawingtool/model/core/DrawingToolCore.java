package fr.ups.m2ihm.drawingtool.model.core;

import java.util.Set;

public interface DrawingToolCore {
    void createShape(Shape shape);
    void removeShape(Shape shape);
    Set<Shape> getShapes();
    void clearShapes();
}
