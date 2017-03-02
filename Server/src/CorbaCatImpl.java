import CorbaCatApp.ICorbaCatPOA;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CorbaCatImpl extends ICorbaCatPOA {

    ConectorJDBC conectorJDBC;
    int coordenadaX;
    int coordenadaY;
    int idJuego;
    Matriz matriz;

    public CorbaCatImpl() {
        this.conectorJDBC = new ConectorJDBC();
        this.matriz = new Matriz(
                new Par(0,0,2),
                new Par(0,1,2),
                new Par(0,2,2),
                new Par(1,0,2),
                new Par(1,1,2),
                new Par(1,2,2),
                new Par(2,0,2),
                new Par(2,1,2),
                new Par(2,2,2)
        );
    }

    private ORB orb;

    public void setORB(ORB orb_val) {
        orb = orb_val;
    }

    @Override
    public int registrarJugador(String s) {
        int id = -1;
        try {
            Connection connection = conectorJDBC.connectToMysqlDB("corba_cat",
                    "corba_cat",
                    "corba_cat",
                    "localhost");
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Jugador VALUES(-1, '"+s+"')",
                    Statement.RETURN_GENERATED_KEYS);
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                id = resultSet.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    @Override
    public int registraJuego(int i, int i1) {
        int id = -1;
        try {
            Connection connection = conectorJDBC.connectToMysqlDB("corba_cat",
                    "corba_cat",
                    "corba_cat",
                    "localhost");
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Juego VALUES(-1, "+i+","+i1+")",
                    Statement.RETURN_GENERATED_KEYS);
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                id = resultSet.getInt(1);
                idJuego = id;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    @Override
    public boolean registrarCoordenadas(int i, int i1, int i2) {
        boolean b = false;
        try {
            Connection connection = conectorJDBC.connectToMysqlDB("corba_cat",
                    "corba_cat",
                    "corba_cat",
                    "localhost");
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Coordenada VALUES(-1, "+i+","+i1+","+i2+", 1)");
            statement.executeUpdate();
            b = true;
        } catch (Exception e) {
            e.printStackTrace();
            b = false;
        }
        return b;
    }

    @Override
    public int obtenerJugadaX() {
        List<Par> jugadasCliente = obtenerOcupadosCliente();
        List<Par> jugadasServidor = obtenerOcupadosServidor();
        actualizarMatriz(jugadasCliente);
        actualizarMatriz(jugadasServidor);
        if (preguntarPorCentro()) {
            this.matriz.setPar5(new Par(1, 1, 0));
            return matriz.getPar5().getX();
        }
        else {
            Par par = seleccionarSmartCoordenada();
            List<Par> semiLista = new ArrayList<>();
            semiLista.add(par);
            actualizarMatriz(semiLista);
            return par.getX();
        }
    }

    private Par seleccionarSmartCoordenada() {
        return new Par(0,0,0);
    }

    private boolean preguntarPorCentro() {
        return matriz.getPar5().getDuenio() == 2;
    }

    private void actualizarMatriz(List<Par> jugadas) {
        for (Par par : jugadas) {
            switch (par.getX()) {
                case 0 :
                    switch (par.getY()) {
                        case 0 : matriz.setPar1(par); break;
                        case 1 : matriz.setPar2(par); break;
                        case 2 : matriz.setPar3(par); break;
                    }
                    break;
                case 1 :
                    switch (par.getY()) {
                        case 0 : matriz.setPar4(par); break;
                        case 1 : matriz.setPar5(par); break;
                        case 2 : matriz.setPar6(par); break;
                    }
                    break;
                case 2:
                    switch (par.getY()) {
                        case 0 : matriz.setPar7(par); break;
                        case 1 : matriz.setPar8(par); break;
                        case 2 : matriz.setPar9(par); break;
                    }
                    break;
            }
        }
    }

    private HashMap<Integer, Integer> obtenerMatriz() {
        HashMap<Integer, Integer> coordenadas = new HashMap<>();
        coordenadas.put(0,0);
        coordenadas.put(0,1);
        coordenadas.put(0,2);
        coordenadas.put(1,0);
        coordenadas.put(1,1);
        coordenadas.put(1,2);
        coordenadas.put(2,0);
        coordenadas.put(2,1);
        coordenadas.put(2,2);
        return coordenadas;
    }

    @Override
    public int obtenerJugadaY() {
        return coordenadaY;
    }

    private List<Par> obtenerOcupadosCliente() {
        List<Par> lista = new ArrayList<>();
        try {
            Connection connection = conectorJDBC.connectToMysqlDB("corba_cat",
                    "corba_cat",
                    "corba_cat",
                    "localhost");
            //tipo_jugador 1 = Cliente, tipo_jugador 0 = Servidor
            ResultSet resultSet = conectorJDBC.getResultSet(connection,
                    "SELECT x, y FROM Coordenada WHERE id_juego = "+idJuego+" AND tipo_jugador = 1)");
            while (resultSet.next()) {
                Par par = new Par(resultSet.getInt("x"), resultSet.getInt("y"), 1);
                lista.add(par);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    private List<Par> obtenerOcupadosServidor() {
        List<Par> lista = new ArrayList<>();
        try {
            Connection connection = conectorJDBC.connectToMysqlDB("corba_cat",
                    "corba_cat",
                    "corba_cat",
                    "localhost");
            //tipo_jugador 1 = Cliente, tipo_jugador 0 = Servidor
            ResultSet resultSet = conectorJDBC.getResultSet(connection,
                    "SELECT x, y FROM Coordenada WHERE id_juego = "+idJuego+" AND tipo_jugador = 0)");
            while (resultSet.next()) {
                Par par = new Par(resultSet.getInt("x"), resultSet.getInt("y"), 0);
                lista.add(par);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    class Par {
        private int x;
        private int y;
        //0 = Servidor, 1 = Cliente, 2 = Nadie
        int duenio;

        public Par(int x, int y, int duenio) {
            this.x = x;
            this.y = y;
            this.duenio = duenio;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getDuenio() {
            return duenio;
        }

        public void setDuenio(int duenio) {
            this.duenio = duenio;
        }
    }

    class Matriz {

        private Par par1;
        private Par par2;
        private Par par3;
        private Par par4;
        private Par par5;
        private Par par6;
        private Par par7;
        private Par par8;
        private Par par9;

        public Matriz(Par par1, Par par2, Par par3, Par par4, Par par5, Par par6, Par par7, Par par8, Par par9) {
            this.par1 = par1;
            this.par2 = par2;
            this.par3 = par3;
            this.par4 = par4;
            this.par5 = par5;
            this.par6 = par6;
            this.par7 = par7;
            this.par8 = par8;
            this.par9 = par9;
        }

        public Par getPar1() {
            return par1;
        }

        public void setPar1(Par par1) {
            this.par1 = par1;
        }

        public Par getPar2() {
            return par2;
        }

        public void setPar2(Par par2) {
            this.par2 = par2;
        }

        public Par getPar3() {
            return par3;
        }

        public void setPar3(Par par3) {
            this.par3 = par3;
        }

        public Par getPar4() {
            return par4;
        }

        public void setPar4(Par par4) {
            this.par4 = par4;
        }

        public Par getPar5() {
            return par5;
        }

        public void setPar5(Par par5) {
            this.par5 = par5;
        }

        public Par getPar6() {
            return par6;
        }

        public void setPar6(Par par6) {
            this.par6 = par6;
        }

        public Par getPar7() {
            return par7;
        }

        public void setPar7(Par par7) {
            this.par7 = par7;
        }

        public Par getPar8() {
            return par8;
        }

        public void setPar8(Par par8) {
            this.par8 = par8;
        }

        public Par getPar9() {
            return par9;
        }

        public void setPar9(Par par9) {
            this.par9 = par9;
        }
    }

}
