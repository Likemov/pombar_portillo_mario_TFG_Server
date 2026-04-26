/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;
import classes.Billete;
import classes.Parada;
import classes.Tramo;
import classes.Usuario;
import classes.Viaje;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 *
 * @author Usuario
 */
public class ServerConnection {
    //Variables de conexión
    private static final String DB_URL = "jdbc:mysql://gvtp_mysql:3306/gvtp_db";
    private static final String USER = "root";
    private static final String PSWRD = "root";
    
    //Tablas de la BDD
    //Tabla Usuario
    private static final String TABLE_USERS = "USUARIO";
    private static final String COLUMN_USER_ID= "id_usuario";
    private static final String COLUMN_USER_EMAIL= "correo";
    private static final String COLUMN_USER_NAME= "nombre";
    private static final String COLUMN_USER_PASSWORD= "password";
    //Tabla Billete
    private static final String TABLE_TICKETS = "BILLETE";
    private static final String COLUMN_TICKET_ID= "id_billete";
    private static final String COLUMN_TICKET_USER_ID= "id_usuario";
    private static final String COLUMN_TICKET_TRAVEL_ID= "id_viaje";
    private static final String COLUMN_TICKET_FIRST_STOP_ID= "id_parada_origen";
    private static final String COLUMN_TICKET_LAST_STOP_ID= "id_parada_destino";
    private static final String COLUMN_TICKET_BASE_PRICE= "precio_base";
    private static final String COLUMN_TICKET_DISCOUNT= "descuento";
    private static final String COLUMN_TICKET_START_DATE= "fecha_inicio";
    private static final String COLUMN_TICKET_END_DATE= "fecha_fin";
    //Tabla Viaje
    private static final String TABLE_TRAVEL = "VIAJE";
    private static final String COLUMN_TRAVEL_ID= "id_viaje";
    private static final String COLUMN_TRAVEL_NAME= "nombre";
    private static final String COLUMN_TRAVEL_TYPE= "tipo_transporte";
    //Tabla Tramo
    private static final String TABLE_TRAIL = "TRAMO";
    private static final String COLUMN_TRAIL_ID= "id_tramo";
    private static final String COLUMN_TRAIL_FIRST_STOP_ID= "id_parada_origen";
    private static final String COLUMN_TRAIL_LAST_STOP_ID= "id_parada_destino";
    private static final String COLUMN_TRAIL_DISTANCE= "distancia";
    private static final String COLUMN_TRAIL_TIME= "tiempo";
    //Tabla de relaciones Viaje-Tramo
    private static final String TABLE_TRAVELS_TRAILS = "VIAJE_TRAMO";
    private static final String COLUMN_TRAVELS_TRAILS_TRAVEL_ID= "id_viaje";
    private static final String COLUMN_TRAVELS_TRAILS_TRAIL_ID= "id_tramo";
    //Tabla Parada
    private static final String TABLE_STOP = "PARADA";
    private static final String COLUMN_STOP_ID= "id_parada";
    private static final String COLUMN_STOP_NAME= "nombre";
    
    private Connection connection = null;
    
    //Constructor de la clase ServerConnection
    public ServerConnection(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            //Trato de conectar con la base de datos
            connection = DriverManager.getConnection(DB_URL, USER, PSWRD);
        } catch (SQLException e){
            //Capturo SQLException y muestro detalles del error
            e.printStackTrace();
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }
    
    //Método para obtener a todos los usuarios
    public ArrayList<Usuario> getUsuarios(){
        //Preparo la lista de usuarios
        ArrayList<Usuario> listaUsuarios = new ArrayList<>();
        //Compongo la sentencia para la base de datos
        String query = "SELECT * FROM " + TABLE_USERS + ";";
        try{
            //Preparo la operación
            Statement stmnt = connection.createStatement();
            //Obtengo el resultado de realizar la operación con la sentencia
            ResultSet result = stmnt.executeQuery(query);
            //Mientras tenga resultados
            while (result.next()){
                //Compongo los datos del usuario obtenido
                int id = result.getInt(COLUMN_USER_ID);
                String nombre = result.getString(COLUMN_USER_NAME);
                String correo = result.getString(COLUMN_USER_EMAIL);
                String password = result.getString(COLUMN_USER_PASSWORD);
                //Creo al usuario recibido
                Usuario usuario = new Usuario(id, correo, nombre, password);
                //Adjunto la lista de billetes de usuario obtenida
                usuario.setListaBilletes(getBilletesOfUsuario(usuario));
                //Y añado al usuario a la lista
                listaUsuarios.add(usuario);
            }
            //Devuelvo la lista
            return listaUsuarios;
        } catch (SQLException e){
            //Controlo SQLException y muestro detalles del error
            e.printStackTrace();
            //Devuelvo lista de usuarios
            return listaUsuarios;
        }
    }
    
    //Método para obtener a todos los billetes
    public ArrayList<Billete> getBilletesOfUsuario(Usuario usuario){
        //Preparo la lista de billetes
        ArrayList<Billete> listaBilletes = new ArrayList<>();
        //Compongo la sentencia para la base de datos
        String query = "SELECT * FROM " + TABLE_TICKETS;
        query += " WHERE " + COLUMN_TICKET_USER_ID + " = " + usuario.getId() + ";" ;
        try{
            //Preparo la operación
            Statement stmnt = connection.createStatement();
            //Obtengo el resultado de realizar la operación con la sentencia
            ResultSet result = stmnt.executeQuery(query);
            //Mientras tenga resultados
            while (result.next()){
                //Compongo los datos del usuario obtenido
                int id = result.getInt(COLUMN_TICKET_ID);
                int idViaje = result.getInt(COLUMN_TICKET_TRAVEL_ID);
                int idOrigen = result.getInt(COLUMN_TICKET_FIRST_STOP_ID);
                int idDestino = result.getInt(COLUMN_TICKET_LAST_STOP_ID);
                float precioBase = result.getFloat(COLUMN_TICKET_BASE_PRICE);
                float descuento = result.getFloat(COLUMN_TICKET_DISCOUNT);
                LocalDate fechaInicio = result.getObject(COLUMN_TICKET_START_DATE, LocalDate.class);
                LocalDate fechaFin = result.getObject(COLUMN_TICKET_END_DATE, LocalDate.class);
                
                Viaje viaje = getViajeById(idViaje);
                Parada paradaOrigen = this.getParadaById(idOrigen);
                Parada paradaDestino= this.getParadaById(idDestino);
                //Y añado el billete a la lista
                listaBilletes.add(new Billete(id, usuario, viaje, paradaOrigen, paradaDestino, precioBase, descuento, fechaInicio, fechaFin));
            }
            //Devuelvo la lista
            return listaBilletes;
        } catch (SQLException e){
            //Controlo SQLException y muestro detalles del error
            e.printStackTrace();
            //Devuelvo lista de billetes
            return listaBilletes;
        }
    }
    
    //Método para obtener todos los viajes
    public ArrayList<Viaje> getViajes(){
        //Preparo la lista de viajes
        ArrayList<Viaje> listaViajes = new ArrayList<>();
        //Compongo la sentencia SQL para la base de datos
        String query = "SELECT * FROM " + TABLE_TRAVEL + ";";
        try{
            //Preparo la operación
            Statement stmnt = connection.createStatement();
            //Obtengo el resultado de ejecución de mi sentencia SQL
            ResultSet result = stmnt.executeQuery(query);
            //Mientras obtenga resultados
            while(result.next()){
                //Compongo el viaje a añadir por los datos obtenidos
                int id = result.getInt(COLUMN_TRAVEL_ID);
                String nombre = result.getString(COLUMN_TRAVEL_NAME);
                String tipo = result.getString(COLUMN_TRAVEL_TYPE);
                //Creo el viaje obtenido
                Viaje viaje = new Viaje(id, nombre, tipo);
                //Y adjunto a su lista de tramos los tramos obtenidos por su tabla de relaciones
                viaje.setListaTramos(getTramosOfViajeByViajeId(id));
                //Añado el viaje a la lista
                listaViajes.add(viaje);
            }
            //Devuelvo la lista al final de la operación normal
            return listaViajes;
        } catch(SQLException e){
            e.printStackTrace();
            return listaViajes;
        }
    }
    //Método para obtener un viaje por su id
    public Viaje getViajeById(int id_viaje){
        //Preparo el viaje a devolver
        Viaje viaje;
        //Compongo la sentencia SQL para la base de datos
        String query = "SELECT * FROM " + TABLE_TRAVEL;
        query += " WHERE " + COLUMN_TRAVEL_ID + " = " + id_viaje + ";" ;
        try{
            //Preparo la operación
            Statement stmnt = connection.createStatement();
            //Obtengo el resultado de ejecución de mi sentencia SQL
            ResultSet result = stmnt.executeQuery(query);
            if (result.next()) {
                //Compongo el viaje a añadir por los datos obtenidos
                int id = result.getInt(COLUMN_TRAVEL_ID);
                String nombre = result.getString(COLUMN_TRAVEL_NAME);
                String tipo = result.getString(COLUMN_TRAVEL_TYPE);
                //Creo el viaje obtenido
                viaje = new Viaje(id, nombre, tipo);
                //Y adjunto a su lista de tramos los tramos obtenidos por su tabla de relaciones
                viaje.setListaTramos(getTramosOfViajeByViajeId(id));

                //Devuelvo el viaje completo
                return viaje;
            }
            return null;
        } catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }
    //Método para obtener a todos los tramos
    public ArrayList<Tramo> getTramos(){
        //Preparo la lista de tramos
        ArrayList<Tramo> listaTramos = new ArrayList<>();
        //Compongo la sentencia para la base de datos
        String query = "SELECT * FROM " + TABLE_TRAIL + ";";
        try{
            //Preparo la operación
            Statement stmnt = connection.createStatement();
            //Obtengo el resultado de realizar la operación con la sentencia
            ResultSet result = stmnt.executeQuery(query);
            //Mientras tenga resultados
            while (result.next()){
                //Compongo los datos del usuario obtenido
                int id = result.getInt(COLUMN_TRAIL_ID);
                int idOrigen = result.getInt(COLUMN_TRAIL_FIRST_STOP_ID);
                int idDestino = result.getInt(COLUMN_TRAIL_LAST_STOP_ID);
                String tiempo = result.getString(COLUMN_TRAIL_TIME);
                float distancia = result.getFloat(COLUMN_TRAIL_DISTANCE);
                
                Parada paradaOrigen = this.getParadaById(idOrigen);
                Parada paradaDestino= this.getParadaById(idDestino);
                //Y añado al usuario a la lista
                listaTramos.add(new Tramo(id, paradaOrigen, paradaDestino, distancia, tiempo));
            }
            //Devuelvo la lista
            return listaTramos;
        } catch (SQLException e){
            //Controlo SQLException y muestro detalles del error
            e.printStackTrace();
            //Devuelvo lista de usuarios
            return listaTramos;
        }
    }
    
    //Método para obtener la lista de tramos de un viaje por id de viaje
    public ArrayList<Tramo> getTramosOfViajeByViajeId(int id_viaje){
        ArrayList<Tramo> listaTramos = new ArrayList<>();
        //Compongo la sentencia para la base de datos
        String query = "SELECT " + COLUMN_TRAVELS_TRAILS_TRAIL_ID + " FROM " + TABLE_TRAVELS_TRAILS;
        query += " WHERE " + COLUMN_TRAVELS_TRAILS_TRAVEL_ID + " = " + id_viaje + ";" ;
        try{
            //Preparo la operación
            Statement stmnt = connection.createStatement();
            //Obtengo el resultado de realizar la operación con la sentencia
            ResultSet result = stmnt.executeQuery(query);
            while (result.next()){
                //Tomo el id
                int id = result.getInt(COLUMN_TRAIL_ID);
                //Busco el tramo completo por el id obtenido
                Tramo tramo = getTramoById(id);
                //Y lo añado a la lista
                listaTramos.add(tramo);
            }
            return listaTramos;
        } catch (SQLException e){
            e.printStackTrace();
            return listaTramos;
        }
    }
    
    //Método para obtener a el tramo por id
    public Tramo getTramoById(int id_tramo){
       //Preparo el tramo a obtener
        Tramo tramo;
        //Compongo la sentencia para la base de datos
        String query = "SELECT * FROM " + TABLE_TRAIL;
        query += " WHERE " + COLUMN_TRAIL_ID + " = " + id_tramo + ";" ;
        try{
            //Preparo la operación
            Statement stmnt = connection.createStatement();
            //Obtengo el resultado de realizar la operación con la sentencia
            ResultSet result = stmnt.executeQuery(query);
            if (result.next()) {
                //Compongo los datos del tramo obtenido
                int id = result.getInt(COLUMN_TRAIL_ID);
                int idOrigen = result.getInt(COLUMN_TRAIL_FIRST_STOP_ID);
                int idDestino = result.getInt(COLUMN_TRAIL_LAST_STOP_ID);
                String tiempo = result.getString(COLUMN_TRAIL_TIME);
                float distancia = result.getFloat(COLUMN_TRAIL_DISTANCE);

                Parada paradaOrigen = this.getParadaById(idOrigen);
                Parada paradaDestino= this.getParadaById(idDestino);

                //Y compongo el tramo obtenido
                tramo = new Tramo(id, paradaOrigen, paradaDestino, distancia, tiempo);

                //Devuelvo el tramo
                return tramo;
            }
            return null;
        } catch (SQLException e){
            //Controlo SQLException y muestro detalles del error
            e.printStackTrace();
            //Devuelvo null
            return null;
        }
    }
    
    //Método para obtener todas las paradas
    public ArrayList<Parada> getParadas(){
        //Preparo la lista de paradas
        ArrayList<Parada> listaParadas = new ArrayList<>();
        //Compongo la sentencia para la base de datos
        String query = "SELECT * FROM " + TABLE_STOP + ";";
        try{
            //Preparo la operación
            Statement stmnt = connection.createStatement();
            //Obtengo el resultado de realizar la operación con la sentencia
            ResultSet result = stmnt.executeQuery(query);
            //Mientras tenga resultados
            while (result.next()){
                //Compongo los datos del usuario obtenido
                int id = result.getInt(COLUMN_STOP_ID);
                String nombre = result.getString(COLUMN_STOP_NAME);
                //Y añado al usuario a la lista
                listaParadas.add(new Parada(id, nombre));
            }
            //Devuelvo la lista
            return listaParadas;
        } catch (SQLException e){
            //Controlo SQLException y muestro detalles del error
            e.printStackTrace();
            //Devuelvo lista de usuarios
            return listaParadas;
        }
    }
    //Método para obtener una parada por su id
    public Parada getParadaById(int id_parada){
        //Preparo la lista de paradas
        Parada parada;
        //Compongo la sentencia para la base de datos
        String query = "SELECT * FROM " + TABLE_STOP;
        query += " WHERE " + COLUMN_STOP_ID + " = " + id_parada + ";" ;
        try{
            //Preparo la operación
            Statement stmnt = connection.createStatement();
            //Obtengo el resultado de realizar la operación con la sentencia
            ResultSet result = stmnt.executeQuery(query);
            if (result.next()) {
                //Compongo los datos de la parada obtenida
                int id = result.getInt(COLUMN_STOP_ID);
                String nombre = result.getString(COLUMN_STOP_NAME);
                //Y creo la parada
                parada = new Parada(id, nombre);

                //Devuelvo la parada
                return parada;
            }
            return null;
        } catch (SQLException e){
            //Controlo SQLException y muestro detalles del error
            e.printStackTrace();
            //Devuelvo lista de usuarios
            return null;
        }
    }
    
    //Método para crear un usuario
    public boolean createUsuario(String email, String name, String password){
        //Preparo la nueva sentencia
        String query = "INSERT INTO " + TABLE_USERS + " (" 
                + COLUMN_USER_EMAIL + ", " 
                + COLUMN_USER_NAME + ", " 
                + COLUMN_USER_PASSWORD + ") " 
                + "VALUES (?, ?, ?)";
        try{
            //Preparo la operación
            var ps = connection.prepareStatement(query);
            //Asigno a la sentencia de la operación preparada los datos de entrada
            ps.setString(1, email);
            ps.setString(2, name);
            ps.setString(3, password);
            //Obtengo la cantidad de líneas afectadas tras ejecutar la sentencia
            int rows = ps.executeUpdate();
            //Devuelvo el valor booleano de si la cantidad de líneas afectadas superan 0
            return rows > 0;
        } catch (SQLException e){
            //Controlo SQLException y muestro detalles del error
            e.printStackTrace();
            //Devuelvo false
            return false;
        }
    }
    //Método para crear un billete de un usuario
    public boolean createTicket(Billete billete){
        //Preparo la nueva sentencia
        String query = "INSERT INTO " + TABLE_TICKETS + " (" 
                + COLUMN_TICKET_USER_ID + ", " 
                + COLUMN_TICKET_TRAVEL_ID + ", " 
                + COLUMN_TICKET_FIRST_STOP_ID + ", " 
                + COLUMN_TICKET_LAST_STOP_ID + ", " 
                + COLUMN_TICKET_BASE_PRICE + ", " 
                + COLUMN_TICKET_DISCOUNT + ", " 
                + COLUMN_TICKET_START_DATE + ", " 
                + COLUMN_TICKET_END_DATE + ") " 
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try{
            //Preparo la operación
            var ps = connection.prepareStatement(query);
            //Asigno a la sentencia de la operación preparada los datos de entrada
            ps.setInt(1, billete.getUsuario().getId());
            ps.setInt(2, billete.getViaje().getId());
            ps.setInt(3, billete.getParadaOrigen().getId());
            ps.setInt(4, billete.getParadaDestino().getId());
            ps.setFloat(5, billete.getPrecioBase());
            ps.setFloat(6, billete.getDescuento());
            ps.setDate(7, java.sql.Date.valueOf(billete.getFechaInicio()));
            ps.setDate(8, java.sql.Date.valueOf(billete.getFechaFin()));
            //Obtengo la cantidad de líneas afectadas tras ejecutar la sentencia
            int rows = ps.executeUpdate();
            //Devuelvo el valor booleano de si la cantidad de líneas afectadas superan 0
            return rows > 0;
        } catch (SQLException e){
            //Controlo SQLException y muestro detalles del error
            e.printStackTrace();
            //Devuelvo false
            return false;
        }
    }
    
    //Método para editar un billete
    public boolean updateTicket(Billete billete){
        //Preparo la nueva sentencia
        String query = "UPDATE " + TABLE_TICKETS + " SET "
        + COLUMN_TICKET_USER_ID + " = " + "?, "
        + COLUMN_TICKET_TRAVEL_ID + " = " + "?, "
        + COLUMN_TICKET_FIRST_STOP_ID + " = " + "?, "
        + COLUMN_TICKET_LAST_STOP_ID + " = " + "?, "
        + COLUMN_TICKET_BASE_PRICE + " = " + "?, "
        + COLUMN_TICKET_DISCOUNT + " = " + "?, "
        + COLUMN_TICKET_START_DATE + " = " + "?, "
        + COLUMN_TICKET_END_DATE + " = " + "? ";
        query += "WHERE " + COLUMN_TICKET_ID + " = ?";
        try{
            //Preparo la operación
            var ps = connection.prepareStatement(query);
            //Asigno a la sentencia de la operación preparada los datos de entrada
            ps.setInt(1, billete.getUsuario().getId());
            ps.setInt(2, billete.getViaje().getId());
            ps.setInt(3, billete.getParadaOrigen().getId());
            ps.setInt(4, billete.getParadaDestino().getId());
            ps.setFloat(5, billete.getPrecioBase());
            ps.setFloat(6, billete.getDescuento());
            ps.setObject(7, billete.getFechaInicio());
            ps.setObject(8, billete.getFechaFin());
            ps.setInt(9, billete.getId());
            //Obtengo la cantidad de líneas afectadas tras ejecutar la sentencia
            int rows = ps.executeUpdate();
            //Devuelvo el valor booleano de si la cantidad de líneas afectadas superan 0
            return rows > 0;
        } catch (SQLException e){
            //Controlo SQLException y muestro detalles del error
            e.printStackTrace();
            //Devuelvo false
            return false;
        }
    }
    //Método para borrar un billete
    public boolean deleteTicket(int id){
        //Preparo la nueva sentencia
        String query = "DELETE FROM " + TABLE_TICKETS + " ";
        query += "WHERE " + COLUMN_TICKET_ID + " = ?";
        try{
            //Preparo la operación
            var ps = connection.prepareStatement(query);
            //Asigno a la sentencia de la operación preparada los datos de entrada
            ps.setInt(1, id);
            //Obtengo la cantidad de líneas afectadas tras ejecutar la sentencia
            int rows = ps.executeUpdate();
            //Devuelvo el valor booleano de si la cantidad de líneas afectadas superan 0
            return rows > 0;
        } catch (SQLException e){
            //Controlo SQLException y muestro detalles del error
            e.printStackTrace();
            //Devuelvo false
            return false;
        }
    }
    
    //Método para borrar un usuario
    public boolean deleteUser(int id){
        //Preparo la nueva sentencia
        String query = "DELETE FROM " + TABLE_USERS + " ";
        query += "WHERE " + COLUMN_USER_ID + " = ?";
        try{
            //Preparo la operación
            var ps = connection.prepareStatement(query);
            //Asigno a la sentencia de la operación preparada los datos de entrada
            ps.setInt(1, id);
            //Obtengo la cantidad de líneas afectadas tras ejecutar la sentencia
            int rows = ps.executeUpdate();
            //Devuelvo el valor booleano de si la cantidad de líneas afectadas superan 0
            return rows > 0;
        } catch (SQLException e){
            //Controlo SQLException y muestro detalles del error
            e.printStackTrace();
            //Devuelvo false
            return false;
        }
    }
}
