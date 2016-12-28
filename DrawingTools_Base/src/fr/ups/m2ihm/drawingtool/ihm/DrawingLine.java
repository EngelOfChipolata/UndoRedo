/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package fr.ups.m2ihm.drawingtool.ihm;

import fr.ups.m2ihm.drawingtool.model.core.Line;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

/**
 *
 * @author David
 */
public class DrawingLine implements DrawingShape {
    private final Point p0;
    private final Point p1;
    private final Color color;

    public DrawingLine(Line line, Color color) {
        this.color = color;
        this.p0 = line.getSource();
        this.p1 = line.getDestination();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DrawingLine other = (DrawingLine) obj;
        if (this.p0 != other.p0 && (this.p0 == null || !this.p0.equals(other.p0))) {
            return false;
        }
        if (this.p1 != other.p1 && (this.p1 == null || !this.p1.equals(other.p1))) {
            return false;
        }
        if (this.color != other.color && (this.color == null || !this.color.equals(other.color))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.p0 != null ? this.p0.hashCode() : 0);
        hash = 47 * hash + (this.p1 != null ? this.p1.hashCode() : 0);
        hash = 47 * hash + (this.color != null ? this.color.hashCode() : 0);
        return hash;
    }
    
    @Override
    public void paint(Graphics graphics) {
        Color oldcolor = graphics.getColor();
        graphics.setColor(color);
        graphics.drawLine(p0.x, p0.y, p1.x, p1.y);
        graphics.setColor(oldcolor);
    }
    
}
