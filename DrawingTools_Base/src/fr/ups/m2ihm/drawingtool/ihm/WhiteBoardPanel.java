/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package fr.ups.m2ihm.drawingtool.ihm;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author David
 */
public class WhiteBoardPanel extends JPanel {
    List<DrawingShape> drawingShapes;
    
    public void addShape(DrawingShape drawingShape) {
        drawingShapes.add(drawingShape);
        repaint();
    }
    
    public void removeShape(DrawingShape drawingShape) {
        drawingShapes.remove(drawingShape);
        repaint();
    }
    
    public void clear() {
        drawingShapes.clear();
        repaint();
    }

    public WhiteBoardPanel() {
        drawingShapes = new ArrayList<>();
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawingShapes.forEach((drawingShape) -> {
            drawingShape.paint(g);
        });
    }
    
}
