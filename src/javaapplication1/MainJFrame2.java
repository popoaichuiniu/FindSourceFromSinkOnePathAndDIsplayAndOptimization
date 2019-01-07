/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication1;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;

import org.gephi.preview.api.G2DTarget;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.api.RenderTarget;
import org.gephi.preview.types.DependantOriginalColor;
import org.gephi.preview.types.EdgeColor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author jacy
 */
public class MainJFrame2 extends javax.swing.JFrame {
    private String filePath;
    private String ultimatePath="you don't  choose two nodes to find a path!";
    
    ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
    private Workspace workspace;
    private ImportController importController;
    private ExportController ec;
     
     private PreviewController previewController;
     private GraphModel graphModel;
     private PreviewModel previewModel;
     
      private  Container container;
      private PreviewSketch previewSketch;
      

    private  int nodeCount=-1;
    private   int edgeCount=-1;
    private final float changeNodeSize=100.0f;
    
    
    
    
   private  Hashtable<Integer,List<Integer>> communities=new Hashtable<Integer,List<Integer>>();//存储社区和其节点    
   private int []belongCommunity;//节点i属于哪个社区   
   private  Hashtable<Integer,HashSet<Integer>>   neighborMap =new Hashtable<Integer,HashSet<Integer>>(); //存储社区的相邻社区
    
    
    
    
    
    
   
    /**
     * Creates new form MainJFrame2
     */
    public MainJFrame2() {
        initComponents();
        pathTextArea.setLineWrap(true);
        
    }
     private void getTestInformation(Graph graph) {

System.out.println("********************testDirected****************");
System.out.println("是否是有向图"+graph.isDirected());
System.out.println("是否是无向图"+graph.isUndirected());
System.out.println("是否是混合图"+graph.isMixed());
System.out.println("********************testDirected*****************");


System.out.println("********************节点个数，边的条数****************");
System.out.println("********************nodeCount**"+nodeCount+"**************");
System.out.println("********************edgeCount**"+edgeCount+"**************");
System.out.println("********************节点个数，边的条数****************");
    }
    private void displayGraph() {                                                   
      
         
    
         if(workspace!=null)
        {  container.closeLoader();
            pc.closeCurrentProject();
            pc.deleteWorkspace(workspace);
        }

   //Init a project - and therefore a workspace 
        pc.newProject();       
        workspace = pc.getCurrentWorkspace();
        //Get models and controllers for this new workspace - will be useful later      
        importController = Lookup.getDefault().lookup(ImportController.class);
        //filterController = Lookup.getDefault().lookup(FilterController.class);
        
         previewController = Lookup.getDefault().lookup(PreviewController.class);
         ec = Lookup.getDefault().lookup(ExportController.class);
         graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
         previewModel = Lookup.getDefault().lookup(PreviewController.class).getModel();
         
         
       
        System.out.println(filePath);
        if(filePath.indexOf(".gexf")!=-1)
        {
            processGexf(filePath);
        }
        else if(filePath.indexOf(".gml")!=-1)
        {
            processGml(filePath);
        }
        else
        {
            System.out.println("file choose error!");
            JOptionPane.showMessageDialog(null, "文件只能选择gml格式文件和gexf文件！"); 
        }  
       
       

    }                                  
    private void processGexf(String filePath)
   {  //Import file
       
       System.out.println("enter in gexf");
        try {
            File file = new File(filePath);
            container = importController.importFile(file);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, ex.toString(),"导入gexf文件出错,请检查格式！",JOptionPane.ERROR_MESSAGE);
            
           return;
        }
        
        
        //××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××
        
         System.out.println("布局算法开始"+new Date().toString());
       long initialSecondLayout=new GregorianCalendar().getTimeInMillis(); 
        DagLayout layout=new DagLayout(new DagLayoutBuilder());
        layout.setGraphModel(graphModel);
        layout.resetPropertiesValues();
       

        layout.initAlgo();//
      
       if(layout.canAlgo()) {
           layout.goAlgo();
       }
        layout.endAlgo(); 
   
    long overSecondLayout=new GregorianCalendar().getTimeInMillis(); 
    System.out.println("layout"+(overSecondLayout-initialSecondLayout));
    
   System.out.println("布局算法结束"+new Date().toString());
        
        
        
        
        
        
        //×××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××

        //Append imported data to GraphAPI
        importController.process(container, new DefaultProcessor(), workspace);

        //Preview configuration
     
      
        previewModel.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);
        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_COLOR, new DependantOriginalColor(Color.WHITE));
        previewModel.getProperties().putValue(PreviewProperty.EDGE_CURVED, Boolean.FALSE);
        previewModel.getProperties().putValue(PreviewProperty.EDGE_OPACITY, 50);
        previewModel.getProperties().putValue(PreviewProperty.EDGE_RADIUS, 10f);
        previewModel.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, Color.WHITE);       
        previewModel.getProperties().putValue(PreviewProperty.EDGE_THICKNESS, 5f);           
        previewModel.getProperties().putValue(PreviewProperty.NODE_OPACITY, 50);   
        previewModel.getProperties().putValue(PreviewProperty.EDGE_COLOR, new EdgeColor(EdgeColor.Mode.ORIGINAL));
          
      refreshView();
       
   }
   private  void  processGml(String filePath)
   { //Import file       
       System.out.println("enter in gml");
       
       long initialSecondImport=new GregorianCalendar().getTimeInMillis();        
        try {
            File file = new File(filePath);
             System.out.println("开始导入文件"+new Date().toString());
            container = importController.importFile(file);
           // container.getLoader().setEdgeDefault(EdgeDirectionDefault.UNDIRECTED);   //Force DIRECTED
        } catch (Exception ex) {
            ex.printStackTrace();
           JOptionPane.showMessageDialog(null, ex.toString(),"导入gml文件出错,请检查格式！",JOptionPane.ERROR_MESSAGE);
            return;
        }

        //Append imported data to GraphAPI
       
         
        importController.process(container, new DefaultProcessor(), workspace);
         long overSecondImport=new GregorianCalendar().getTimeInMillis();
        System.out.println("import"+(overSecondImport-initialSecondImport));
        
           System.out.println("文件导入结束"+new Date().toString());


   
    
     System.out.println("布局算法开始"+new Date().toString());
       long initialSecondLayout=new GregorianCalendar().getTimeInMillis(); 
        DagLayout layout=new DagLayout(new DagLayoutBuilder());
        layout.setGraphModel(graphModel);
        layout.resetPropertiesValues();
       

        layout.initAlgo();//
      
       if(layout.canAlgo()) {
           layout.goAlgo();
       }
        layout.endAlgo(); 
   
    long overSecondLayout=new GregorianCalendar().getTimeInMillis(); 
    System.out.println("layout"+(overSecondLayout-initialSecondLayout));
    
   System.out.println("布局算法结束"+new Date().toString());
    System.out.println("其他属性渲染开始"+new Date().toString());
   

        //Preview
       
        previewModel.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE); 
        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_FONT, previewModel.getProperties().getFontValue(PreviewProperty.NODE_LABEL_FONT).deriveFont(8));
        previewModel.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, Color.WHITE);
        previewModel.getProperties().putValue(PreviewProperty.EDGE_THICKNESS, 5f);
        previewModel.getProperties().putValue(PreviewProperty.EDGE_CURVED, Boolean.FALSE);
        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_COLOR, new DependantOriginalColor(Color.YELLOW));
        previewModel.getProperties().putValue(PreviewProperty.EDGE_COLOR, new EdgeColor(EdgeColor.Mode.ORIGINAL));
          System.out.println("其他属性渲染结束"+new Date().toString()); 
          long initialSecondDisplay=new GregorianCalendar().getTimeInMillis();  
        refreshView();
         long overSecondDisplay=new GregorianCalendar().getTimeInMillis();
          System.out.println("display"+(overSecondDisplay-initialSecondDisplay));
        System.out.print("");  
   }
   private void refreshView()
   {
        //New Processing target, get the PApplet
        if(previewSketch!=null)
        {
            graphPanel.removeAll();
            previewSketch=null;
            
        }
        G2DTarget target = (G2DTarget) previewController.getRenderTarget(RenderTarget.G2D_TARGET);
        while(target==null)
        {
            System.out.println("等待target.......");
        }
       
        previewSketch = new PreviewSketch(target);
        previewController.refreshPreview();
        previewSketch.resetZoom();
        graphPanel.setLayout(new BorderLayout());        
        graphPanel.add(previewSketch,BorderLayout.CENTER);
        graphPanel.validate();
        
   }
  
   
     
     

    class MyCompartor implements Comparator<String>
    {

        @Override
        public int compare(String o1, String o2) {
            String []temp1=o1.split("\\*");
            String[]temp2=o2.split("\\*");
            float num1=Float.parseFloat(temp1[0]);
            float num2=Float.parseFloat(temp2[0]);
            int   numI1=Integer.parseInt(temp1[1]);
             int   numI2=Integer.parseInt(temp2[1]);
               int   numJ1=Integer.parseInt(temp1[2]);
             int   numJ2=Integer.parseInt(temp2[2]);
             
            
            if(num1>num2)
                return 1;
            else if(num1<num2)
                return -1;
            else if(numI1>numI2)
                   return 1;
            else if(numI1<numI2)
                  return -1;
            else if(numJ1>numJ2)
                 return 1;
            else if(numJ1<numJ2)
                return -1;
              else
                return 0;
                
                
            
        }


    
        
    }
    private void cnm(Graph graph) throws RuntimeException, NumberFormatException {
        System.out.println("开始显示划分社区"+new Date().toString());
        long initialSecondD= new  GregorianCalendar().getTimeInMillis();
        /**************CNM******************/

        


int changeNodeCount=graph.getNodeCount();
System.out.println("********************changeNodeCount**"+changeNodeCount+"**************");


float Qmodularity [][]=new float[nodeCount+1][nodeCount+1];//保存deltQ

for(int i=1;i<nodeCount+1;i++)
{
    Arrays.fill(Qmodularity[i],0);
}

float aDegreeDTwoM []=new float[nodeCount+1];//保存ai
boolean isReach[][]=new boolean [nodeCount+1][nodeCount+1];

belongCommunity[0]=-1;


for(int i=1;i<nodeCount+1;i++)
{
    Arrays.fill(isReach[i],false);
}
final int edgeCountM=graph.getEdgeCount();
System.out.println("********************changeEdgeCount**"+edgeCountM+"**************");
final float halfDM=0.5f/edgeCountM;//1/(2*m)
final float twoMsquare=2*edgeCountM*edgeCountM;//2*（m^2）
TreeSet<String> [] rowSet=new TreeSet[nodeCount+1];//红黑树，插入和查找都是logN
MyCompartor myCompartor=new MyCompartor();
TreeSet<String> rowsMax=new TreeSet<String>(myCompartor);//存储每行最大的
rowSet[0]=null;
aDegreeDTwoM[0]=-1;

boolean []hasRow=new boolean[nodeCount+1];
boolean []hasColumn=new boolean[nodeCount+1];
Arrays.fill(hasRow,true);
hasRow[0]=false;
Arrays.fill(hasColumn,true);
hasColumn[0]=false;

String[] maxQSingleRow=new String[nodeCount+1];  //每一行最大的字符串
String[] previousRowMaxQString=new String[nodeCount+1];  //每一行修改之前的最大的字符串
maxQSingleRow[0]=null;
previousRowMaxQString[0]=null;



System.out.println("*****************Qmodularity[i][j]*****************");



for (int i=1;i<nodeCount+1;i++)
{
    belongCommunity[i]=i;
    rowSet[i]=new TreeSet<String>(myCompartor);
    aDegreeDTwoM[i]=(float)graph.getDegree(graph.getNode(String.valueOf(i)))/(2*edgeCountM);
    List<Integer> arr=new LinkedList<Integer>();
    arr.add(i);
    communities.put(i, arr);
    
}

for(Iterator<Edge> iteratorEdge =graph.getEdges().iterator();iteratorEdge.hasNext();)
{  Edge edgeTemp=iteratorEdge.next();
Node nodeSource=edgeTemp.getSource();
int i=Integer.parseInt((String)nodeSource.getId());
Node nodeTarget=edgeTemp.getTarget();
int j=Integer.parseInt((String)nodeTarget.getId());
System.out.println("边："+i+","+j);
Qmodularity[i][j]=halfDM-(float)(graph.getDegree(graph.getNode(String.valueOf(i)))*graph.getDegree(graph.getNode(String.valueOf(j))))/twoMsquare;
Qmodularity[j][i]=Qmodularity[i][j];
isReach[i][j]=true;
isReach[j][i]=true;
rowSet[i].add(Qmodularity[i][j]+"*"+i+"*"+j);
rowSet[j].add(Qmodularity[j][i]+"*"+j+"*"+i);
}




//    for (int i=1;i<nodeCount+1;i++)
//        {
//
//                    rowSet[i]=new TreeSet<String>(myCompartor);
//                    aDegreeDTwoM[i]=(float)graph.getDegree(graph.getNode(String.valueOf(i)))/(2*edgeCountM);
//                    List<Integer> arr=new LinkedList<Integer>();
//                     arr.add(i);
//                     communities.put(i, arr);
//
//                    for(int j=1;j<nodeCount+1;j++)
//                    {
//
//
//                           if(graph.isAdjacent(graph.getNode(String.valueOf(i)), graph.getNode(String.valueOf(j))))//如果i 和j是连接的,
//                              {
//                                  Qmodularity[i][j]=halfDM-(float)(graph.getDegree(graph.getNode(String.valueOf(i)))*graph.getDegree(graph.getNode(String.valueOf(j))))/twoMsquare;
//
//                                  isReach[i][j]=true;
//
//                              }
//                          else
//                              {
//                                 Qmodularity[i][j]=0;
//                                 isReach[i][j]=false;
//                              }
//
//
//                           rowSet[i].add(Qmodularity[i][j]+"*"+i+"*"+j);
//
//
//
//                         System.out.print( Qmodularity[i][j]+"              ");
//
//
//
//
//                    }
//
//                    System.out.println("");
//
//
//    }
System.out.println("*****************Qmodularity[i][j]*****************");

System.out.println("**************rowSet[i].last()********************");
for (int i=1;i<nodeCount+1;i++)
{
    if(rowSet[i].size()!=0)
        maxQSingleRow[i]=rowSet[i].last();
    else
        maxQSingleRow[i]="0*"+i+"*0";
    
    rowsMax.add(maxQSingleRow[i]);
    System.out.print(maxQSingleRow[i]+"   ");
    
    
}
System.out.println("");     System.out.println("**************rowSet[i].last()********************");

//

String maxStringQ=rowsMax.pollLast();
String  splitString []=maxStringQ.split("\\*");
int maxI=-1;
int maxJ=-1;
float maxQ=-1;
if(splitString.length!=3)
{
    throw new RuntimeException("代码出现错误");
}
else
{
    maxI=Integer.parseInt(splitString[1]);
    maxJ=Integer.parseInt(splitString[2]);
    maxQ=Float.parseFloat(splitString[0]);
}
while(maxQ>0)
{ rowSet[maxI]=null;//去掉第maxI行
// rowsMax.remove(maxQSingleRow[maxI]);不需要这个，因为将最大值取出时，就拿走了第i行的值
maxQSingleRow[maxI]=null;//去掉第maxI行的最大值

previousRowMaxQString[maxI]=null;//
hasRow[maxI]=false;
hasColumn[maxI]=false;

for (int i=1;i<nodeCount+1;i++)//将社区maxI和社区maxJ融合 ，新的社区以maxJ代替,更新矩阵
{
    
    
    if(rowSet[i]!=null)//等价于 if(hasRow[i])
    {  previousRowMaxQString[i]=maxQSingleRow[i];//
    rowSet[i].remove( Qmodularity[i][maxI]+"*"+i+"*"+maxI);//去掉第i列
    if(i!=maxJ)
    {       //加入重新修改后的值
        if(isReach[i][maxI]&&isReach[i][maxJ])//i 和 maxI，maxJ都连接
        { rowSet[i].remove( Qmodularity[i][maxJ]+"*"+i+"*"+maxJ);//去掉第j列的值，然后加入重新修改
        rowSet[maxJ].remove(Qmodularity[maxJ][i]+"*"+maxJ+"*"+i);
        Qmodularity[maxJ][i]=Qmodularity[maxI][i]+Qmodularity[maxJ][i];//
        Qmodularity[i][maxJ]=Qmodularity[maxJ][i];
        rowSet[i].add(Qmodularity[i][maxJ]+"*"+i+"*"+maxJ);
        rowSet[maxJ].add(Qmodularity[maxJ][i]+"*"+maxJ+"*"+i);
        
        }
        else if(isReach[i][maxI]&&(!isReach[i][maxJ]))//i 和 maxI连接但不和maxJ连接
        {     rowSet[i].remove( Qmodularity[i][maxJ]+"*"+i+"*"+maxJ);//去掉第j列的值，然后加入重新修改
        rowSet[maxJ].remove(Qmodularity[maxJ][i]+"*"+maxJ+"*"+i);
        Qmodularity[maxJ][i]=Qmodularity[maxI][i]-2*aDegreeDTwoM[maxJ]*aDegreeDTwoM[i];//
        Qmodularity[i][maxJ]=Qmodularity[maxJ][i];
        rowSet[i].add(Qmodularity[i][maxJ]+"*"+i+"*"+maxJ);
        rowSet[maxJ].add(Qmodularity[maxJ][i]+"*"+maxJ+"*"+i);
        isReach[i][maxJ]=true;
        isReach[maxJ][i]=true;
        }
        else if((!isReach[i][maxI])&&isReach[i][maxJ])//i 和 maxJ连接但不和maxI连接
        {     rowSet[i].remove( Qmodularity[i][maxJ]+"*"+i+"*"+maxJ);//去掉第j列的值，然后加入重新修改
        rowSet[maxJ].remove(Qmodularity[maxJ][i]+"*"+maxJ+"*"+i);
        Qmodularity[maxJ][i]=Qmodularity[maxJ][i]-2*aDegreeDTwoM[maxI]*aDegreeDTwoM[i];//
        Qmodularity[i][maxJ]=Qmodularity[maxJ][i];
        rowSet[i].add(Qmodularity[i][maxJ]+"*"+i+"*"+maxJ);
        rowSet[maxJ].add(Qmodularity[maxJ][i]+"*"+maxJ+"*"+i);
        
        }
        
        
        else//i和两者都不连接,没有任何增益，不做任何操作
        {         //i和maxI ，maxJ不连接的话，Qmodularity[i][maxJ]，Qmodularity[maxJ][i]之前都是0
        }
    }
    if(rowSet[i].size()!=0)
        maxQSingleRow[i]=rowSet[i].last();//更新maxQSingleRow
    else
    {
        maxQSingleRow[i]="0*"+i+"*0";
    }
    
    
    
    
    
    
    
    }
    
    
    
}



for (int i=1;i<nodeCount+1;i++)
{
    
    
    if(rowSet[i]!=null)
    {
        
        if(!previousRowMaxQString[i].equals(maxQSingleRow[i]))//如果两社区融合之后，第i行的最大值发生变化，则更新rowsMax
        {
            
            
            
            rowsMax.remove(previousRowMaxQString[i]);
            rowsMax.add(maxQSingleRow[i]);
            
        }
        
    }
    
    
    
    
}

aDegreeDTwoM[maxJ]=aDegreeDTwoM[maxI]+aDegreeDTwoM[maxJ];//要在for循环之后，保证Qmodularity[maxJ][i]计算正确
aDegreeDTwoM[maxI]=-1;

List<Integer> tempArr=communities.get(maxJ);
List<Integer> tempArrMaxI=communities.get(maxI);
for(Iterator<Integer> iterator=tempArrMaxI.iterator();iterator.hasNext();)
{
    int i=iterator.next();
    belongCommunity[i]=maxJ;
}
tempArr.addAll(tempArrMaxI);
communities.put(maxJ, tempArr);

communities.remove(maxI);

maxStringQ=rowsMax.pollLast();
splitString=maxStringQ.split("\\*");
if(splitString.length!=3)
{
    throw new RuntimeException("代码出现错误");
}
else
{
    maxI=Integer.parseInt(splitString[1]);
    maxJ=Integer.parseInt(splitString[2]);
    maxQ=Float.parseFloat(splitString[0]);
    
}



}




int colorRGB=150;
System.out.println("********************communities.size()    "+communities.size()+"**********************");
int interval=(16777215-colorRGB)/(communities.size());

Set<Map.Entry<Integer,List<Integer>>> entry=communities.entrySet();

for(Iterator<Map.Entry<Integer, List<Integer>>> iterator =entry.iterator();iterator.hasNext();)//每个社区
{
    Map.Entry<Integer, List<Integer>> item= iterator.next();
    
    List<Integer> list=item.getValue();
    int communityID=item.getKey();
   
    HashSet<Integer> neighbors=new HashSet<Integer>();//社区的相邻社区
    System.out.println(communityID+"    "+"**********社区大小************"+list.size()+"社区颜色RGB*************"+colorRGB);
    for(Iterator<Integer> iteratorList=list.iterator();iteratorList.hasNext();)//社区中的每个点
    {   int cnodeTempInt=iteratorList.next();
    Node nodeTemp=graph.getNode(String.valueOf(cnodeTempInt));
    nodeTemp.setColor(new Color(colorRGB));
    System.out.println(cnodeTempInt+" ");
   
    for(Iterator<Node> iteratorBorder=graph.getNeighbors(nodeTemp).iterator();iteratorBorder.hasNext();)//每个点的邻居
    {
        Node neighborNode=iteratorBorder.next();
        int belongCommunity=this.belongCommunity[Integer.parseInt((String)neighborNode.getId())];
        if(belongCommunity!=communityID)
        {   
        neighbors.add(belongCommunity);
        }
    }
  
    
    }   
   
    neighborMap.put(communityID,neighbors);
    colorRGB=colorRGB+interval;
    
}

long overSecondD= new  GregorianCalendar().getTimeInMillis();
System.out.println("划分算法处理时间"+((overSecondD-initialSecondD)/1000));
refreshView();
    }   


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        graphPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        sourceTextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        targetTextField = new javax.swing.JTextField();
        filterButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        pathTextArea = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        file = new javax.swing.JMenu();
        openFile = new javax.swing.JMenuItem();
        save = new javax.swing.JMenuItem();
        exit = new javax.swing.JMenuItem();
        edit = new javax.swing.JMenu();
        nodeMenuItem = new javax.swing.JMenuItem();
        edgeMenuItem = new javax.swing.JMenuItem();
        exactlyNodeModify = new javax.swing.JMenuItem();
        exactlyEdgeModify = new javax.swing.JMenuItem();
        modifyBackgroundColor = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        HelpMenuItem = new javax.swing.JMenuItem();
        AboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(MainJFrame2.class, "MainJFrame2.title")); // NOI18N

        graphPanel.setBackground(new java.awt.Color(204, 204, 255));

        javax.swing.GroupLayout graphPanelLayout = new javax.swing.GroupLayout(graphPanel);
        graphPanel.setLayout(graphPanelLayout);
        graphPanelLayout.setHorizontalGroup(
            graphPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        graphPanelLayout.setVerticalGroup(
            graphPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel3.setBackground(new java.awt.Color(204, 204, 255));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(MainJFrame2.class, "MainJFrame2.jLabel3.text")); // NOI18N

        sourceTextField.setText(org.openide.util.NbBundle.getMessage(MainJFrame2.class, "MainJFrame2.sourceTextField.text")); // NOI18N
        sourceTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                sourceTextFieldKeyPressed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(MainJFrame2.class, "MainJFrame2.jLabel4.text")); // NOI18N

        targetTextField.setText(org.openide.util.NbBundle.getMessage(MainJFrame2.class, "MainJFrame2.targetTextField.text")); // NOI18N
        targetTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                targetTextFieldKeyPressed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(filterButton, org.openide.util.NbBundle.getMessage(MainJFrame2.class, "MainJFrame2.filterButton.text")); // NOI18N
        filterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterButtonActionPerformed(evt);
            }
        });

        pathTextArea.setColumns(20);
        pathTextArea.setRows(5);
        jScrollPane1.setViewportView(pathTextArea);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(MainJFrame2.class, "MainJFrame2.jLabel5.text")); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(jLabel5))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(181, 181, 181)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sourceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(targetTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3))))
                .addContainerGap(215, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(filterButton, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(54, 54, 54))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(101, 101, 101)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addComponent(sourceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42)
                .addComponent(jLabel4)
                .addGap(36, 36, 36)
                .addComponent(targetTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42)
                .addComponent(filterButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(graphPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(graphPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(MainJFrame2.class, "MainJFrame2.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(MainJFrame2.class, "MainJFrame2.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(file, org.openide.util.NbBundle.getMessage(MainJFrame2.class, "MainJFrame2.file.text")); // NOI18N

        openFile.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/fileOpen.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(openFile, org.openide.util.NbBundle.getMessage(MainJFrame2.class, "MainJFrame2.openFile.text")); // NOI18N
        openFile.setActionCommand(org.openide.util.NbBundle.getMessage(MainJFrame2.class, "MainJFrame2.openFile.actionCommand")); // NOI18N
        openFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openFileActionPerformed(evt);
            }
        });
        file.add(openFile);

        save.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        save.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/saveGraph.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(save, org.openide.util.NbBundle.getMessage(MainJFrame2.class, "MainJFrame2.save.text")); // NOI18N
        save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveActionPerformed(evt);
            }
        });
        file.add(save);

        exit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        exit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/exit.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(exit, org.openide.util.NbBundle.getMessage(MainJFrame2.class, "MainJFrame2.exit.text")); // NOI18N
        exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitActionPerformed(evt);
            }
        });
        file.add(exit);

        jMenuBar1.add(file);

        org.openide.awt.Mnemonics.setLocalizedText(edit, org.openide.util.NbBundle.getMessage(MainJFrame2.class, "MainJFrame2.edit.text")); // NOI18N

        nodeMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        nodeMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/allNode.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(nodeMenuItem, org.openide.util.NbBundle.getMessage(MainJFrame2.class, "MainJFrame2.nodeMenuItem.text")); // NOI18N
        nodeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nodeMenuItemActionPerformed(evt);
            }
        });
        edit.add(nodeMenuItem);

        edgeMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        edgeMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/allEdge.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(edgeMenuItem, org.openide.util.NbBundle.getMessage(MainJFrame2.class, "MainJFrame2.edgeMenuItem.text")); // NOI18N
        edgeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edgeMenuItemActionPerformed(evt);
            }
        });
        edit.add(edgeMenuItem);

        exactlyNodeModify.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        exactlyNodeModify.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/point.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(exactlyNodeModify, org.openide.util.NbBundle.getMessage(MainJFrame2.class, "MainJFrame2.exactlyNodeModify.text")); // NOI18N
        exactlyNodeModify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exactlyNodeModifyActionPerformed(evt);
            }
        });
        edit.add(exactlyNodeModify);

        exactlyEdgeModify.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        exactlyEdgeModify.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/edge.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(exactlyEdgeModify, org.openide.util.NbBundle.getMessage(MainJFrame2.class, "MainJFrame2.exactlyEdgeModify.text")); // NOI18N
        exactlyEdgeModify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exactlyEdgeModifyActionPerformed(evt);
            }
        });
        edit.add(exactlyEdgeModify);

        modifyBackgroundColor.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
        modifyBackgroundColor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/backgroundColor.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(modifyBackgroundColor, org.openide.util.NbBundle.getMessage(MainJFrame2.class, "MainJFrame2.modifyBackgroundColor.text")); // NOI18N
        modifyBackgroundColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modifyBackgroundColorActionPerformed(evt);
            }
        });
        edit.add(modifyBackgroundColor);

        jMenuBar1.add(edit);

        org.openide.awt.Mnemonics.setLocalizedText(jMenu1, org.openide.util.NbBundle.getMessage(MainJFrame2.class, "MainJFrame2.jMenu1.text")); // NOI18N

        HelpMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
        HelpMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/help.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(HelpMenuItem, org.openide.util.NbBundle.getMessage(MainJFrame2.class, "MainJFrame2.HelpMenuItem.text")); // NOI18N
        HelpMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HelpMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(HelpMenuItem);

        AboutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        AboutMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/version.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(AboutMenuItem, org.openide.util.NbBundle.getMessage(MainJFrame2.class, "MainJFrame2.AboutMenuItem.text")); // NOI18N
        AboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AboutMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(AboutMenuItem);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 991, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(456, 456, 456))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void filterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterButtonActionPerformed
        // TODO add your handling code here:
     
          
        
        
        //初始化
       ultimatePath="";      
       pathTextArea.setText("");     
       String sourceID=sourceTextField.getText().trim();
        String targetID=targetTextField.getText().trim();
        
     if(sourceID.equals("")||targetID.equals(""))   
     {
          JOptionPane.showMessageDialog(null, "您未填入源节点或者目的节点");  
             return;
     }
     int sourceIDInt= Integer.parseInt(sourceID);
      int targetIDInt=  Integer.parseInt(targetID);   
         if(graphModel==null)
         {
             JOptionPane.showMessageDialog(null, "您未选择文件");  
             return;
         }
       // Graph graph=graphModel.getUndirectedGraph();//有向图中2到5有边，5到2有边。执行这个之后这2条边记为一条边，但是isAdjacent这个方法是不会改变的，即2到5有边。5到2没边，则返回true和false;
       Graph graph=graphModel.getGraph();
       DirectedGraph diGraph=graphModel.getDirectedGraph();
       
       Node sourceNode=graph.getNode(sourceID);
        Node targetNode=graph.getNode(targetID);     
       if(sourceNode==null||targetNode==null)
       {  JOptionPane.showMessageDialog(null, "源节点和目的节点不存在！");
           return;
       }
        /*设置源节点和目的节点的颜色、大小、alpha*/
        targetNode.setColor(Color.RED); 
        targetNode.setSize( changeNodeSize);
       
       
        sourceNode.setColor(Color.RED);
        sourceNode.setSize(changeNodeSize);
      
         long initialSearchIndex=new GregorianCalendar().getTimeInMillis();
        /*如果源节点和目的节点不属于一个弱连通图*/
         if(belongWeakLyConnectedSubgraph[sourceIDInt]!=belongWeakLyConnectedSubgraph[targetIDInt])
         {
              System.out.println("源节点和目的节点不属于一个弱连通图");
              pathTextArea.setText("没有路径！");
              ultimatePath=sourceID+" can't reach "+targetID;
         } 
         else
         {StronglyConnectedComponent sourceScc=stronglyConnectedMap.
                                              get(belongStronglyConnectedSubgraph[sourceIDInt]);
         StronglyConnectedComponent targetScc=stronglyConnectedMap.
                                              get(belongStronglyConnectedSubgraph[targetIDInt]);         
            
         getSccPath(sourceScc, targetScc);
          if(sccPath==null)
          {ultimatePath=sourceID+" can't reach "+targetID;  
           pathTextArea.setText("没有路径！");
           long overSearchIndex=new GregorianCalendar().getTimeInMillis();
           System.out.println("SearchIndex"+(overSearchIndex-initialSearchIndex));
          }        
         else
            {          
                   
                 getNodePath(sourceIDInt, targetIDInt, diGraph);                 
                
                  System.out.println("***************************path***********************");                 
                 for(int i=0;i<nodePath.size();i++)
                 {  
                     System.out.print(nodePath.get(i)+"   ");
                     Node node=graph.getNode(String.valueOf(nodePath.get(i)));
                     node.setSize(changeNodeSize);
                     node.setColor(Color.RED);
                     //node.setAlpha(0.5f);
                     
                 }      
                long overSearchIndex=new GregorianCalendar().getTimeInMillis();
                System.out.println("SearchIndex"+(overSearchIndex-initialSearchIndex));
                targetNode.setSize( changeNodeSize+80);
             // targetNode.setAlpha(0.5f);         
                sourceNode.setSize(changeNodeSize+80);
            //  sourceNode.setAlpha(0.5f);
                  
                  for(int i=0;i<nodePath.size()-1;i=i+1)
                 {
                    ultimatePath=ultimatePath+nodePath.get(i)+"--->";
                    Node node1=graph.getNode(String.valueOf(nodePath.get(i)));
                    Node node2=graph.getNode(String.valueOf(nodePath.get(i+1)));
                    Edge edge=graph.getEdge(node1, node2);                  
                     edge.setAlpha(0.95f);
                     edge.setColor(new Color(0xFF0000));
                    edge.setWeight(8);
                   
                    
                     
                 }
                  ultimatePath=ultimatePath+nodePath.get(nodePath.size()-1);
                  pathTextArea.setText(ultimatePath);
                  System.out.println("");
                  System.out.println("***************************path***********************");
              
            }   
             
         }
        
           
//         /*普通方法*/
//      long initialSearchCommon=new GregorianCalendar().getTimeInMillis();
//      getPathForce(diGraph,sourceIDInt,targetIDInt); 
//      long overSearchCommon=new GregorianCalendar().getTimeInMillis();
//       
//      System.out.println("SearchCommon"+(overSearchCommon-initialSearchCommon));
      
      refreshView();
     JOptionPane.showMessageDialog(null, "查找结束！"); 
    
    }//GEN-LAST:event_filterButtonActionPerformed
    
    private  boolean  [] enterInStackForce;
    private  ArrayList<Integer> pathListForce;
    private void depthForce(int nodeIDInt,int targetIDInt ,Stack<Integer>  stack,DirectedGraph graph)
    {   if(pathListForce==null)
            {
                if(!enterInStackForce[nodeIDInt])
                { stack.push(nodeIDInt);
                  enterInStackForce[nodeIDInt]=true;
                 if(nodeIDInt==targetIDInt)
                 {    
                     pathListForce=new ArrayList<Integer>(stack);
                     return;
                    

                 }
                 else
                 {   Node nodeID=graph.getNode(String.valueOf(nodeIDInt));
                      for(Iterator<Node> iterator=graph.getSuccessors(nodeID).iterator();iterator.hasNext();)
                     {   
                         Node nodeTemp=iterator.next();
                          int nodeTempIDInt=Integer.parseInt((String)nodeTemp.getId());
                          if(!whethernoPathNodeForce[nodeTempIDInt])
                          depthForce(nodeTempIDInt,targetIDInt,stack,graph);
                     }
                      if(pathListForce==null)
                      whethernoPathNodeForce[nodeIDInt]=true;

                }
                 stack.pop();
                 enterInStackForce[nodeIDInt]=false;

                }
            }
        
     
    
        
    }
    private boolean [] whethernoPathNodeForce;
    private void getPathForce(DirectedGraph graph ,int sourceIDInt,int targetIDInt) {
        Stack stack=new Stack<Integer>();
        enterInStackForce=new boolean[nodeCount+1];        
        Arrays.fill(enterInStackForce, false);
        pathListForce=null;
        whethernoPathNodeForce=new boolean[nodeCount+1];        
         Arrays.fill(whethernoPathNodeForce, false);
        System.out.println("开始执行寻找时间:            "+new Date().toLocaleString());
        depthForce(sourceIDInt,targetIDInt,stack,graph);        
         System.out.println("执行结束寻找时间:            "+new Date().toLocaleString());
       
        
               
       if(pathListForce==null)
       {
           System.out.println("没有路径到达");
            JOptionPane.showMessageDialog(null, "源节点和目的节点不存在路径！"); 
              pathTextArea.setText("没有路径！");
       }
       else
       {
             for(Iterator<Integer> iterator=pathListForce.iterator();iterator.hasNext();)
                            {   int pathNodeInt=iterator.next();                            
                              Node pathNode=   graph.getNode(String.valueOf(pathNodeInt));
                              pathNode.setColor(Color.RED);
                               pathNode.setSize(changeNodeSize);                            
                                System.out.print(pathNodeInt+" ");      

                            }
                     System.out.println("");
       }
       
       
    }
    /*获取源节点和目的节点所在强连通分量之间的路径，存储在sccPath中*/
   private  ArrayList<Integer> sccPath;  
   private boolean  whetherNoSccPathNode [];
    private void getSccPath(StronglyConnectedComponent sourceScc,StronglyConnectedComponent targetScc)
    {
        Stack<Integer>  stackSccPath=new Stack<Integer>();
        whetherNoSccPathNode=new boolean[nodeCount+1];
        Arrays.fill(whetherNoSccPathNode,false);
        sccPath=null;
         long initialGetSccPath=new GregorianCalendar().getTimeInMillis();
        sccProcess(sourceScc, targetScc, stackSccPath); 
        long overGetSccPath=new GregorianCalendar().getTimeInMillis();
       
         System.out.println("GetSccPath***************"+(overGetSccPath-initialGetSccPath));
        if( sccPath==null)
            return;
//         /*验证sccPath*/
//                System.out.println("******************验证sccPath*****************"); 
//              for(int i=0;i<sccPath.size();i++)
//              {
//                  System.out.print(sccPath.get(i)+" ");
//              }
//                System.out.println("");
//                 System.out.println("******************验证sccPath*****************"); 
            
        
    }
    private void sccProcess(StronglyConnectedComponent sourceScc,StronglyConnectedComponent targetScc, Stack<Integer> stackSccPath) {
        if(sccPath==null) 
        {
          stackSccPath.push(sourceScc.getLabel());//不用判断已经走过的路径是否已包含sourceScc，因为是有向无环图
         if(sourceScc.getLayer()==targetScc.getLayer())
         {
             if(sourceScc.getLabel()!=targetScc.getLabel())
             {   stackSccPath.pop();
                 return;
             }
             else
             {sccPath=new ArrayList<Integer>(stackSccPath);              
              return;
             }
         }
         if(sourceScc.getLayer()>targetScc.getLayer())
         {    stackSccPath.pop();
             return;
         
         }
         
        if(sourceScc.getLayer()<targetScc.getLayer())
            {for(Iterator<StronglyConnectedComponent> iteratorScc=sourceScc.getOutSccs().iterator();iteratorScc.hasNext();)
                    {StronglyConnectedComponent sccTemp= iteratorScc.next();
                              if(!whetherNoSccPathNode[sccTemp.getLabel()])
                              sccProcess(sccTemp, targetScc,stackSccPath);


                    }
            if(sccPath==null)
            whetherNoSccPathNode[sourceScc.getLabel()]=true;
            }
           
        }
        
        
       
        
        
        
        
        
     

    }
    private boolean flagNodeOver;
    private int currentNodeIDInt;
    private Stack<Integer> stackNode;
    private boolean whetherInStackNode [];
    private void nodeProcess(int sourceIDInt,int currentScc,int targetScc ,DirectedGraph graph )
    {    if(!flagNodeOver)
            {   stackNode.push(sourceIDInt);
                whetherInStackNode[sourceIDInt]=true;
                if(belongStronglyConnectedSubgraph[sourceIDInt]!=targetScc)
                {  if(belongStronglyConnectedSubgraph[sourceIDInt]==currentScc)
                        {
                            Node sourceNode=graph.getNode(String.valueOf(sourceIDInt));     
                           for(Iterator<Node> iterator=graph.getSuccessors(sourceNode).iterator();iterator.hasNext();)
                           {Node nodeTemp=iterator.next();
                            int nodeTempIDInt=Integer.valueOf((String)nodeTemp.getId());
                                 if((! whetherInStackNode[nodeTempIDInt])&&(!whetherNoNodePathNode[nodeTempIDInt]))
                                nodeProcess(nodeTempIDInt, currentScc, targetScc, graph);

                           }
                           if(!flagNodeOver)
                           {
                               whetherNoNodePathNode[sourceIDInt]=true;
                           }
                           
                        }
                
                    stackNode.pop();
                    whetherInStackNode[sourceIDInt]=false;                 
                    
                        
                    
                }
                else
                {currentNodeIDInt=sourceIDInt;
                    flagNodeOver=true;
                 stackNode.pop();//去掉最后一个节点
                 nodePath.addAll(stackNode);
                }
                
                  
            }
                
            
    
        
    }
    
    
    private Stack<Integer> stackLastNode;
    private boolean flagLastNodeOver;
    private boolean whetherInStackLastNode [];
    private void lastNodeProcess(int sourceIDInt,int currentScc,int targetIDInt ,DirectedGraph graph )
    {    if(!flagLastNodeOver)
            {   stackLastNode.push(sourceIDInt);
                whetherInStackLastNode[sourceIDInt]=true;
                if(sourceIDInt!=targetIDInt)
                {  if(belongStronglyConnectedSubgraph[sourceIDInt]==currentScc)
                        {
                            Node sourceNode=graph.getNode(String.valueOf(sourceIDInt));     
                           for(Iterator<Node> iterator=graph.getSuccessors(sourceNode).iterator();iterator.hasNext();)
                           {Node nodeTemp=iterator.next();
                            int nodeTempIDInt=Integer.valueOf((String)nodeTemp.getId());
                                 if((! whetherInStackLastNode[nodeTempIDInt])&&(! whetherNoNodePathNode[nodeTempIDInt]))
                                lastNodeProcess(nodeTempIDInt, currentScc, targetIDInt, graph);

                           }
                           if(!flagLastNodeOver)
                               whetherNoNodePathNode[sourceIDInt]=true;
                        }
                
                   stackLastNode.pop();
                     whetherInStackLastNode[sourceIDInt]=false;                 
                    
                        
                    
                }
                else
                {
                  flagLastNodeOver=true;                
                 nodePath.addAll(stackLastNode);
                }
                
                  
            }
                
            
    
        
    }
    /*获取源节点到目的节点之间的路径*/
    private  ArrayList<Integer> nodePath; 
    private  boolean  whetherNoNodePathNode [];
    private void getNodePath(int sourceIDInt,int targetIDInt ,DirectedGraph graph) 
    {    
        currentNodeIDInt=sourceIDInt;
        stackNode=new Stack<Integer>();
         whetherInStackNode=new boolean[nodeCount+1];       
         whetherNoNodePathNode= new boolean[nodeCount+1];         
         nodePath=new ArrayList<Integer>();
         
          Arrays.fill(whetherNoNodePathNode, false);
            Arrays.fill(whetherInStackNode, false);
            
            int currentSccPathIndex=1;
        while(belongStronglyConnectedSubgraph[currentNodeIDInt]!=belongStronglyConnectedSubgraph[targetIDInt])
        {   flagNodeOver=false;
             System.out.print("currentSccPathIndex:"+currentSccPathIndex+"   ");
            nodeProcess(currentNodeIDInt, belongStronglyConnectedSubgraph[currentNodeIDInt], sccPath.get(currentSccPathIndex++), graph);
           
        }
        System.out.println("");
        flagLastNodeOver=false;
        stackLastNode=new Stack<Integer>();
         whetherInStackLastNode=new boolean[nodeCount+1];
         
         Arrays.fill(whetherInStackLastNode, false);
         
        lastNodeProcess(currentNodeIDInt, belongStronglyConnectedSubgraph[currentNodeIDInt], targetIDInt, graph);
    } 
    
    private int belongWeakLyConnectedSubgraph[];
    private  boolean [] whetherVisitedWeaklyConnect;
     private  HashMap<Integer,ArrayList<Integer>> weaklyConnectedMap;
    private void  weaklyConnect(Graph graph,int nodeID,int i,ArrayList arr)
    {
        whetherVisitedWeaklyConnect[nodeID]=true;
        belongWeakLyConnectedSubgraph[nodeID]=i;
        arr.add(nodeID);
        Node node=graph.getNode(String.valueOf(nodeID));
        for(Iterator<Node> iterator=graph.getNeighbors(node).iterator();iterator.hasNext();)
        {
  ;
          int nodeTempID=Integer.parseInt((String)iterator.next().getId());
          if(!whetherVisitedWeaklyConnect[nodeTempID])
            {
                weaklyConnect(graph, nodeTempID,i,arr);
            }
        }
        
    }
     private void splitWccs(Graph graph) {
      
        
        /*弱连通图的划分*/
        belongWeakLyConnectedSubgraph=new int[nodeCount+1];
        whetherVisitedWeaklyConnect=new boolean[nodeCount+1];
        Arrays.fill(whetherVisitedWeaklyConnect, false);
        weaklyConnectedMap=new HashMap<Integer,ArrayList<Integer>>();
        
        for(int i=1;i<=nodeCount;i++)
        { if(!whetherVisitedWeaklyConnect[i])
            {  ArrayList<Integer> arr=new ArrayList<Integer>();
            weaklyConnect(graph, i,i,arr);
            weaklyConnectedMap.put(i, arr);
            }
        
        
        }
//        /*验证弱连通图*/
//        System.out.println("**************************验证弱连通图********************");
//        for(Iterator<Map.Entry<Integer,ArrayList<Integer>>> iteratorWcc=weaklyConnectedMap.entrySet().iterator();iteratorWcc.hasNext();)
//        {
//            Map.Entry<Integer,ArrayList<Integer>> itEntry=iteratorWcc.next();
//            int label=itEntry.getKey();
//            ArrayList<Integer> arr=itEntry.getValue();
//            System.out.println("************");
//            System.out.println(label);
//            for(int i=0;i<arr.size();i++)
//            {
//                System.out.print(arr.get(i)+ " ");
//            }
//            System.out.println("");
//            
//        }
//        System.out.println("**************************验证弱连通图********************");
    }
    
    
    
    private int belongStronglyConnectedSubgraph[];
    private  boolean [] whetherVisitedScc;
    private  int []nodeVisitIndex;
    private  int []nodeLowLink;
    private  Stack<Integer> stackScc;
     private  boolean [] whetherInStack;    
    private   int  index;
   private  HashMap<Integer,StronglyConnectedComponent> stronglyConnectedMap;
     private void strongConnect(int v,DirectedGraph graph )
    {    
            whetherVisitedScc[v]=true;
            
            nodeVisitIndex[v]=index;
            nodeLowLink[v]=index;
            index++;
            stackScc.push(v);
            whetherInStack[v]=true;
            Node node=graph.getNode(String.valueOf(v));
            for(Iterator<Node> iterator=graph.getSuccessors(node).iterator();iterator.hasNext();)
            {  Node nodeTemp=iterator.next();
               int w=Integer.parseInt((String)nodeTemp.getId());            
                   if(!whetherVisitedScc[w])
                   {strongConnect(w, graph);
                    nodeLowLink[v]=Math.min(nodeLowLink[v], nodeLowLink[w]);
                   
                   }
                   else  if(whetherInStack[w])
                         nodeLowLink[v]=Math.min(nodeLowLink[v], nodeVisitIndex[w]);
                   
                   
              
               
            }
            
          
             if(nodeVisitIndex[v]==nodeLowLink[v])
             { int temp=0;
              
               StronglyConnectedComponent scc=new StronglyConnectedComponent(v);
               {while((temp=stackScc.pop())!=v)
                    {
                    
                     scc.addNode(graph.getNode(String.valueOf(temp)));
       
                     whetherInStack[temp]=false;
                     belongStronglyConnectedSubgraph[temp]=v;
                    }
                   
               }
            
               scc.addNode(graph.getNode(String.valueOf(temp)));    
                whetherInStack[temp]=false;
                  belongStronglyConnectedSubgraph[temp]=v;     
                  
                  
                  
                  
                  
           
             stronglyConnectedMap.put(v, scc);
             }
             
            
             
                
             
       
    }
     private void splitSccs() throws NumberFormatException {
        /*******Tarjan算法*******/
        DirectedGraph diGraph=graphModel.getDirectedGraph();
        
        
        whetherVisitedScc=new boolean[nodeCount+1];//节点是否被访问过
         whetherInStack=new boolean[nodeCount+1];//节点是否在栈中
         nodeVisitIndex=new int[nodeCount+1];//节点访问顺序
        nodeLowLink=new int[nodeCount+1];
        stackScc=new Stack<Integer>();        
         belongStronglyConnectedSubgraph=new int[nodeCount+1];
        stronglyConnectedMap=new HashMap<Integer,StronglyConnectedComponent>();
        index=1;        
        
        System.out.println("Arrays fill start"+new Date().toString());
        Arrays.fill(whetherVisitedScc,false);
        System.out.println("Arrays fill over"+new Date().toString());
         Arrays.fill(whetherInStack,false);
       
        
       
       
        
      
        System.out.println("强连通分量开始"+new Date().toString());
        for(int i=1;i<=nodeCount;i++)
            {    if(!whetherVisitedScc[i])
                        {
                            strongConnect(i, diGraph);

                        }


            }
        System.out.println("强连通分量个数："+stronglyConnectedMap.size());
        
        for(Iterator<Map.Entry<Integer,StronglyConnectedComponent>> iteratorScc=stronglyConnectedMap.entrySet().iterator();iteratorScc.hasNext();)
        {
            Map.Entry<Integer,StronglyConnectedComponent> item=iteratorScc.next();
            int v=item.getKey();
            StronglyConnectedComponent scc=item.getValue();
            for(Iterator<Node> iteratorArr=scc.getArr().iterator();iteratorArr.hasNext();)
            {
                Node nodeTemp=iteratorArr.next();
                for(Iterator<Node> iteratorInNodes=diGraph.getPredecessors(nodeTemp).iterator();iteratorInNodes.hasNext();)
                {
                    Node inNode=iteratorInNodes.next();
                    int inSccID=belongStronglyConnectedSubgraph[Integer.parseInt((String)inNode.getId())];
                    if(inSccID!=scc.getLabel())
                        scc.addInScc(stronglyConnectedMap.get(inSccID));
                    
                }
                for(Iterator<Node> iteratorOutNodes=diGraph.getSuccessors(nodeTemp).iterator();iteratorOutNodes.hasNext();)
                { Node outNode=iteratorOutNodes.next();
                int outSccID=belongStronglyConnectedSubgraph[Integer.parseInt((String)outNode.getId())];
                if(outSccID!=scc.getLabel())
                    scc.addOutScc(stronglyConnectedMap.get(outSccID));
                
                }
            }
//            
//            /*验证Tarjan算法的正确性*/
//            System.out.println("***********"+v+"***********");
//            System.out.println("inSccs:");
//            for(StronglyConnectedComponent inScc:scc.getInSccs())
//                System.out.print(inScc.getLabel()+"  ");
//            System.out.println("");
//            System.out.println("outSccs:");
//            for(StronglyConnectedComponent outScc:scc.getOutSccs())
//                System.out.print(outScc.getLabel()+"  ");
//            System.out.println("");
//            
//            System.out.println("Nodes:");
//            for(Node arrNode:scc.getArr())
//                System.out.print(arrNode.getId()+"  ");
//            System.out.println("");
            
            
            
            
            
            
        }
        System.out.println("强连通分量结束"+new Date().toString());
    }    
     

    private void openFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openFileActionPerformed
            // TODO add your handling code here:
            
    JFileChooser chooser = new JFileChooser();
    FileNameExtensionFilter filter = new FileNameExtensionFilter(
        " gexf & gml text", "gexf", "gml");
    chooser.setFileFilter(filter);
    int returnVal = chooser.showOpenDialog(null);
    if(returnVal == JFileChooser.APPROVE_OPTION) {
       System.out.println("You chose to open this file: " +
            chooser.getSelectedFile().getName());
      filePath=chooser.getSelectedFile().getAbsolutePath();
    }
    
    
    if(filePath==null)
        return;
    
    long initialSecond=new GregorianCalendar().getTimeInMillis();
        System.out.println("开始显示图形"+new Date().toString());
    displayGraph();
    long overSecond=new GregorianCalendar().getTimeInMillis();
        System.out.println("图形显示处理时间"+(overSecond-initialSecond));
        long initialSecondPreProcess=new GregorianCalendar().getTimeInMillis();
         Graph graph=graphModel.getGraph();
          nodeCount=graph.getNodeCount();
         edgeCount=graph.getEdgeCount();
         
          /*显示图的相关信息*/
          getTestInformation(graph);
          
          //******************************************************************************
//          /*划分弱连通分量*/
//           splitWccs(graph);
//          if(graph.isDirected())         
//          { /*划分强连通分量*/
//          splitSccs();
//         /*强连通分量的拓扑排序，为了设定强连通分量的层次*/
//         topologicalSort();
//         
//          }
//          else
//          {
//              /*划分无向图*/
//
////          belong=new int[nodeCount+1];
////          cnm(graph);//不使用
//          }

//*********************************************************************************************
        long overSecondPreProcess=new GregorianCalendar().getTimeInMillis();
            System.out.println("preProcess"+(overSecondPreProcess-initialSecondPreProcess));
          ultimatePath="you don't  choose two nodes to find a path!";
          JOptionPane.showMessageDialog(null, "图的初始化已完毕");
          
       
            
            
            
            
            

        








        
        
    }//GEN-LAST:event_openFileActionPerformed
    private  ArrayList<StronglyConnectedComponent> findRoot()
     {    ArrayList<StronglyConnectedComponent> arr=new ArrayList<StronglyConnectedComponent>();
          for(Iterator<Map.Entry<Integer,StronglyConnectedComponent>> iteratorScc=stronglyConnectedMap.entrySet().iterator();iteratorScc.hasNext();)
         {
             Map.Entry<Integer,StronglyConnectedComponent> item=iteratorScc.next(); 
           
            StronglyConnectedComponent scc=item.getValue(); 
            if(scc.getInDegree()==0)
                arr.add(scc);
            
         }
          return arr;
     }
    private void topologicalSort() {
        /********DAG    initial layer********/
        
        ArrayList<StronglyConnectedComponent> currentLayerSccs= findRoot();
        int currentDepth=1;
        while(!currentLayerSccs.isEmpty())
        {
            ArrayList<StronglyConnectedComponent> nextLayerSccs=new ArrayList<StronglyConnectedComponent>();
            for(Iterator<StronglyConnectedComponent> iteratorScc=currentLayerSccs.iterator();iteratorScc.hasNext();)
            {
                StronglyConnectedComponent currentScc=iteratorScc.next();
                currentScc.setLayer(currentDepth);
                
                for(Iterator<StronglyConnectedComponent> iteratorSccOut=currentScc.getOutSccs().iterator();iteratorSccOut.hasNext();)
                {StronglyConnectedComponent successorScc=iteratorSccOut.next();
                successorScc.decInDegree();
                if(successorScc.getInDegree()==0)
                    nextLayerSccs.add(successorScc);
                
                }
                
            }
            currentLayerSccs=nextLayerSccs;
            currentDepth++;
            
        }
        
        
        //         /*验证拓扑排序的正确性*/
//          System.out.println("**************************验证拓扑排序的正确性*******************");
//           for(Iterator<Map.Entry<Integer,StronglyConnectedComponent>> iteratorScc=stronglyConnectedMap.entrySet().iterator();iteratorScc.hasNext();)
//           {
//              Map.Entry<Integer,StronglyConnectedComponent> item=iteratorScc.next(); 
//              int v=item.getKey();
//              StronglyConnectedComponent scc=item.getValue();  
//               System.out.println(v+" "+scc.getLayer()+" ");
//           }
//        System.out.println("**************************验证拓扑排序的正确性*******************");
    }    

    private void nodeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nodeMenuItemActionPerformed
        // TODO add your handling code here:
         if(graphModel==null)
                         {
                             JOptionPane.showMessageDialog(null, "您未选择文件！");
                             return;
                         }
        Property nodePropertyDialog=new Property(new JFrame(),"节点颜色", "节点大小",200,0);
        nodePropertyDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                 System.out.println("enter in all node change");               
                 if(nodePropertyDialog.getColor()!=null)
                    {    Color color=nodePropertyDialog.getColor();
                         int size=nodePropertyDialog.getDimension();                        

                        for(Iterator<Node> iterator=graphModel.getGraph().getNodes().iterator();iterator.hasNext();)
                            {
                                 Node node=iterator.next();
                                 node.setColor(color);
                                 node.setSize(size);
                            }
                         
                        refreshView();
                    }
            }
            
});
        nodePropertyDialog.setVisible(true);
       
        
    }//GEN-LAST:event_nodeMenuItemActionPerformed

    private void edgeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edgeMenuItemActionPerformed
        // TODO add your handling code here:
         if(graphModel==null)
                         {
                             JOptionPane.showMessageDialog(null, "您未选择文件！");
                             return;
                         }
         Property edgePropertyDialog=new Property(new JFrame(),"边颜色", "边粗细",20,0);
          edgePropertyDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                 System.out.println("enter in change");               
                 if(edgePropertyDialog.getColor()!=null)
                    {    Color color=edgePropertyDialog.getColor();
                         int size=edgePropertyDialog.getDimension();
                        

                        for(Iterator<Edge> iterator=graphModel.getGraph().getEdges().iterator();iterator.hasNext();)
                            {
                                 Edge edge=iterator.next();
                                 edge.setColor(color);
                                 edge.setWeight(size);
                            }
                         
                        refreshView();
                    }
            }
            
});
        edgePropertyDialog.setVisible(true);
    }//GEN-LAST:event_edgeMenuItemActionPerformed

    private void saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveActionPerformed
        // TODO add your handling code here:
        
    if(graphModel==null)
    {
        JOptionPane.showMessageDialog(null, "您未选择文件！");
         return;
    
    }
    String fileSavePath="";
    JFileChooser chooser = new JFileChooser();
    FileNameExtensionFilter filter = new FileNameExtensionFilter(
        "pdf", "pdf");
    chooser.setFileFilter(filter);
    GregorianCalendar gregorianCalendar= new GregorianCalendar();
    int year=gregorianCalendar.get(Calendar.YEAR);
    int month=gregorianCalendar.get(Calendar.MONTH)+1;
    int day=gregorianCalendar.get(Calendar.DAY_OF_MONTH);
    int hour=gregorianCalendar.get(Calendar.HOUR_OF_DAY);
     int minute=gregorianCalendar.get(Calendar.MINUTE);
      int second=gregorianCalendar.get(Calendar.SECOND);
      chooser.setSelectedFile(new  File(year+"_"+month+"_"+day+"_"+hour+"_"+minute+"_"+second+".pdf"));  
    int returnVal = chooser.showSaveDialog(null);
  
    if(returnVal == JFileChooser.APPROVE_OPTION) {       
       System.out.println("You chose to save to this file: " +"**"+
       chooser.getSelectedFile().getAbsolutePath()+"**");
      fileSavePath=chooser.getSelectedFile().getAbsolutePath();
    }
    
       
        
        
    if(fileSavePath.equals(""))
        return;
        
        
        //Export       
        
        try {
            File tempFile=new File(year+"_"+month+"_"+day+"_"+hour+"_"+minute+"_"+(second+1)+".pdf");
             ec.exportFile(tempFile);       
             
             
             
          PdfReader reader = new PdfReader(tempFile.getAbsolutePath());
          PdfStamper stamper;
        try {
         FileOutputStream   fileOutputStream=new FileOutputStream (fileSavePath);
         stamper = new PdfStamper(reader,fileOutputStream );
        
         
         int quotient=ultimatePath.length()/3690;
          int  remainder=ultimatePath.length()%3690;
          
           if(remainder!=0)
               quotient++;
           
           String [] ultimateSplit=new String[quotient];
           for(int i=0;i<quotient-1;i++)
           {   ultimateSplit[i]="";
               for(int j=i*3690;j<(i+1)*3690;j++)
               ultimateSplit[i]=ultimateSplit[i]+ultimatePath.charAt(j);
           }
           ultimateSplit[quotient-1]="";
           for(int j=(quotient-1)*3690;j<ultimatePath.length();j++)
           {
                ultimateSplit[quotient-1]=ultimateSplit[quotient-1]+ultimatePath.charAt(j);
           }
           for(int i=0;i<quotient;i++)
           {stamper.insertPage(2+i, reader.getPageSize(1));
           
            PdfContentByte cb = stamper.getOverContent(2+i);        
            ColumnText ct = new ColumnText(cb);
            ct.setSimpleColumn(reader.getPageSize(1).getLeft()+10f,reader.getPageSize(1).getTop()-10f,
            reader.getPageSize(1).getWidth()-10f  ,10f);        
           Paragraph p=new Paragraph(ultimateSplit[i]);          
             
           ct.addElement(p);
           ct.go();             
               
           }          
            stamper.close();   
            fileOutputStream.close();
            reader.close();
        } catch (DocumentException ex) {
            Exceptions.printStackTrace(ex);
        }
       tempFile.delete();            
      
        JOptionPane.showMessageDialog(null, "图像文件保存成功！");
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, ex.toString(),"保存成pdf文件出错",JOptionPane.ERROR_MESSAGE);
            return;
        }
        
    }//GEN-LAST:event_saveActionPerformed

    private void exactlyNodeModifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exactlyNodeModifyActionPerformed
        // TODO add your handling code here:
        if(graphModel==null)
            {
            JOptionPane.showMessageDialog(null, "您未选择文件！");
            return;
            }
        ExactNodeProperty exactNodeProperty=new ExactNodeProperty(null,200,0, graphModel.getGraph().getNodes());
         exactNodeProperty.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                 System.out.println("enter in change");               
                 if(exactNodeProperty.getColor()!=null)
                    {    Color color=exactNodeProperty.getColor();
                         int size=exactNodeProperty.getDimension();
                         String  nodeID=exactNodeProperty.getNodeID();
                         

                       Node node=graphModel.getGraph().getNode(nodeID);
                       if(node==null)
                       {
                           JOptionPane.showMessageDialog(null, "不存在该节点！");
                           return;
                       }
                       node.setColor(color);
                       node.setSize(size);
                         
                        refreshView();
                    }
            }
            
});
        exactNodeProperty.setVisible(true);
        
    }//GEN-LAST:event_exactlyNodeModifyActionPerformed

    private void exactlyEdgeModifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exactlyEdgeModifyActionPerformed
        // TODO add your handling code here:
         if(graphModel==null)
            {
            JOptionPane.showMessageDialog(null, "您未选择文件！");
            return;
            }
        ExactEdgeProperty exactEdgeProperty=new ExactEdgeProperty(null,20,0, graphModel.getGraph().getNodes());
         exactEdgeProperty.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                 System.out.println("enter in change");               
                 if(exactEdgeProperty.getColor()!=null)
                    {    Color color=exactEdgeProperty.getColor();
                         int size=exactEdgeProperty.getDimension();
                         String  sourceNodeID=exactEdgeProperty.getSourceNodeID();
                         String targetNodeID=exactEdgeProperty.getTargetNodeID();
                         
                   
                       Edge edge=graphModel.getGraph().getEdge(graphModel.getGraph().getNode(sourceNodeID),graphModel.getGraph().getNode(targetNodeID));
                       if(edge==null)
                       {
                           JOptionPane.showMessageDialog(null, "您选择的源节点和目的节点有误，不存在该边！");
                               return;
                       }
                       
                       edge.setColor(color);
                       edge.setWeight(size);
                         
                        refreshView();
                    }
            }
            });
        exactEdgeProperty.setVisible(true);
    }//GEN-LAST:event_exactlyEdgeModifyActionPerformed

    private void modifyBackgroundColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifyBackgroundColorActionPerformed
        // TODO add your handling code here:
       
          // TODO add your handling code here:
         if(graphModel==null)
            {
            JOptionPane.showMessageDialog(null, "您未选择文件！");
            return;
            }
        BackGroundColorChoose backGroundColorChoose=new BackGroundColorChoose(null);
         backGroundColorChoose.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                 System.out.println("enter in change");               
                 if(backGroundColorChoose.getColor()!=null)
                    {    Color color=backGroundColorChoose.getColor();
                         previewModel.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, color);                         
                   
                       
                         
                        refreshView();
                    }
            }
            });
        backGroundColorChoose.setVisible(true);
        
    }//GEN-LAST:event_modifyBackgroundColorActionPerformed

    private void exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_exitActionPerformed

    private void HelpMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HelpMenuItemActionPerformed
        // TODO add your handling code here:
        HelpDialog helpDialog=new HelpDialog(null);
        helpDialog.setVisible(true);
            
    }//GEN-LAST:event_HelpMenuItemActionPerformed

    private void AboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AboutMenuItemActionPerformed
        // TODO add your handling code here:
        AboutDialog aboutDialog=new AboutDialog(null);
        aboutDialog.setVisible(true);
                
    }//GEN-LAST:event_AboutMenuItemActionPerformed

    private void sourceTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sourceTextFieldKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER)
        {   
            targetTextField.requestFocus();
        }
    }//GEN-LAST:event_sourceTextFieldKeyPressed

    private void targetTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_targetTextFieldKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER)
        {
            filterButtonActionPerformed(null);
        }
    }//GEN-LAST:event_targetTextFieldKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainJFrame2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainJFrame2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainJFrame2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainJFrame2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainJFrame2().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem AboutMenuItem;
    private javax.swing.JMenuItem HelpMenuItem;
    private javax.swing.JMenuItem edgeMenuItem;
    private javax.swing.JMenu edit;
    private javax.swing.JMenuItem exactlyEdgeModify;
    private javax.swing.JMenuItem exactlyNodeModify;
    private javax.swing.JMenuItem exit;
    private javax.swing.JMenu file;
    private javax.swing.JButton filterButton;
    private javax.swing.JPanel graphPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenuItem modifyBackgroundColor;
    private javax.swing.JMenuItem nodeMenuItem;
    private javax.swing.JMenuItem openFile;
    private javax.swing.JTextArea pathTextArea;
    private javax.swing.JMenuItem save;
    private javax.swing.JTextField sourceTextField;
    private javax.swing.JTextField targetTextField;
    // End of variables declaration//GEN-END:variables
}
