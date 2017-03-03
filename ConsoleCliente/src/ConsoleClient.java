import CorbaCatApp.*;
import org.omg.CosNaming.*;
import org.omg.CORBA.*;

public class ConsoleClient {

    static ICorbaCat corbaCat;

    public ConsoleClient() {
        invocarComponente();
    }

    public void registrarJugador(String nombre) {
        corbaCat.registrarJugador(nombre);
    }

    public int getX() {
        return corbaCat.obtenerJugadaX();
    }

    public int getY() { return corbaCat.obtenerJugadaY(); }

    public void invocarComponente() {
        try {
            // crear e inicializar el ORB
            ConsoleClient.args[3] = "localhost";
            ORB orb = ORB.init(ConsoleClient.args, null);

            // obtener la raiz del nombre del contexto
            org.omg.CORBA.Object objRef =
                    orb.resolve_initial_references("NameService");

            // Usar NamingContextExt para instanciar NamingContext.
            // Esto es parte de la interoperabilidad del servicio de nombres.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // resolver la referencia del objeto con el nombre
            String name = "CorbaCat";
            corbaCat = ICorbaCatHelper.narrow(ncRef.resolve_str(name));

        } catch (Exception e) {
            System.out.println("ERROR : " + e) ;
            e.printStackTrace(System.out);
        }
    }

    static String args[];
    public static void main(String args[]) {
        ConsoleClient.args = args;
        ConsoleClient c =  new ConsoleClient();
        c.registrarJugador("PRBA");
        System.out.println(c.getX());
        System.out.println(c.getY());
    }

}
