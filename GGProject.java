import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
/**
* GGChebanov class written for CS313 Project
* @author Oleksandr Chebanov
*/
public class GGProject extends JFrame {
    Toolkit tk = Toolkit.getDefaultToolkit();
    int x = ((int) tk.getScreenSize().getWidth());
    int y = ((int) tk.getScreenSize().getHeight());
    Graph graph;
    String remoteControl;
    DrawPicture picture;
    int enteredWeight;
    HashMap ans;

    public GGProject(){
        Menu menu = new Menu();
        graph = new Graph();
        picture = new DrawPicture();

        setTitle("Graph GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(picture, BorderLayout.CENTER);
        add(menu, BorderLayout.WEST);
        pack();
        setVisible(true);
    }
// Create a Menu Panel with all radiobuttons and buttons
    private class Menu extends JPanel{
        public Menu(){
            setLayout(new GridBagLayout());
            GridBagConstraints g = new GridBagConstraints();
            g.fill = GridBagConstraints.HORIZONTAL;

            RadioButton addVertex = new RadioButton("Add Vertex");
            g.gridx = 0;
            g.gridy = 0;
            g.gridwidth = 2;
            g.ipady = (int) y/12;
            add(addVertex, g);

            RadioButton addEdge = new RadioButton("Add Edge");
            g.gridy = 1;
            add(addEdge, g);

            RadioButton moveVertex = new RadioButton("Move Vertex");
            g.gridy = 2;
            add(moveVertex, g);

            RadioButton shortestPath = new RadioButton("Shortest Path");
            g.gridy = 3;
            add(shortestPath, g);

            RadioButton changeWeight = new RadioButton("Change a weight to:");
            g.gridy = 4;
            g.gridwidth = 1;
            add(changeWeight, g);

            ButtonGroup group = new ButtonGroup();
            group.add(addVertex);
            group.add(addEdge);
            group.add(moveVertex);
            group.add(shortestPath);
            group.add(changeWeight);

            JTextField textField = new JTextField();
            textField.setBounds(50,150, 200,30);
            g.gridx = 1;
            g.gridy = 4;
            g.ipadx = (int) x/12;
            add(textField, g);

            Button addButton = new Button("Add All Edges");
            g.gridx = 0;
            g.gridy = 5;
            g.gridwidth = 2;
            add(addButton, g);

            Button randomButton = new Button("Random Weights");
            g.gridy = 6;
            add(randomButton, g);

            Button treeButton = new Button("Minimal Spanning Tree");
            g.gridy = 7;
            add(treeButton, g);

            Button helpButton = new Button("Help");
            g.gridy = 8;
            add(helpButton, g);

            textField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                        String s = textField.getText();
                        enteredWeight = Integer.parseInt(s);
                }
            });
        }
    }
    //Create a button including action that occurs when user chooses this button
    private class Button extends JButton {
        public Button(String s1){
            setText(s1);
            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    remoteControl = s1;
                    switch (s1){
                        case "Add All Edges":
                            for (int i=0; i<graph.numberOfVertices()-1; i++){
                                for (int j=i+1; j<graph.numberOfVertices(); j++){
                                    graph.insertEdge(graph.listOfVertices.get(i),
                                        graph.listOfVertices.get(j));
                                }
                            }
                            picture.reDraw();
                            break;
                        case "Random Weights":
                            Random r = new Random(System.currentTimeMillis());
                            for (int i =0; i<graph.numberOfEdges(); i++){
                                int randomWeight = r.nextInt(2*graph.numberOfEdges()) + 1;
                                graph.listOfEdges.get(i).setWeight(randomWeight);
                            }
                            picture.reDraw();
                            break;
                        case "Minimal Spanning Tree":
                            ans = minimumSpanningTree(graph);
                            Iterator x = ans.keySet().iterator();
                            while(x.hasNext()){
                                Vertex u = (Vertex) x.next();
                                Edge v = (Edge) ans.get(u);
                                v.setColor("GREEN");
                            }
                            picture.reDraw();
                            break;
                        case "Help":
                            new HelpWindow();
                    }
                }
            });
        }
    }
    // Create new radiobutton
    private class RadioButton extends JRadioButton {
        public RadioButton(String s1){
            setText(s1);
            addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    remoteControl = s1;
                }
            });
        }
    }
    // Create a Help pop-up window
    private class HelpWindow extends JFrame {
        private JLabel helpLabel;
        private HelpWindow(){
            setTitle("Help");
            setSize(2*x/3, 2*y/3);
            setLayout(new FlowLayout(FlowLayout.LEFT));
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            helpLabel = new JLabel("<html> <h2> To create vertices: select " +
"the radio button that is called Add Vertex <br> and select position of " +
"vertex in the right half of the window by clicking the mouth. <br><br> To " +
"add edge, select the radio button that is called Add Edge <br> and click on " +
"the two vertices that specify edge's ends. <br><br> To add all possible " +
"edges among vertices, select the button Add All Edges. <br><br> To choose " +
"random weights for edges in a graph, select button Random Weights. <br><br>" +
"To change a weight to an edge, add a value for the weight in the text box," +
"<br> select radio button Change A Weight To,  <br> and click on the two " +
"vertices that specify edge's ends. <br><br> To define the shortest path, " +
"choose the radio button Shortest Path <br> and select two vertices. <br><br>" +
"To calculate the minimal spanning tree, <br> choose " +
"the button Minimal Spanning Tree. </h2> </html>", JLabel.LEFT);
            add(helpLabel);
            setVisible(true);
        }
    }
    // Create a Panel with vertices, edges, and weights
    private class DrawPicture extends JPanel{
        Vertex green = null;
        int index =0;
        public DrawPicture(){
            setPreferredSize(new Dimension(3*x/4, y));
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    for (int i = 0; i < graph.numberOfEdges(); i++){
                        graph.listOfEdges.get(i).setColor("BLUE");
                    }
                    repaint();
                    switch (remoteControl){
                        case "Add Vertex":
                            graph.insertVertex(e.getX() - 5, e.getY() - 5);
                            repaint();
                            break;
                        case "Add Edge":
                            int i = 0;
                            if (green == null) {
                                index = changeColor(e);
                            } else {
                                while(green != null && i<graph.numberOfVertices()){
                                    if (isClickOK(i, e.getX(), e.getY())){
                                        graph.insertEdge(graph.listOfVertices.get(index),
                                            graph.listOfVertices.get(i));
                                        repaint();
                                        green = null;
                                    }
                                    i++;
                                }
                            }
                            break;
                        case "Move Vertex":
                            i = 0;
                            if (green == null) {
                                index = changeColor(e);
                            } else {
                                while(green != null){
                                    graph.listOfVertices.get(index).setX(e.getX()-5);
                                    graph.listOfVertices.get(index).setY(e.getY()-5);
                                    repaint();
                                    green = null;
                                }
                            }
                            break;
                        case "Change a weight to:":
                            i = 0;
                            if (green == null) {
                                index = changeColor(e);
                            } else {
                                while(green != null && i<graph.numberOfVertices()){
                                    if (isClickOK(i, e.getX(), e.getY())){
                                        int k = graph.getEdge(graph.listOfVertices.get(index),
                                            graph.listOfVertices.get(i));
                                        if (k >= 0)
                                            graph.listOfEdges.get(k).setWeight(enteredWeight);
                                            repaint();
                                            green = null;
                                    }
                                    i++;
                                }
                            }
                            break;
                        case "Shortest Path":
                            i = 0;
                            if (green == null) {
                                index = changeColor(e);
                            } else {
                                while(green != null && i<graph.numberOfVertices()){
                                    if (isClickOK(i, e.getX(), e.getY())){
                                        ans = shortestPath(graph, graph.listOfVertices.get(index));
                                        while(i != index){
                                            Edge v = (Edge) ans.get(graph.listOfVertices.get(i));
                                            v.setColor("GREEN");
                                            if (v.getStartPoint().getPos() != i)
                                                i = v.getStartPoint().getPos();
                                            else
                                                i = v.getEndPoint().getPos();
                                        }
                                        repaint();
                                        green = null;
                                    }
                                    i++;
                                }
                            }
                            break;
                    }
                }
            });
        }
        // Check if the user choose an existing vertex
        public boolean isClickOK(int i, int c, int d){
            int a = graph.listOfVertices.get(i).getX();
            int b = graph.listOfVertices.get(i).getY();
            return ((a < c+45) && (a > c-55) && (b < d+45) && (b > d-55));
        }
        // Change color of the vertex from blue to green
        public int changeColor(MouseEvent e){
            int i = 0;
            while(green == null && i<graph.numberOfVertices()){
                if (isClickOK(i, e.getX(), e.getY())){
                    green = new Vertex(graph.listOfVertices.get(i).getX(), graph.listOfVertices.get(i).getY());
                    reDraw();
                    return i;
                  }
                  i++;
            }
            return -1;
        }
        // Create picture with all existing vertices, edges, and weights at this moment
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            paintVertices(g);
            paintEdges(g);
            paintWeights(g);
        }
        // Draw a vertex
        public void paintVertex(Graphics g, Vertex a){
          g.fillOval(a.getX(),a.getY(),10,10);
        }
        // Redraw picture
        public void reDraw(){
            repaint();
        }
        // Draw all existing vertices at this moment
        public void paintVertices(Graphics g){
            g.setColor(Color.RED);
            for (int j=0; j<graph.numberOfVertices(); j++){
                paintVertex(g, graph.listOfVertices.get(j));
            }
            if (green != null) {
                g.setColor(Color.GREEN);
                paintVertex(g, green);
            }
        }
        // Draw an Edge
        public void paintEdge(Graphics g, Edge a){
            if (a.getColor().equals("GREEN")){
                g.setColor(Color.GREEN);
            }
            else g.setColor(Color.BLUE);
            g.drawLine(a.getStartPoint().getX()+5, a.getStartPoint().getY()+5,
                a.getEndPoint().getX()+5, a.getEndPoint().getY()+5);
        }
        // Draw all existing edges at this moment
        public void paintEdges(Graphics g){
            for(int i = 0; i<graph.numberOfEdges(); i++){
                paintEdge(g, graph.listOfEdges.get(i));
            }
        }
        // Draw all existing weights of all existing edges at this moment
        public void paintWeights(Graphics g){
            g.setColor(Color.BLUE);
            for (int i=0; i < graph.numberOfEdges(); i++){
                if (graph.listOfEdges.get(i).getWeight() > 0)
                    paintWeight(g, i);
            }
        }
        // Draw the weight of an edge
        public void paintWeight(Graphics g, int i){
            String w = "" + graph.listOfEdges.get(i).getWeight();
            int xCoord = (graph.listOfEdges.get(i).getStartPoint().getX() +
                graph.listOfEdges.get(i).getEndPoint().getX())/2;
            int yCoord = (graph.listOfEdges.get(i).getStartPoint().getY() +
                graph.listOfEdges.get(i).getEndPoint().getY())/2;
            g.drawString(w, xCoord - 5, yCoord - 5);
        }

    }
    // Create a vertex
    private class Vertex {
        private int xCoordinate;
        private int yCoordinate;
        private int position;
        private LinkedList<Vertex> ongoing;
        public Vertex(int x1, int y1){
            xCoordinate = x1;
            yCoordinate = y1;
        }
        public Vertex(int x1, int y1, int n){
            xCoordinate = x1;
            yCoordinate = y1;
            position = n;
            ongoing = new LinkedList<Vertex>();
        }
        public int getX() {
            return xCoordinate;
        }
        public int getY() {
            return yCoordinate;
        }
        public int getPos() {
            return position;
        }
        public void setX(int x1){
            xCoordinate = x1;
        }
        public void setY(int y1){
            yCoordinate = y1;
        }
        public LinkedList<Vertex> getOngoing(){
            return ongoing;
        }
        public void addOngoing(Vertex a){
            ongoing.addLast(a);
        }
    }
    // Create an edge
    private class Edge {
        private Vertex startPoint;
        private Vertex endPoint;
        private int weight;
        private int position;
        private String color = "BLUE";
        public Edge(Vertex a, Vertex b) {
            startPoint = a;
            endPoint = b;
        }
        public Edge(Vertex a, Vertex b, int n) {
            startPoint = a;
            endPoint = b;
            a.addOngoing(b);
            b.addOngoing(a);
            position = n;
            weight = 0;
        }
        public Vertex getStartPoint() {
            return startPoint;
        }
        public Vertex getEndPoint() {
            return endPoint;
        }
        public int getPos() {
            return position;
        }
        public int getWeight() {
            return weight;
        }
        public void setWeight(int a){
            weight = a;
        }
        public String getColor(){
            return color;
        }
        public void setColor(String a){
            color = a;
        }
    }
    // Create a graph
    private class Graph {
        int numberOfVertices;
        int numberOfEdges;
        LinkedList<Vertex> listOfVertices;
        LinkedList<Edge> listOfEdges;
        public Graph(){
            numberOfVertices = 0;
            numberOfEdges = 0;
            listOfVertices = new LinkedList<Vertex>();
            listOfEdges = new LinkedList<Edge>();
        }
        public int numberOfVertices() {
            return numberOfVertices;
        }
        public int numberOfEdges() {
            return numberOfEdges;
        }
        public Vertex insertVertex(int a, int b){
            Vertex n = new Vertex(a, b, numberOfVertices++);
            listOfVertices.addLast(n);
            return n;
        }
        public Edge insertEdge(Vertex a, Vertex b) {
            if (a.getOngoing().contains(b) ||
                b.getOngoing().contains(a))
                    return null;
            Edge n = new Edge(a, b, numberOfEdges++);
            listOfEdges.addLast(n);
            return n;
        }
        public int getEdge(Vertex a, Vertex b) {
            for (int i=0; i< numberOfEdges; i++){
                if (listOfEdges.get(i).getStartPoint().equals(a) &&
                    listOfEdges.get(i).getEndPoint().equals(b))
                    return i;
                if (listOfEdges.get(i).getStartPoint().equals(b) &&
                    listOfEdges.get(i).getEndPoint().equals(a))
                    return i;
            }
            return -1;
        }
    }
    // Calculate the shortest path to the vertex
    private HashMap shortestPath(Graph g, Vertex src){
        HashMap<Vertex, Integer> d = new HashMap<Vertex, Integer>();
        HashMap<Vertex, Integer> cloud = new HashMap<Vertex, Integer>();
        PriorityQueue<Entry> pq = new PriorityQueue<Entry>();
        HashMap<Vertex, Entry> pqTokens = new HashMap<Vertex, Entry>();
        HashMap<Vertex, Edge> tree = new HashMap<Vertex, Edge>();

        for (int i=0; i<g.numberOfVertices(); i++) {
            if (g.listOfVertices.get(i) == src)
                d.put(g.listOfVertices.get(i), 0);
            else
                d.put(g.listOfVertices.get(i), Integer.MAX_VALUE);
            Entry n = new Entry(d.get(g.listOfVertices.get(i)), g.listOfVertices.get(i));
            pq.add(n);
            pqTokens.put(g.listOfVertices.get(i), n);
        }
        while(!pq.isEmpty()){
            Entry entry = pq.remove();
            int key = entry.getKey();
            Vertex u = entry.getValue();
            cloud.put(u, key);
            pqTokens.remove(u);
            for (int i = 0; i < u.getOngoing().size(); i++){
                Vertex v = u.getOngoing().get(i);
                if (cloud.get(v) == null) {
                    int wgt = g.listOfEdges.get(g.getEdge(u, v)).getWeight();
                    if (d.get(u) + wgt < d.get(v)) {
                        d.put(v, d.get(u) + wgt);
                        Entry n = pqTokens.get(v);
                        pq.remove(pqTokens.get(v));
                        n.setKey(d.get(v));
                        pq.add(n);
                    }
                }
            }
        }
        Iterator x = cloud.keySet().iterator();
        while(x.hasNext()){
            Vertex v = (Vertex) x.next();
            if (v != src)
                for (int i = 0; i < v.getOngoing().size(); i++){
                    Vertex u = v.getOngoing().get(i);
                    int wgt = g.listOfEdges.get(g.getEdge(u, v)).getWeight();
                    if (d.get(v) == d.get(u) + wgt){
                        tree.put(v, g.listOfEdges.get(g.getEdge(u, v)));
                    }
                }
        }
        return tree;
    }
    // Calculate the Minimum Spanning Tree of the graph
    private HashMap minimumSpanningTree(Graph g){
        HashMap<Vertex, Edge> tree = new HashMap<Vertex, Edge>();
        PriorityQueue<EntryEdge> pqc = new PriorityQueue<EntryEdge>();
        ArrayList<EntryEdge> pl = new ArrayList<EntryEdge>();
        for (int i = 0; i < g.numberOfEdges(); i++){
            EntryEdge n = new EntryEdge(g.listOfEdges.get(i).getWeight(), g.listOfEdges.get(i));
            pqc.add(n);
        }
        Iterator y = pqc.iterator();
        while(!pqc.isEmpty()){
            EntryEdge m = pqc.remove();
            pl.add(m);
        }
        tree.put(pl.get(0).getValue().getStartPoint(), pl.get(0).getValue());
        tree.put(pl.get(0).getValue().getEndPoint(), pl.get(0).getValue());
        pl.remove(0);
        while(!pl.isEmpty()){
            for(int i=0; i<pl.size(); i++){
                EntryEdge n = pl.get(i);
                if (tree.containsKey(n.getValue().getStartPoint()) &&
                    tree.containsKey(n.getValue().getEndPoint())) {
                        pl.remove(i);
                        break;
                } else if (tree.containsKey(n.getValue().getStartPoint())) {
                    tree.put(n.getValue().getEndPoint(), n.getValue());
                    pl.remove(i);
                    break;
                } else if (tree.containsKey(n.getValue().getEndPoint())) {
                    tree.put(n.getValue().getStartPoint(), n.getValue());
                    pl.remove(i);
                    break;
                }
            }
        }
        return tree;
    }
    // Create an entry that consists of vertex and key
    private class Entry implements Comparable<Entry>{
        private Vertex a;
        private Integer key;
        public Entry(Integer x, Vertex b){
            a = b;
            key = x;
        }
        public Vertex getValue(){
            return a;
        }
        public Integer getKey(){
            return key;
        }
        public void setKey(Integer x){
            key = x;
        }
        public int compareTo(Entry b){
            return key.compareTo(b.getKey());
        }
    }
    // Create an entry that consists of edge and key
    private class EntryEdge implements Comparable<EntryEdge>{
        private Edge a;
        private Integer key;
        public EntryEdge(Integer x, Edge b){
            a = b;
            key = x;
        }
        public Edge getValue(){
            return a;
        }
        public Integer getKey(){
            return key;
        }
        public void setKey(Integer x){
            key = x;
        }
        public int compareTo(EntryEdge b){
            return key.compareTo(b.getKey());
        }
    }

    public static void main(String args[]){
        try {
            UIManager.setLookAndFeel(UIManager.
                getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException |
            IllegalAccessException | UnsupportedLookAndFeelException e) {
        }
        new GGProject();
     }
}
