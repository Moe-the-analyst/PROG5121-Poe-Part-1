
package part1;

/**
 *Student Number: st10456177
 * Full Name Monde Jobe-Jnr Matlhaku 
 * 
 * 
 * 
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
         Registration registration = new Registration();
         Login login = new Login(registration);
        login.setVisible(true);
    }
    
}
