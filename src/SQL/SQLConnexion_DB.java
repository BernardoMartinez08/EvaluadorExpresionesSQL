/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SQL;
//Importar las librerias de .net y .sql que se necesitan para establcer conexiones
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Bernardo Jose
 */
public class SQLConnexion_DB {
    /*
        Atributos para almacenar la URL del servidor, el usuario para acceder
        y la contrasena que da acceso al mismo en el gestor de la base da datos.
    */
    private static String URL = "jdbc:mysql://localhost:3306/northwind";
    private static String User = "root";
    private static String Password = "24464438";
    
    /*
        Funcion que nos permite establecer la conexion  con la base de datos.
        Esta funcion debe ser estatica y retornar un tipo Connection que es
        propio de la libreria de MySQL JDBC.
    */
    public static Connection StarConnetion(){
        //Declaramos nuestra coneccion inicialmente como nula.
        /*
            En este parte se realiza un try osea de intenta establecer una
            conexion con el servidor usando los datos dados anteriormente.
        */
        Connection connection = null;
        try{
            //Establecemos la clase como una de tipo conexion a traves de la
            //libreria jdbc.Driver.
            Class.forName("com.mysql.jdbc.Driver");
            
            //Ahora nuestra conexion nula pasa a conectar con el servidor
            connection = DriverManager.getConnection(URL,User,Password);
            System.out.println("Conexion Establecida con la base de datos de Northwind");
        }catch(ClassNotFoundException | SQLException e){
            //Si no llega a completarse la conexion se detecta por:
            //ClassNotFoundException nos dira si las librerias no estan importadas.
            //SQLException nos dira si la coneccion no se logro establecer.
            System.out.println("Error de conexion: " + e);        
        }
        
        //Se retorna la conexion establecida.
        return connection;
    }
}
