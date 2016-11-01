/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataguide;

import java.util.HashSet;

/**
 *
 * @author mary
 */
public class EntityClass {
    String pathToParentClass=null;
    Node node=null;
    EntityClass  parentClass=null;
    
    HashSet<String> pathsWithValuesInTheSubtree=new HashSet<String>();
    
    HashSet<String> pathsToChildClasses=new HashSet<String>();
    
    
    
    
    
    
    public EntityClass(Node n){this.node=n;}
     
    @Override
     public String toString(){
        return  "(node="+node.name+"  siblings "+node.currentSiblings+", pathToParent="+pathToParentClass+"       parent="+((parentClass!=null)?parentClass.node.name:null)+" pathsWithValues="+pathsWithValuesInTheSubtree + "  pathsToChildEntities="+pathsToChildClasses;
     }
}
