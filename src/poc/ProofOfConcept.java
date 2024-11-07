
package poc;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import proyectoProgAvanzada.ProyectoFinalProgAvanzada;

/**
 *
 * For testing concepts
 * To be deleted from project, once all works as expected
 */
public class ProofOfConcept {
    
    public static void main(String[] args) {
       //for testing purposes...
       /*Thread thread = Thread.ofVirtual().start(() -> System.out.println("Hello"));
            try {
                thread.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(ProyectoFinalProgAvanzada.class.getName()).log(Level.SEVERE, null, ex);
            }
        */   
        String nombreArchivo = "src/testfile1.txt";
        File objectoFile = new File (nombreArchivo);
        
        //File objectoFile = new File (nombreArchivo);
        if (objectoFile.exists()){
            System.out.println("Archivo existe!" +objectoFile.getAbsolutePath());
            System.out.println("Path: "+ objectoFile.getPath());
        }else{
            System.out.println("Algo pasó");
        }
        
    }
    
}
