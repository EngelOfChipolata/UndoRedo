package fr.ups.m2ihm.drawingtool.model.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DefaultDrawingToolCore implements DrawingToolCore {
    List<Shape> shapes;
    
    public DefaultDrawingToolCore() {
        shapes = new ArrayList<>();
    }

    @Override
    public void createShape(Shape shape) {
        shapes.add(shape);
    }

    @Override
    public void removeShape(Shape shape) {
        shapes.remove(shape);
    }

    @Override
    public Set<Shape> getShapes() {
        return new HashSet<>(shapes);
    }

    @Override
    public void clearShapes() {
        shapes.clear();
    }
}
