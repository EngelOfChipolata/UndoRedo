/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ups.m2ihm.drawingtool.ihm;

import fr.ups.m2ihm.drawingtool.model.core.Triangle;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author guilhem
 */
public class DrawingTriangle implements DrawingShape{
    
    private final Point p0;
    private final Point p1;
    private final Point p2;
    private final Color color;
    
    public DrawingTriangle(Triangle triangle, Color color){
        this.p0 = triangle.getA();
        this.p1 = triangle.getB();
        this.p2 = triangle.getC();
        this.color = color;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DrawingTriangle other = (DrawingTriangle) obj;
        List<Point> oPoints = new ArrayList<>(); //All the 3 points of the other triangle
        oPoints.add(other.p0);
        oPoints.add(other.p1);
        oPoints.add(other.p2);
        
        for (Point p: oPoints){ //Checks if all the 3 points are equals regardless of the order.
            if (p.equals(this.p0)){
                oPoints.remove(p);
                break;
            }
        }
        for (Point p: oPoints){
            if (p.equals(this.p1)){
                oPoints.remove(p);
                break;
            }
        }
        for (Point p: oPoints){
            if (p.equals(this.p2)){
                oPoints.remove(p);
                break;
            }
        }
        return oPoints.isEmpty();
    }

    @Override
    public int hashCode() {
        int hash = 11;
        hash = 103 * hash + (this.p0 != null ? this.p0.hashCode() : 0);
        hash = 103 * hash + (this.p1 != null ? this.p1.hashCode() : 0);
        hash = 103 * hash + (this.p2 != null ? this.p2.hashCode() : 0);
        hash = 103 * hash + (this.color != null ? this.color.hashCode() : 0);
        return hash;
    }
    
    

    @Override
    public void paint(Graphics graphics) {
        Color oldcolor = graphics.getColor();
        graphics.setColor(color);
        graphics.drawLine(p0.x, p0.y, p1.x, p1.y);
        graphics.drawLine(p1.x, p1.y, p2.x, p2.y);
        graphics.drawLine(p2.x, p2.y, p0.x, p0.y);
        graphics.setColor(oldcolor);
    }
    
}
