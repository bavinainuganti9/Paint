import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.filechooser.FileFilter;

public class PaintProgram extends JPanel implements MouseListener, MouseMotionListener, ActionListener, AdjustmentListener, ChangeListener{
    JFrame frame;
    JMenuBar menuBar;
    JMenu colorMenu, file;
    JMenuItem[] colorOptions;
    Color[] colors;
    JColorChooser colorChooser;
    JScrollBar penWidthBar;
    JLabel penWidthLabel;
    ArrayList<Point> points;
    //Stack<ArrayList<Point>> freeLines;
    JMenuItem save, load, clear, exit;
    ImageIcon saveImg, loadImg, rectImg, ovalImg, triangleImg, eraseImg, freeLineImg, straightLineImg, undoImg, redoImg;
    Stack<Object> shapes;
    Stack<Object> undoRedo;
    Shape currentShape;
    boolean drawingRectangle, drawingOval, drawingTriangle, drawingStraightLine, erasing;
    boolean drawingFreeLine=true;
    boolean firstClick;
    int inX, inY;
    JButton rectButton, ovalButton, triangleButton, eraseButton, freeLineButton, straightLineButton, redoButton, undoButton;

    int penWidth = 2;
    Color currentColor;
    Color backgroundColor;
    JFileChooser fileChooser;
    BufferedImage loadedImg;

    public static void main(String[] args) {
        PaintProgram paintProgram = new PaintProgram();
    }

    public PaintProgram(){
        frame = new JFrame("The Best Paint Program Ever Constructed");
        
        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        menuBar = new JMenuBar();
        save = new JMenuItem("Save", KeyEvent.VK_S);
        load = new JMenuItem("Load", KeyEvent.VK_L);
        clear = new JMenuItem("Clear");
        exit = new JMenuItem("Exit");

        save.addActionListener(this);
        load.addActionListener(this);
        clear.addActionListener(this);
        exit.addActionListener(this);

        saveImg = new ImageIcon("saveImg.png");
        saveImg = new ImageIcon(saveImg.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        save.setIcon(saveImg);

        loadImg = new ImageIcon("loadImg.png");
        loadImg = new ImageIcon(loadImg.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        load.setIcon(loadImg);

        file = new JMenu("File");
        file.setLayout(new GridLayout(4, 1));
        file.add(clear);
        file.add(load);
        file.add(save);
        file.add(exit);

        freeLineButton = new JButton();
        freeLineImg = new ImageIcon("freeLineImg.png");
        freeLineImg = new ImageIcon(freeLineImg.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        freeLineButton.setIcon(freeLineImg);
        freeLineButton.addActionListener(this);
        freeLineButton.setFocusable(false);

        straightLineButton = new JButton();
        straightLineImg = new ImageIcon("straightLineImg.png");
        straightLineImg = new ImageIcon(straightLineImg.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        straightLineButton.setIcon(straightLineImg);
        straightLineButton.addActionListener(this);
        straightLineButton.setFocusable(false);

        rectButton = new JButton();
        rectImg = new ImageIcon("rectImg.png");
        rectImg = new ImageIcon(rectImg.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        rectButton.setIcon(rectImg);
        rectButton.addActionListener(this);
        rectButton.setFocusable(false);

        ovalButton = new JButton();
        ovalImg = new ImageIcon("ovalImg.png");
        ovalImg = new ImageIcon(ovalImg.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        ovalButton.setIcon(ovalImg);
        ovalButton.addActionListener(this);
        ovalButton.setFocusable(false);

        triangleButton = new JButton();
        triangleImg = new ImageIcon("triangleImg.png");
        triangleImg = new ImageIcon(triangleImg.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        triangleButton.setIcon(triangleImg);
        triangleButton.addActionListener(this);
        triangleButton.setFocusable(false);

        undoButton = new JButton();
        undoImg = new ImageIcon("undoImg.png");
        undoImg = new ImageIcon(undoImg.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        undoButton.setIcon(undoImg);
        undoButton.addActionListener(this);
        undoButton.setFocusable(false);

        redoButton = new JButton();
        redoImg = new ImageIcon("redoImg.png");
        redoImg = new ImageIcon(redoImg.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        redoButton.setIcon(redoImg);
        redoButton.addActionListener(this);
        redoButton.setFocusable(false);

        eraseButton = new JButton();
        eraseImg = new ImageIcon("eraserImg.png");
        eraseImg = new ImageIcon(eraseImg.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        eraseButton.setIcon(eraseImg);
        eraseButton.addActionListener(this);
        eraseButton.setFocusable(false);

        colorMenu = new JMenu("Colors");
        colorMenu.addActionListener(this);
        colors = new Color[]{Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.PINK, Color.WHITE, Color.BLACK};
        colorOptions = new JMenuItem[colors.length];
        colorMenu.setLayout(new GridLayout(colors.length, 1));
        for(int i=0;i<colors.length;i++){
            colorOptions[i] = new JMenuItem();
            colorOptions[i].putClientProperty("colorIndex", i);
            colorOptions[i].setOpaque(true);
            colorOptions[i].setBackground(colors[i]);
            colorOptions[i].addActionListener(this);
            colorMenu.add(colorOptions[i]);
        }

        colorChooser = new JColorChooser();
        colorChooser.getSelectionModel().addChangeListener(this);
        colorMenu.add(colorChooser);

        String currentDir = System.getProperty("user.dir");
        fileChooser = new JFileChooser(currentDir);
        
        penWidthBar = new JScrollBar(JScrollBar.HORIZONTAL, 2, 0, 1, 100);
        penWidthBar.addAdjustmentListener(this);
        penWidth = penWidthBar.getValue();
        penWidthLabel = new JLabel("Pen Width: " + penWidth);


        points = new ArrayList<Point>();
        shapes=new Stack<Object>();
        undoRedo = new Stack<Object>();
        //freeLines = new Stack<ArrayList<Point>>();
        currentColor = colors[0];
        backgroundColor = Color.WHITE;
        firstClick=true;

        menuBar.add(file);
        menuBar.add(colorMenu);
        menuBar.add(freeLineButton);
        menuBar.add(straightLineButton);
        menuBar.add(rectButton);
        menuBar.add(ovalButton);
        menuBar.add(triangleButton);
        menuBar.add(undoButton);
        menuBar.add(redoButton);
        menuBar.add(eraseButton);
        
        menuBar.add(penWidthLabel);
        menuBar.add(penWidthBar);

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        frame.add(this);
        frame.add(menuBar, BorderLayout.NORTH);
        frame.setSize(1400, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        frame.setVisible(true);
    }

    public class Point{
        int x, y, penWidth;
        Color color;

        public Point(int x, int y, Color color, int penWidth){
            this.x = x;
            this.y = y;
            this.color = color;
            this.penWidth = penWidth;
        }

        public int getX(){
            return x;
        }

        public int getY(){
            return y;
        }

        public int getPenWidth(){
            return penWidth;
        }

        public Color getColor(){
            return color;
        }
    }

    public class Shape{
        int x, y, penWidth, width, height;
        Color color;

        public Shape(int x, int y, int penWidth, int width, int height, Color color){
            this.x = x;
            this.y = y;
            this.penWidth = penWidth;
            this.width = width;
            this.height = height;
            this.color = color;
        }

        public int getX(){
            return x;
        }
        
        public int getY(){
            return y;
        }

        public int getPenWdith(){
            return penWidth;
        }

        public int getWidth(){
            return width;
        }

        public int getHeight(){
            return height;
        }

        public Color getColor(){
            return color;
        }

        public void setX(int x){
            this.x = x;
        }

        public void setY(int y){
            this.y = y;
        }

        public void setWidth(int width){
            this.width = width;
        }

        public void setHeight(int height){
            this.height = height;
        }
    }

    public class Rectangle extends Shape{

        public Rectangle(int x, int y, int penWidth, int width, int height, Color color){
            super(x, y, penWidth, width, height, color);
        }

        public Rectangle2D.Double getShape(){
            return new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight());
        }
    }

    public class Oval extends Shape{

        public Oval(int x, int y, int penWidth, int width, int height, Color color){
            super(x, y, penWidth, width, height, color);
        }

        public Ellipse2D.Double getShape(){
            return new Ellipse2D.Double(getX(), getY(), getWidth(), getHeight());
        }
    }

    public class Triangle extends Shape{

        public Triangle(int x, int y, int penWidth, int width, int height, Color color){
            super(x, y, penWidth, width, height, color);
        }

        public ArrayList<Line2D.Double> getShape(){
            ArrayList<Line2D.Double> list = new ArrayList<>();

            list.add(new Line2D.Double(getX(), getY(), getX() - (getWidth()/2), getY() - getHeight()));
            list.add(new Line2D.Double(getX() - (getWidth()/2), getY() - getHeight(), getX() + (getWidth()/2), getY() - getHeight()));
            list.add(new Line2D.Double(getX() + (getWidth()/2), getY() - getHeight(), getX(), getY()));

            return list;
        }
    }

    public class StraightLine extends Shape{
        public StraightLine(int x, int y, int penWidth, int width, int height, Color color){
            super(x, y, penWidth, width, height, color);
        }

        public Line2D.Double getShape(){
            return new Line2D.Double(getX(), getY(), getWidth(), getHeight());
        }
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setColor(backgroundColor);
        g.fillRect(0, 0, frame.getWidth(), frame.getHeight());

        if(loadedImg != null){
            g.drawImage(loadedImg, 0, 0, null);
        }

        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //Iterator it = freeLines.iterator();
        if(shapes.size()>0)
        {
            Iterator it = shapes.iterator();
            while(it.hasNext()){
                Object shape = it.next();
                System.out.println(shape.getClass().getName());
                if(shape instanceof BufferedImage){
                    g.drawImage(loadedImg, 0, 0, this);
                }
                else if(shape instanceof ArrayList<?>){
                    ArrayList<Point> p = (ArrayList<Point>)shape;
                    g.setColor(p.get(0).getColor());
                    g2.setStroke(new BasicStroke(p.get(0).getPenWidth(),BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    for(int a=0;a<p.size() - 1;a++){
                        Point p1 = p.get(a);
                        Point p2 = p.get(a+1);
                        g.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
                    } 
                }
                else if(shape instanceof Rectangle){
                    Rectangle rect = (Rectangle)shape;
                    g.setColor(rect.getColor());
                    g2.draw(rect.getShape());
                }
                else if(shape instanceof Oval){
                    Oval oval = (Oval)shape;
                    g.setColor(oval.getColor());
                    g2.draw(oval.getShape());            
                }
                else if(shape instanceof Triangle){
                    Triangle triangle = (Triangle)shape;
                    g.setColor(triangle.getColor());
                    ArrayList<Line2D.Double> list = triangle.getShape();
                    for(Line2D.Double line : list){
                        g2.draw(line);
                    }
                }
                else if(shape instanceof StraightLine){
                    StraightLine straightLine = (StraightLine)shape;
                    g.setColor(straightLine.getColor());
                    g2.draw(straightLine.getShape());            
                }
            }
        }

        if(drawingFreeLine && points.size() > 0){
            g.setColor(points.get(0).getColor());
            g2.setStroke(new BasicStroke(points.get(0).getPenWidth()));

            for(int a=0;a<points.size() - 1;a++){
                Point p1 = points.get(a);
                Point p2 = points.get(a+1);
                g.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            }
        }
    }

    public BufferedImage createImage(){
        int width = this.getWidth();
        int height = this.getHeight();
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        this.paint(g2);
        g2.dispose();
        return img;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        currentColor = colorChooser.getColor();
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        if(e.getSource() == penWidthBar){
            penWidth = penWidthBar.getValue();
            penWidthLabel.setText("Pen Width: " + penWidth);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == eraseButton){
            erasing = true;
            drawingFreeLine = true;
            drawingStraightLine = false;
            drawingRectangle = false;
            drawingOval = false;
            drawingTriangle = false;
        }
        else if(e.getSource() == freeLineButton){
            erasing = false;
            drawingFreeLine = true;
            drawingStraightLine = false;
            drawingRectangle = false;
            drawingOval = false;
            drawingTriangle = false;
        }
        else if(e.getSource() == straightLineButton){
            erasing = false;
            drawingFreeLine = false;
            drawingStraightLine = true;
            drawingRectangle = false;
            drawingOval = false;
            drawingTriangle = false;
        }
        else if(e.getSource() == rectButton){
            erasing = false;
            drawingFreeLine = false;
            drawingStraightLine = false;
            drawingRectangle = true;
            drawingOval = false;
            drawingTriangle = false;
        }
        else if(e.getSource() == ovalButton){
            erasing = false;
            drawingFreeLine = false;
            drawingStraightLine = false;
            drawingRectangle = false;
            drawingOval = true;
            drawingTriangle = false;
        }
        else if(e.getSource() == triangleButton){
            erasing = false;
            drawingFreeLine = false;
            drawingStraightLine = false;
            drawingRectangle = false;
            drawingOval = false;
            drawingTriangle = true;
        }
        else if(e.getSource() == undoButton){
            if(!shapes.isEmpty()){
                undoRedo.push(shapes.pop());
                repaint();
            }
        }
        else if(e.getSource() == redoButton){
            if(!undoRedo.isEmpty()){
                shapes.push(undoRedo.pop());
                repaint();
            }
        }
        else if(e.getSource() == clear){
            //freeLines = new Stack<ArrayList<Point>>();
            points = new ArrayList<>();
            shapes = new Stack<>();
            loadedImg = null;
            repaint();
        }
        else if(e.getSource() == load){
            fileChooser.showOpenDialog(null);
            File imgFile = fileChooser.getSelectedFile();
            if(imgFile != null && imgFile.toString().indexOf(".png") >= 0){
                try{
                    loadedImg = ImageIO.read(imgFile);

                }catch(IOException ee){}
                //freeLines = new Stack<ArrayList<Point>>();
                repaint();
            }
            else if(imgFile != null){
                JOptionPane.showMessageDialog(null, "Wrong file type. Please select a new file.");
            }
        }
        else if(e.getSource() == save){
            FileFilter filter = new FileNameExtensionFilter("*.png", "png");
            fileChooser.setFileFilter(filter);
            if(fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){
                File file = fileChooser.getSelectedFile();
                try{
                    String str = file.getAbsolutePath();
                    if(str.indexOf(".png") >= 0){
                        str = str.substring(0, str.length() - 4);
                    }
                    ImageIO.write(createImage(), "png", new File(str+".png"));
                }catch(IOException ee){}
                

            }
        }
        else if(e.getSource() == exit){
            System.exit(0);
        }
        else{
            int index = (int)((JMenuItem)e.getSource()).getClientProperty("colorIndex");
            currentColor = colors[index];
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'mouseClicked'");
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'mouseEntered'");
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'mouseExited'");
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'mousePressed'");
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(drawingFreeLine){
            if(points.size() > 0){
                shapes.push(points);
                points = new ArrayList<Point>();
            }
        }
        firstClick = true;
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        if(drawingFreeLine){
            Color c = currentColor;
            if(erasing){
                c = backgroundColor;
            }
            points.add(new Point(e.getX(), e.getY(), c, penWidth));
        }
        else if(drawingRectangle){
            if(firstClick){
                inX = e.getX();
                inY = e.getY();
                shapes.push(new Rectangle(inX, inY, penWidth, 0, 0, currentColor));
                firstClick = false;
            } else {
                int width = Math.abs(inX - e.getX());
                int height = Math.abs(inY - e.getY());
                Rectangle rect = (Rectangle) shapes.peek();
                rect.setWidth(width);
                rect.setHeight(height);
                if(e.getX() < inX){
                    rect.setX(e.getX());
                }
                if(e.getY() < inY){
                    rect.setY(e.getY());
                }
            } 
        }
        else if(drawingOval){
            if(firstClick){
                inX = e.getX();
                inY = e.getY();
                shapes.push(new Oval(inX, inY, penWidth, 0, 0, currentColor));
                firstClick = false;
            } else {
                int width = Math.abs(inX - e.getX());
                int height = Math.abs(inY - e.getY());
                Oval oval = (Oval) shapes.peek();
                oval.setWidth(width);
                oval.setHeight(height);
                if(e.getX() < inX){
                    oval.setX(e.getX());
                }
                if(e.getY() < inY){
                    oval.setY(e.getY());
                }
            } 
        }
        else if(drawingTriangle){
            if(firstClick){
                inX = e.getX();
                inY = e.getY();
                shapes.push(new Triangle(inX, inY, penWidth, 0, 0, currentColor));
                firstClick = false;
            } else {
                int width = Math.abs(inX - e.getX());
                int height = inY - e.getY();
                Triangle triangle = (Triangle) shapes.peek();
                triangle.setWidth(width);
                triangle.setHeight(height);
            } 
        }
        else if(drawingStraightLine){
            if(firstClick){
                inX = e.getX();
                inY = e.getY();
                shapes.push(new StraightLine(inX, inY, penWidth, 0, 0, currentColor));
                firstClick = false;
            } else {
                int width = e.getX();
                int height = e.getY();
                StraightLine straightLine = (StraightLine) shapes.peek();
                straightLine.setWidth(width);
                straightLine.setHeight(height);
            } 
        }
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'mouseMoved'");
    }
}
