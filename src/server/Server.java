/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import classes.Billete;
import classes.Login;
import classes.Mensaje;
import classes.Usuario;
import classes.Register;
import classes.Viaje;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Usuario
 */
public class Server extends Thread {
    private final Socket skClient;
    static final int PORT=2000;
    private final ServerConnection dataBase;
    //Listas de Objetos de la base de datos
    private ArrayList<Viaje> listaViajes;
    //Separador de Array de respuestas recibidas
    //Métodos que puede recibir el servidor
    static final String LOGIN="LOGIN";
    static final String REGISTER="REGISTER";
    static final String MANAGE_TICKETS="MANAGE_TICKETS";
    static final String CREATE_TICKET="CREATE_TICKET";
    static final String UPDATE_TICKET="UPDATE_TICKET";
    static final String DELETE_TICKET="DELETE_TICKET";
    static final String DELETE_USER="DELETE_USER";
    static final String DELETE_USER_OUTPUT="DELETE USER SUCCESS";
    //Métodos para enviar al cliente 
    static final String LOGIN_OUTPUT="LOGIN SUCCESS";
    static final String REGISTER_OUTPUT="REGISTER SUCCESS";
    static final String MANAGE_TICKETS_OUTPUT="MANAGE TICKETS SUCCESS";
    static final String CREATE_TICKET_OUTPUT="CREATE TICKET SUCCESS";
    static final String UPDATE_TICKET_OUTPUT="UPDATE TICKET SUCCESS";
    static final String DELETE_TICKET_OUTPUT="DELETE TICKET SUCCESS";
    //Excepciones a mostrar
    static final String EXCEPTION_REGISTER="No se pudo registrar al usuario";
    static final String EXCEPTION_DELETE_USER="No se pudo borrar el usuario";
    static final String EXCEPTION_CREATE_TICKET="No se pudo crear el billete";
    static final String EXCEPTION_UPDATE_TICKET="No se pudo editar el billete";
    static final String EXCEPTION_DELETE_TICKET="No se pudo borrar el billete";
    static final String EXCEPTION_REGISTER_EMAIL_EXIST="Usuario ya existe con ese correo electrónico";
    
    
    public Server(Socket skClient){
        this.skClient = skClient;
        dataBase = new ServerConnection();
    }
    
    public static void main(String[] args) {
        System.out.println("Servidor listo!");
        try{
            ServerSocket skServer = new ServerSocket(PORT);
            while(true){
                Socket skClient = skServer.accept();
                new Server(skClient).start();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    //Método de escucha de la aplicación servidor
    public void run(){
        //Preparo la condición de cierre en falso
        boolean exit = false;
        //Inicializo el valor de respuestas que voy a recibir
        Mensaje input;
        String response;
        this.listaViajes = dataBase.getViajes();
        try{
            //Trato de crear los Data[Input/Output]Streams de emisión/recepción de mensajes con cliente
            ObjectOutputStream objectOutput = new ObjectOutputStream(skClient.getOutputStream());
            objectOutput.flush();
            ObjectInputStream objectInput = new ObjectInputStream(skClient.getInputStream());
            //Inicializo el usuario asociado con ese cliente
            Usuario usuario;
            System.out.println("Cliente conectado!");
            //Mientras la condición de cierre no sea true
            while(!exit){
                //Adecúo lo que recibo componiéndolo en el formato de respuesta esperado
                input = (Mensaje) objectInput.readObject();
                response = input.getMotivo();
                switch (response) {
                    //Caso LOGIN
                    case LOGIN:
                        System.out.println("Petición " + LOGIN + " recibida");
                        //Intento hacer la operación LOGIN
                        try{
                            //Hago que el usuario del cliente sea el de la respuesta de la operación
                            usuario = this.login((Login) input.getContenido());
                            
                            //Devuelvo al cliente que la operación ha sido un éxito
                            objectOutput.writeObject(new Mensaje(LOGIN_OUTPUT, usuario));
                            
                        } 
                        //Si capturo algún error...
                        catch (Exception e){
                            //Trato de enviar la causa del error al cliente                            
                            objectOutput.writeObject(new Mensaje(e.getMessage(), null));                            
                        }
                        break;
                    //Caso CREATE_TICKET
                    case CREATE_TICKET:
                        System.out.println("Petición " + CREATE_TICKET + " recibida");
                        try{
                            Billete billete = (Billete) input.getContenido();                            
                            boolean success = this.createTicket(billete);
                            //Devuelvo al cliente que la operación ha sido un éxito
                            if (success){
                                ArrayList<Billete> listaBilletes = dataBase.getBilletesOfUsuario(billete.getUsuario());
                                System.out.println("Lista de Billetes adjuntada del usuario " + billete.getUsuario().getNombre());
                                objectOutput.writeObject(new Mensaje(CREATE_TICKET_OUTPUT, listaBilletes));
                            }
                            else{
                                objectOutput.writeObject(new Mensaje(EXCEPTION_CREATE_TICKET, null));
                            }
                            break;
                        } catch (Exception e){
                            //Trato de enviar la causa del error al cliente   
                            objectOutput.writeObject(new Mensaje(e.getMessage(), null));
                        }
                        break;
                        
                    //Caso CREATE_TICKET
                    case UPDATE_TICKET:
                        System.out.println("Petición " + UPDATE_TICKET + " recibida");
                        try{
                            Billete billete = (Billete) input.getContenido();                            
                            boolean success = this.updateTicket(billete);
                            //Devuelvo al cliente que la operación ha sido un éxito
                            if (success){
                                ArrayList<Billete> listaBilletes = dataBase.getBilletesOfUsuario(billete.getUsuario());
                                System.out.println("Lista de Billetes adjuntada del usuario " + billete.getUsuario().getNombre());
                                objectOutput.writeObject(new Mensaje(UPDATE_TICKET_OUTPUT, listaBilletes));
                            }
                            else{
                                objectOutput.writeObject(new Mensaje(EXCEPTION_UPDATE_TICKET, null));
                            }
                            break;
                        } catch (Exception e){
                            //Trato de enviar la causa del error al cliente   
                            objectOutput.writeObject(new Mensaje(e.getMessage(), null));
                        }
                        break;
                    //Caso DELETE_TICKET
                    case DELETE_TICKET:
                        System.out.println("Petición " + DELETE_TICKET + " recibida");
                        try{
                            boolean success = this.deleteTicket((int) input.getContenido());
                            //Devuelvo al cliente que la operación ha sido un éxito
                            if (success){
                                objectOutput.writeObject(new Mensaje(DELETE_TICKET_OUTPUT, null));
                            }
                            else{
                                objectOutput.writeObject(new Mensaje(EXCEPTION_DELETE_TICKET, null));
                            }
                            break;
                        } catch (Exception e){
                            //Trato de enviar la causa del error al cliente   
                            objectOutput.writeObject(new Mensaje(e.getMessage(), null));
                        }
                        break;
                    case MANAGE_TICKETS:
                        System.out.println("Petición " + MANAGE_TICKETS + " recibida");
                        //Intento hacer la operación MANAGE_TICKETS
                        try{
                            //Devuelvo al cliente la lista de Viajes
                            objectOutput.writeObject(new Mensaje(MANAGE_TICKETS_OUTPUT, listaViajes));
                        } 
                        //Si capturo algún error...
                        catch (Exception e){
                            //Trato de enviar la causa del error al cliente                            
                            objectOutput.writeObject(new Mensaje(e.getMessage(), null));                            
                        }
                        break;
                    case REGISTER:
                        System.out.println("Petición " + REGISTER + " recibida");
                        //Intento hacer la operación MANAGE_TICKETS
                        try{
                            boolean success = this.register((Register) input.getContenido());
                            //Devuelvo al cliente que la operación ha sido un éxito
                            if (success){
                                objectOutput.writeObject(new Mensaje(REGISTER_OUTPUT, null));
                            }
                        } 
                        //Si capturo algún error...
                        catch (Exception e){
                            //Trato de enviar la causa del error al cliente                            
                            objectOutput.writeObject(new Mensaje(e.getMessage(), null));                            
                        }
                        break;
                    //Caso DELETE_USER
                    case DELETE_USER:
                        System.out.println("Petición " + DELETE_USER + " recibida");
                        try{
                            boolean success = this.deleteUser((int) input.getContenido());
                            //Devuelvo al cliente que la operación ha sido un éxito
                            if (success){
                                objectOutput.writeObject(new Mensaje(DELETE_USER_OUTPUT, null));
                            }
                            else{
                                objectOutput.writeObject(new Mensaje(EXCEPTION_DELETE_USER, null));
                            }
                            break;
                        } catch (Exception e){
                            //Trato de enviar la causa del error al cliente   
                            objectOutput.writeObject(new Mensaje(e.getMessage(), null));
                        }
                        break;
                    default:
                        System.out.println("Sin petición");
                        break;
                }
            }
        } catch(IOException e){
            System.err.println("Cliente desconectado");
        } catch(ClassNotFoundException e){
            System.err.println("Respuesta inesperada");
        }
    }
    
    //Método de la función LOGIN
    private Usuario login(Login login) throws Exception{
        String email = login.getEmail();
        String password = login.getPassword();
        //Primero Busco el usuario con dicho email
        Usuario usuario = searchUser(email);
        //Si no existe...
        if(usuario == null){
            //Envío "Ya no estoy listo"
            return null;
        }
        //Comparo la contraseña del usuario con la recibida,
        //y si no coincide...
        if(!userAccess(usuario, password)){  
            //Envío "Ya no estoy listo"
            return null;
        }
        //Devuelvo "Bienvenido [USUARIO]"
        return usuario;
    }
    
    //Método de la función REGISTER
    private boolean register(Register register) throws Exception{
        //Primero obtengo "email"
        String email = register.getEmail();
        //Recibo el nombre de usuario
        String name = register.getName();
        //Recibo la contraseña
        String password = register.getPassword();
        //Busco el usuario con dicho email
        Usuario usuario = searchUser(email);
        //Si existe un registro previo...
        if(usuario != null){
            //Envío "Ya no estoy listo"
            System.err.println("El cliente " + name + " trató de crear una cuenta con el correo electrónico " + email);
            throw new Exception(EXCEPTION_REGISTER_EMAIL_EXIST);
            }
        //Trato de crear al nuevo usuario
        boolean success = dataBase.createUsuario(email, name, password);
        //Si no puedo crearlo...
        if(!success){
            //Envío "Ya no estoy listo"
            System.err.println("De alguna manera, no se ha podido crear el usuario");
            throw new Exception(EXCEPTION_REGISTER);
        }
        //Devuelvo "[USUARIO] registrado"
        return true;
    }
    
    //Método de la función CREATE_TICKET
    private boolean createTicket(Billete billete) throws Exception{
        //Trato de crear el nuevo Billete
        boolean success = dataBase.createTicket(billete);
        //Si no puedo crearlo...
        if(!success){
            //Envío "Ya no estoy listo"
            System.err.println("De alguna manera, no se ha podido crear el billete");
            throw new Exception(EXCEPTION_CREATE_TICKET);
        }
        //Devuelvo "[USUARIO] registrado"
        return true;
    }
    
    //Método de la función UPDATE_TICKET
    private boolean updateTicket(Billete billete) throws Exception{
        //Trato de crear el nuevo Billete
        boolean success = dataBase.updateTicket(billete);
        //Si no puedo crearlo...
        if(!success){
            //Envío "Ya no estoy listo"
            System.err.println("De alguna manera, no se ha podido editar el billete");
            throw new Exception(EXCEPTION_UPDATE_TICKET);
        }
        //Devuelvo "[USUARIO] registrado"
        return true;
    }
    //Método de la función DELETE_TICKET
    private boolean deleteTicket(int id) throws Exception{
        //Trato de crear el nuevo Billete
        boolean success = dataBase.deleteTicket(id);
        //Si no puedo crearlo...
        if(!success){
            //Envío "Ya no estoy listo"
            System.err.println("De alguna manera, no se ha podido borrar el billete");
            throw new Exception(EXCEPTION_DELETE_TICKET);
        }
        //Devuelvo "[USUARIO] registrado"
        return true;
    }
    //Método de la función DELETE_TICKET
    private boolean deleteUser(int id) throws Exception{
        //Trato de crear el nuevo Billete
        boolean success = dataBase.deleteUser(id);
        //Si no puedo crearlo...
        if(!success){
            //Envío "Ya no estoy listo"
            System.err.println("De alguna manera, no se ha podido borrar al usuario");
            throw new Exception(EXCEPTION_DELETE_USER);
        }
        //Devuelvo "[USUARIO] registrado"
        return true;
    }
    //Método para obtener a un usuario buscando por su correo electrónico
    private Usuario searchUser(String email){
        //Creo una lista de usuarios registrados en la Base de Dato
        ArrayList<Usuario> listaUsuarios = this.dataBase.getUsuarios();
        //Y por cada usuario de la lista..
        for (Usuario usuario : listaUsuarios) {
            //Si dicho usuario coincide su correo por el nuestro...
            if (usuario.getCorreo().equals(email))
                //Devuelvo dicho usuario
                return usuario;
        }
        //Si nunca devuelvo un usuario, devuelvo null
        return null;
    }
    
    //Método para comprobar la valided de la contraseña con un usuario en cuestión
    private boolean userAccess(Usuario usuario, String password){
        //Devuelvo el valor booleano de la comparación de la contraseña del usuario con la introducida
        return usuario.getPassword().equals(password);
    }
    
}
