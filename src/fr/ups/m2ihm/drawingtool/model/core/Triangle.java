/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ups.m2ihm.drawingtool.model.core;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author guilhem
 */
public class Triangle implements Shape{
    private Point a, b, c;

    public Triangle(Point a, Point b, Point c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public String toString() {
        return "Triangle{" + "a=" + a + ", b=" + b + ", c=" + c + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Triangle other = (Triangle) obj;
        List<Point> oPoints = new ArrayList<>(); //All the 3 points of the other triangle
        oPoints.add(other.getA());
        oPoints.add(other.getB());
        oPoints.add(other.getC());
        
        for (Point p: oPoints){ //Checks if all the 3 points are equals regardless of the order.
            if (p.equals(this.a)){
                oPoints.remove(p);
                break;
            }
        }
        for (Point p: oPoints){
            if (p.equals(this.b)){
                oPoints.remove(p);
                break;
            }
        }
        for (Point p: oPoints){
            if (p.equals(this.c)){
                oPoints.remove(p);
                break;
            }
        }
        return oPoints.isEmpty();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 103 * hash + (this.a != null ? this.a.hashCode() : 0);
        hash = 103 * hash + (this.b != null ? this.b.hashCode() : 0);
        hash = 103 * hash + (this.c != null ? this.c.hashCode() : 0);
        return hash;
    }

    public Point getA() {
        return a;
    }

    public Point getB() {
        return b;
    }

    public Point getC() {
        return c;
    }   
}
