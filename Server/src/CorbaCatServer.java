import CorbaCatApp.ICorbaCat;
import CorbaCatApp.ICorbaCatHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;


public class CorbaCatServer {

    public static void main(String args[]) {
        try {
            ORB orb = ORB.init(args, null);

            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            CorbaCatImpl corbaCatImpl = new CorbaCatImpl();
            corbaCatImpl.setORB(orb);

            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(corbaCatImpl);
            ICorbaCat href = ICorbaCatHelper.narrow(ref);

            org.omg.CORBA.Object objRef =
                    orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            String name = "CorbaCat";
            NameComponent path[] = ncRef.to_name(name);
            ncRef.rebind(path, href);

            System.out.println("El servidor con el componente CORBA está listo...!");

            orb.run();
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.out);
        }
        System.out.println("Cerrando servidor...");
    }

}
