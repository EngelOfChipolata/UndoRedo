/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package fr.ups.m2ihm.drawingtool.ihm;

import fr.ups.m2ihm.drawingtool.model.core.Rectangle;
import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author David
 */
public class DrawingRectangle implements DrawingShape {
    private final int x0;
    private final int y0;
    private final int w;
    private final int h;
    
    private final Color color;

    public DrawingRectangle(Rectangle rectangle, Color color) {
        this.color = color;
        this.x0 = rectangle.getUpperLeftCorner().x;
        this.y0 = rectangle.getUpperLeftCorner().y;
        this.w = rectangle.getLowerRightCorner().x - rectangle.getUpperLeftCorner().x;
        this.h = rectangle.getLowerRightCorner().y - rectangle.getUpperLeftCorner().y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DrawingRectangle other = (DrawingRectangle) obj;
        if (this.x0 != other.x0) {
            return false;
        }
        if (this.y0 != other.y0) {
            return false;
        }
        if (this.w != other.w) {
            return false;
        }
        if (this.h != other.h) {
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
        hash = 61 * hash + this.x0;
        hash = 61 * hash + this.y0;
        hash = 61 * hash + this.w;
        hash = 61 * hash + this.h;
        hash = 61 * hash + (this.color != null ? this.color.hashCode() : 0);
        return hash;
    }

    @Override
    public void paint(Graphics graphics) {
        Color oldcolor = graphics.getColor();
        graphics.setColor(color);
        graphics.drawRect(x0, y0, w, h);
        graphics.setColor(oldcolor);
    }
    
}
