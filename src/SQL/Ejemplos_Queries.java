/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SQL;

import static SQL.Queries_SQL.lastOrder;
import SQL.SQLConnexion_DB;
import java.awt.HeadlessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import javax.swing.JOptionPane;
/**
 *
 * @author Bernardo Jose
 */
public class Ejemplos_Queries {
    //Atributo que nos permite hacer uso de una coneccion a la base de datos.
    private static Connection connetion;
    
    //Atributo que se encarga de hacer las consultas con la base de datos.
    private static PreparedStatement prepared_statement;
    
    //Atributo que almacena la consulta
    private static Statement statement;
    
    //Atributo que almacena el resultado devuelto de una consulta
    private static ResultSet result;
    
    
//Ejemplo de consulta  de Seleccion (Select)
    public static int lastOrder() {
        int LastOrder = 0;
        //Todo procedimiento de consulta a la base de datos debe ir en un try
        //El objetivo de este es evitar que el programa truene.
        try {
            //Establecemos nuestra conexion haciendo uso de la coneccion creada anteriormente
            connetion = SQLConnexion_DB.StarConnetion();
            
            //En un string almacenamos la consulta que deseanos realizar sobre la BD.
            String select_query = "SELECT OrderID FROM Orders ORDER BY OrderID DESC LIMIT 1";
            
           //El prepared statemente se engarga de hacer la consulta en la BD de parametro
           //enviamos la consulta que queremos realizar.
            prepared_statement = connetion.prepareStatement(select_query);
            
            //El ResultSet se encarga de almacenar el resultado arrojado por la base de datos.
            result = prepared_statement.executeQuery();

            /*
                El formato de retorno que nos da la BD es una tabla por lo danto debemos 
                extraer la info que necesitamos, este if se encarga de decirnos si
                realmente existe un resultado que extraer. En este caso la consulta
                deberia de devolver una tabla de 1x1 por lo tanto no es necesario
                un recorrido while, en casos donde la tabla es mas grande si aplica 
                un recorrido.
            */
            if (result.next()) {
                //Tambien sabemos que el resultado de retorno es un Int lo extraemos
                //directamente con el comando .getInt(1); seria la posicion 1 de la fila.
                LastOrder = result.getInt(1);
            }
            System.out.println("Orden Importada con Exito!!" + LastOrder);
            
            //Muy importante finalizar la conexion cuando ya no estemos haciendo uso de ella.
            connetion.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        } finally {
            try {
                connetion.close();
            } catch (SQLException e) {
                System.out.println("Error: " + e);
            }
        }

        return LastOrder;
    }
    
    
    //Ejemplo de consulta de Insercion y Actualicion (Insert and Update)
    public static void insertar_datos_orden_detail(int order_id, int product_id, double unit_price, int quantity, double discount) {
        //Todo procedimiento de consulta a la base de datos debe ir en un try
        //El objetivo de este es evitar que el programa truene.
        try {
            //Establecemos nuestra conexion haciendo uso de la coneccion creada anteriormente
            connetion = SQLConnexion_DB.StarConnetion();
            
            //En un string almacebamos la consulta de Seleccion que queremos hacer a la BD
            String searchQuery = "SELECT * FROM orderdetails OD WHERE (OD.OrderID = " + order_id + " and OD.ProductID = " + product_id + ")";
            
            //En un string tambien almacenamos la consulta de actualizacion que vamos a hacer sobre la BD 
            String updateQuery = "UPDATE orderdetails set Quantity = (Quantity + " + quantity + ") WHERE (OrderID = " + order_id + " and ProductID = " + product_id + ")";
            
            /*
                En un string tambien almacenamos la consulta de insecion pero en este caso no enviamos los datos a insertar 
                si no solo enviamos los parametros como "?" para luego enviarlos encriptados para evitar cualquier posible
                intersepcion a los datos cuando se haga el envio de los datos del programa al servidor o del servidor al la
                base de datos.
            */
            String insertQuery = "INSERT INTO orderdetails (OrderID, ProductID, UnitPrice, Quantity, Discount) Values (?,?,?,?,?)";
            
            //En un string tambien almacenamos la consulta de actualizacion que vamos a hacer sobre la BD 
            String updateProductQuery = "UPDATE Products set UnitsInStock = (UnitsInStock - " + quantity + ") WHERE ProductID = " + product_id;
            
            //El prepared statemente se engarga de hacer la consulta en la BD de parametro
            //enviamos la consulta que queremos realizar.
            prepared_statement = connetion.prepareStatement(searchQuery);
            //El ResultSet se encarga de guardar el resultado de la consulta.
            result = prepared_statement.executeQuery();

            int i = 0, j = 0;
            
            //Este if nos indica si el resultado fue nulo que se realice una 
            //consulta de insecion.
            if (!result.next()) {            
                //El prepared statemente se engarga de hacer la consulta en la BD de parametro
                //enviamos la consulta que queremos realizar.
                prepared_statement = connetion.prepareStatement(insertQuery);
                
                /*
                    Ahora es aqui donde establecemos los valores a los parametros
                    de la insercion que establecimos anterioremente estos valores
                    deben coincidir con los tipos en la base de datos para que no
                    arroje un error, para cada tipo usamos .setType (.setInt, 
                    .setDouble, .setVarchar, etc).
                
                    De parametros enviamos la columna que ocupa el dato en la tabla 
                    seguido del valor que le vamos a dar.
                */
                prepared_statement.setInt(1, order_id);
                prepared_statement.setInt(2, product_id);
                prepared_statement.setDouble(3, unit_price);
                prepared_statement.setInt(4, quantity);
                prepared_statement.setDouble(5, discount);
                i = prepared_statement.executeUpdate();
            } else {
                //Si existe un resultado de la consulta realiza una actualizacion a ese registro en la BD.
                
                //El prepared statemente se engarga de hacer la consulta en la BD de parametro
                //enviamos la consulta que queremos realizar.
                prepared_statement = connetion.prepareStatement(updateQuery);
                i = prepared_statement.executeUpdate();
            }

            if (i > 0) {
                //El prepared statemente se engarga de hacer la consulta en la BD de parametro
                //enviamos la consulta que queremos realizar.
                prepared_statement = connetion.prepareStatement(updateProductQuery);
                j = prepared_statement.executeUpdate();
                if (j > 0) {
                    JOptionPane.showMessageDialog(null, "PRODUCTO AGREGADO AL CARRITO!!");
                } else {
                    JOptionPane.showMessageDialog(null, "ERROR AL INTENTAR AGREGAR LOS DATOS!!");
                }
            } else {
                JOptionPane.showMessageDialog(null, "ERROR AL INTENTAR AGREGAR LOS DATOS!!");
            }

            //Muy importante finalizar la conexion cuando ya no estemos haciendo uso de ella.
            connetion.close();
        } catch (HeadlessException | SQLException e) {
            System.out.println("Error: " + e);
        } finally {
            try {
                connetion.close();
            } catch (SQLException e) {
                System.out.println("Error: " + e);
            }
        }
    }
    
    
    
    //Ejemplo de Seleccion de multiples registrios de la base de datos
    //Esta funcion retorna un arreglo con todos los registros que se extraen
    public static ArrayList<String> seleccionar_empleados() {
        ArrayList<String> empleados = new ArrayList<>();
        
        //Todo procedimiento de consulta a la base de datos debe ir en un try
        //El objetivo de este es evitar que el programa truene.
        try {
            //Establecemos nuestra conexion haciendo uso de la coneccion creada anteriormente
            connetion = SQLConnexion_DB.StarConnetion();
            
            //En un string almacebamos la consulta de Seleccion que queremos hacer a la BD
            String select_query = "SELECT * FROM employees";
            
            //El prepared statemente se engarga de hacer la consulta en la BD de parametro
            //enviamos la consulta que queremos realizar.
            prepared_statement = connetion.prepareStatement(select_query);
            
            //El ResultSet se encarga de guardar el resultado de la consulta.
            result = prepared_statement.executeQuery();
            System.out.println("Empleados Importados con Exito!!");
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }

        //Todo procedimiento de extraccion en la base de datos debe ir en un try
        //El objetivo de este es evitar que el programa truene.
        try {
            //En este ciclo decimos que mientras exista un resultado siguiente
            //Esto quiere decir que mientras existan filas en la tabla se continua ejecutando el cuerpo del while
            while (result.next()) {
                /*
                    Aqui dependera de la data que querramos extraer de la base de datos pero el formato sera igual
                    podemos hacer uso de .getType(NombreColumna) donde type es el tipo de dato y NombreColumna el 
                    nombre de la columna ambos deben coincidir con el nombre y tipo en la base de datos. 
                    
                    O podemos hacer uso de .getType(posicion) donde posicion es la posicion del dato que queremos
                    extraer y type el tipo de dato que extraemos, ambos deben coincidir con la base de datos.
                */
                String complete_name = result.getString("FirstName") + " " + result.getString("LastName");
                empleados.add(complete_name);
            }
            
            //Muy importante finalizar la conexion cuando ya no estemos haciendo uso de ella.
            connetion.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        } finally {
            try {
                connetion.close();
            } catch (SQLException e) {
                System.out.println("Error: " + e);
            }
        }

        return empleados;
    }
    
}
