/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ups.m2ihm.drawingtool.macrocommand;

import fr.ups.m2ihm.drawingtool.model.CreateShapeCommand;
import fr.ups.m2ihm.drawingtool.model.core.DrawingToolCore;
import fr.ups.m2ihm.drawingtool.model.core.Line;
import fr.ups.m2ihm.drawingtool.model.core.Rectangle;
import fr.ups.m2ihm.drawingtool.model.core.Shape;
import fr.ups.m2ihm.drawingtool.model.core.Triangle;
import fr.ups.m2ihm.drawingtool.undomanager.Command;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author guilhem
 */
public class MacroCommand implements Command{
    
    private String name;
    private List<Command> commands;
    private Point originPoint;
    private Point creationPoint;

    public MacroCommand() {
        name = null;
        commands = new ArrayList<>();
    }
    
    public MacroCommand(MacroCommand m){
        this.name = m.getName();
        this.commands = new ArrayList<>(m.getCommands());
        this.originPoint = m.getOriginPoint();
    }
    

    @Override
    public void execute() {
        commands.forEach((Command c) -> {
            c.execute();
        });
    }

    @Override
    public void undo() {
        System.out.println("Undo !" + commands.size());
        for (int i = commands.size() - 1; i >= 0; i--){ //Undo the commands backward !
            commands.get(i).undo();
            System.out.println("Undo");
        }
    }
    
    public void add(Command c){
        commands.add(c);
    }
    
    public void normalize(){        
        for (Command c : getCommands()){
            if (c instanceof CreateShapeCommand){
                Shape s = ((CreateShapeCommand) c).getShape();
                if (s instanceof Line){
                    originPoint = upperLeftPoint(originPoint, ((Line) s).getSource());
                    originPoint = upperLeftPoint(originPoint, ((Line) s).getDestination());
                }else if (s instanceof Rectangle){
                    originPoint = upperLeftPoint(originPoint, ((Rectangle) s).getUpperLeftCorner());
                }else if (s instanceof Triangle){
                    originPoint = upperLeftPoint(originPoint, ((Triangle) s).getA());
                    originPoint = upperLeftPoint(originPoint, ((Triangle) s).getB());
                    originPoint = upperLeftPoint(originPoint, ((Triangle) s).getC());
                }
            }else if(c instanceof MacroCommand){
                originPoint = upperLeftPoint(originPoint, ((MacroCommand) c).getCreationPoint());
            }
            System.out.println(originPoint);
        }
    }
    
    private Point upperLeftPoint(Point p1, Point p2){
        if (p1==null && p2!=null){
            return p2;
        }
        if (p2==null && p1!=null){
            return p1;
        }
        if (p1 != null && p2 != null){
            return new Point((int)Math.min(p1.getX(), p2.getX()), (int)Math.min(p1.getY(), p2.getY()));
        }
        return null;
    }
    
    public void removeLast(){
        Command c = commands.remove(commands.size() - 1);
        c.undo();
    }
    
    public void setCreationPoint(Point p){
        List<Command> normalizedCommands = new ArrayList<>();
        this.creationPoint = p;
        for (Command c : getCommands()){
            if (c instanceof CreateShapeCommand){
                Shape s = ((CreateShapeCommand) c).getShape();
                DrawingToolCore core = ((CreateShapeCommand) c).getCore();
                if (s instanceof Line){
                    Point source = new Point((int)(((Line) s).getSource().getX() - originPoint.getX() + p.getX()),
                    (int)(((Line) s).getSource().getY() - originPoint.getY() + p.getY()));
                    Point dest = new Point((int)(((Line) s).getDestination().getX() - originPoint.getX() + p.getX()),
                    (int)(((Line) s).getDestination().getY() - originPoint.getY() + p.getY()));
                    normalizedCommands.add(new CreateShapeCommand(core, new Line(source, dest)));
                } else if (s instanceof Rectangle){
                    Point upperLeftPoint = new Point((int)(((Rectangle) s).getUpperLeftCorner().getX() - originPoint.getX() + p.getX()),
                    (int)(((Rectangle) s).getUpperLeftCorner().getY() - originPoint.getY() + p.getY()));
                    Point lowerRightPoint = new Point((int)(((Rectangle) s).getLowerRightCorner().getX() - originPoint.getX() + p.getX()),
                    (int)(((Rectangle) s).getLowerRightCorner().getY() - originPoint.getY() + p.getY()));
                    normalizedCommands.add(new CreateShapeCommand(core, new Rectangle(upperLeftPoint, lowerRightPoint)));
                }else if (s instanceof Triangle){
                    Point pointa = new Point((int)(((Triangle) s).getA().getX() - originPoint.getX() + p.getX()),
                    (int)(((Triangle) s).getA().getY() - originPoint.getY() + p.getY()));
                    Point pointb = new Point((int)(((Triangle) s).getB().getX() - originPoint.getX() + p.getX()),
                    (int)(((Triangle) s).getB().getY() - originPoint.getY() + p.getY()));
                    Point pointc = new Point((int)(((Triangle) s).getC().getX() - originPoint.getX() + p.getX()),
                    (int)(((Triangle) s).getC().getY() - originPoint.getY() + p.getY()));
                    normalizedCommands.add(new CreateShapeCommand(core, new Triangle(pointa, pointb, pointc)));
                }
            }else if(c instanceof MacroCommand){
                MacroCommand m = new MacroCommand((MacroCommand) c);
                m.setCreationPoint(new Point((int)(p.getX() + m.getOriginPoint().getX() - originPoint.getX()), 
                        (int)(p.getY() + m.getOriginPoint().getY() - originPoint.getY())));
                System.out.println(m.getCreationPoint());
                normalizedCommands.add(m);
            }
        }
        commands = normalizedCommands;
        
    }
    
    public String getName(){
        return name;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public List<Command> getCommands(){
        return this.commands;
    }
    
    public Point getCreationPoint(){
        return this.creationPoint;
    }

    @Override
    public String toString() {
        return "Macro " + getName() + " @ " + getCreationPoint();
    }
    
    public Point getOriginPoint(){
        return this.originPoint;
    }
    
    public int commandSize(){
        return this.commands.size();
    }
    
    
}
