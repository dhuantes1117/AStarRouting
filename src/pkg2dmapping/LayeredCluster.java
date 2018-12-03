/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg2dmapping;

import java.util.*;

/**
 *
 * @author dhuant
 */
public class LayeredCluster extends ArrayList<Cluster>{
    public LayeredCluster() {
        
    }
    
    public LayeredCluster(List<Cluster> Areas) {
        this.addAll(Areas);
    }
    
    
}
