import java.sql.*;


/**
 * Esta clase contiene metodos que se pueden utilizar para conectarse a la BD,
 * obtener un ResultSet a partir de una sentencia SQL y cerrar la conexion a la
 * BD. Importarte: Siempre llamar el metodo cerrarConexion al terminar de usar
 * la base de datos.
 */
public class ConectorJDBC {

    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    public ConectorJDBC() {
    }

    /**
     * Esta funcion permite conectarse a la Base de datos.
     *
     * @param dataBase   Nombre de la base de datos
     * @param user       Nombre del usuario
     * @param password   Contrasena del usuario
     * @param host       Direccion IP de la base de datos
     * @return           Un objeto [Connection] con la conexion a la Base de datos
     * @throws Exception Si hay un error en la conexion se regresa el error
     */
    public Connection connectToMysqlDB(String dataBase, String user, String password, String host) throws Exception {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection("jdbc:mysql://" + host + "/"+dataBase+"?user=" + user + "&password=" + password);
        } catch (ClassNotFoundException var6) {
            var6.getException();
        } catch (Exception var7) {
            var7.getMessage();
        }

        return this.connection;
    }

    /**
     * Este metodo regresa un ResultSet con la informacion recolectada por el SQL
     *
     * @param connection La conexion obetenida por el metodo connectoToMysqlDB
     * @param sqlQuery   La sentencia SQL para ser ejecutada en la BD
     * @return           El ResultSet obtenido.
     */
    public ResultSet getResultSet(Connection connection, String sqlQuery) {
        preparedStatement = this.getPreparedStatement(connection, sqlQuery);
        resultSet = null;

        try {
            resultSet = preparedStatement.executeQuery();
        } catch (Exception ex) {
            ex.getMessage();
        }

        return resultSet;
    }

    private PreparedStatement getPreparedStatement(Connection connection, String sqlQuery) {
        preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(sqlQuery);
        } catch (Exception var5) {
            var5.getMessage();
        }

        return preparedStatement;
    }

    /**
     * Este metodo se necesita llamar cada vez que se termina de
     * utilizar la conexion a la BD.
     */
    public void cerrarConexion(){
        if(resultSet != null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(preparedStatement != null){
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
