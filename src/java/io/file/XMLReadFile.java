/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.file;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author SuMario
 */
public class XMLReadFile extends ReadFile{
    
    private Document doc=null;
    private DocumentBuilder docBuilder=null;
    private DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    
    public XMLReadFile(String dir, String file) { super(dir, file); parse(); }        
    public XMLReadFile(String nfile){             super( nfile );   parse(); }
    public XMLReadFile(File file) {               super(file);      parse(); }
    
    private boolean __OK__=false; 
    
    private void parse() {
      if ( ! __OK__ ) {  
        try {   
          if ( docBuilder == null ) { 
              
              docBuilder = docBuilderFactory.newDocumentBuilder(); 
              docBuilder.setErrorHandler(new ErrorHandler() {
                                            @Override
                                            public void warning(SAXParseException ex) throws SAXException {
                                                final String func=getFunc("error(SAXParseException ex)");
                                                printf(func,2,"Exception produced in line "+ex.getLineNumber()+" position "+ex.getColumnNumber()+" message are :"+ex.getMessage());
                                            }

                                            @Override
                                            public void fatalError(SAXParseException ex) throws SAXException {
                                                final String func=getFunc("error(SAXParseException ex)");
                                                printf(func,2,"Exception produced in line "+ex.getLineNumber()+" position "+ex.getColumnNumber()+" message are :"+ex.getMessage());
                                            }

                                            @Override
                                            public void error(SAXParseException ex) throws SAXException {
                                                final String func=getFunc("error(SAXParseException ex)");
                                                printf(func,2,"Exception produced in line "+ex.getLineNumber()+" position "+ex.getColumnNumber()+" message are :"+ex.getMessage());
                                            }
                                        });
          
          }
          ByteArrayInputStream input =  new ByteArrayInputStream( readOut().toString().getBytes("UTF-8"));
          doc = docBuilder.parse(input);
          __OK__=true;
        }catch(Exception e) { __OK__=false;}  
      }
    }
    
    public void printOut() {
        
        try { 
         System.out.println("Root element :" 
            + doc.getDocumentElement().getNodeName());
         NodeList nList = doc.getElementsByTagName("student");
         System.out.println("----------------------------");
         for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            System.out.println("\nCurrent Element :" 
               + nNode.getNodeName());
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
               Element eElement = (Element) nNode;
                System.out.println("element:"+eElement);
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
    }
    
    
    public NodeList getNodeList(String name){
        NodeList nList = doc.getElementsByTagName(name);
        return nList;
    }
    
    public String getContext(String eName,String node) {
        final String func="getContext(String eName,String node)";
        NodeList nList = getNodeList(node);
        printf(func,2, "node has "+nList.getLength()+" elements"); 
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            printf(func,3,"Current Element :"  + nNode.getNodeName()+" NODE:"+nNode);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                printf(func,3,"Element"+temp+":"+eElement);
                if ( eElement != null ) {
                    NodeList m = eElement.getElementsByTagName(eName);
                    if ( m != null && m.getLength()>0) {
                        if ( m.item(0).getNodeName().matches(eName) ) {
                            String t = m.item(0).getNodeValue();
                            printf(func,2,"found "+eName+" : " +t);
                            return t;
                        }
                    }
                    
                    HashMap<String,String> p =  getAttributes(nNode) ;
                    if ( p.get(eName) != null ) {  
                        printf(func,2,"found "+eName+" : with value " +p.get(eName));
                        return p.get(eName); 
                    }

                } else {
                    printf(func,3,"getting for "+eName+" : NULL");
                }  
                
            }
        }
        return "";
    }
    
    
    public HashMap<String,String> getAttributes(Node n) {
        final String func=getFunc("getAttributes(Node n)");
        NamedNodeMap m = n.getAttributes();
        HashMap<String, String> sp = new HashMap<String, String>();
        for ( int j=0; j<m.getLength(); j++ ) {
                    Node l = m.item(j);
                    printf(func, 2, "Attribute Node =>|"+l+"|<= >>"+l.getNodeName()+"<<  >>"+l.getNodeValue()+"<<");
                    sp.put(l.getNodeName(), l.getNodeValue());
        }
        return sp;
    }
    
    public static void main(String[] args) {
        if ( args.length == 0 ) { throw new RuntimeException("ERROR: missing file propertie");}
        for(int i=0; i< args.length; i++) {
             XMLReadFile f = (new XMLReadFile(args[i]));
             f.printOut(); 
             
             f.getContext("NAME", "HOME");
        }
    }

    public void nodeReadout(NodeList nlf, HashMap<String, String> nh) {
        System.out.println("1bb"+nlf);
        if ( nlf == null ) { return; }
        int savdebug=debug;
        debug=4;
        final String func=getFunc("nodeReadout(NodeList nlf, HashMap<String, String> nh)");
        printf(func,4,"starting");
        if ( nlf.getLength() > 0 ) {
            for(int i=0;i<nlf.getLength(); i++) {
                Node n = nlf.item(i);
                NamedNodeMap na = n.getAttributes();
                printf(func,3,"node["+i+"]="+n.getNodeName()+"=>"+n.getNodeValue()+"\n\t"
                      +func+"content:"+n.getTextContent()+"|\n\t"
                      +func+"map (na) =>|"+na+"|<=");
                if ( nh.get(n.getNodeName()) == null )
                  nh.put(n.getNodeName(), ((n.getNodeValue()==null)?n.getTextContent():n.getNodeValue()) );
                if ( n.hasChildNodes() ) {
                    HashMap<String, String> nhn = new HashMap<String, String>();
                    nodeReadout(n.getChildNodes(),nhn);
                    if ( ! nhn.isEmpty() ) {
                        printf(func,2,"nhn =>"+nhn+"<=");
                    }
                }
            }
        }
        debug=savdebug;
        printf(func,4,"completed");
    }
    
    
}
