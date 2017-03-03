import TestApp.*;
import org.omg.CosNaming.*;
import org.omg.CORBA.*;

public class ClienteCRM {

    static Test test;

    public ClienteCRM() {
        invocarComponente();
    }

    public void save(String nombre, String nombre2) {
        test.save(nombre, nombre2);
    }

    public void invocarComponente() {
        try {
            // crear e inicializar el ORB
            ClienteCRM.args[3] = "148.226.80.70";
            ORB orb = ORB.init(ClienteCRM.args, null);

            // obtener la raiz del nombre del contexto
            org.omg.CORBA.Object objRef =
                    orb.resolve_initial_references("NameService");

            // Usar NamingContextExt para instanciar NamingContext.
            // Esto es parte de la interoperabilidad del servicio de nombres.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // resolver la referencia del objeto con el nombre
            String name = "Test";
            test = TestHelper.narrow(ncRef.resolve_str(name));

        } catch (Exception e) {
            System.out.println("ERROR : " + e) ;
            e.printStackTrace(System.out);
        }
    }

    static String args[];
    public static void main(String args[]) {
        ClienteCRM.args = args;
        ClienteCRM c =  new ClienteCRM();
        c.save("PRBA", "TWWD");
    }

}
