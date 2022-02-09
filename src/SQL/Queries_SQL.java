/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SQL;

import java.awt.HeadlessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author Bernardo Jose
 */
public class Queries_SQL {

    private static Connection connetion;
    private static PreparedStatement prepared_statement;
    private static Statement statement;
    private static ResultSet result;
    DefaultTableModel ModeloTabla;

    public static int lastOrder() {
        int LastOrder = 0;
        try {
            connetion = SQLConnexion_DB.StarConnetion();
            String select_query = "SELECT OrderID FROM Orders ORDER BY OrderID DESC LIMIT 1";
            prepared_statement = connetion.prepareStatement(select_query);
            result = prepared_statement.executeQuery();

            if (result.next()) {
                LastOrder = result.getInt(1);
            }
            System.out.println("Orden Importada con Exito!!" + LastOrder);

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

    public static int insertar_datos_orden(String custumer_id, int employee_id, int ship_via, double freight, String ship_name,
            String ship_adress, String ship_city, String ship_region, String ship_postal_code, String ship_country) {
        int idOrdenInserted = 0;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime today = LocalDateTime.now();
            LocalDateTime tomorrow = today.plusDays(2);

            Object order_date = today.format(formatter);
            Object required_date = tomorrow.format(formatter);
            Object shipped_date = null;

            connetion = SQLConnexion_DB.StarConnetion();
            String insertQuery = "INSERT INTO ORDERS (CustomerID,EmployeeID,OrderDate,RequiredDate,ShippedDate,ShipVia,Freight,ShipName,ShipAddress,ShipCity,ShipRegion,ShipPostalCode,ShipCountry) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
            prepared_statement = connetion.prepareStatement(insertQuery);
            prepared_statement.setString(1, custumer_id);
            prepared_statement.setInt(2, employee_id);
            prepared_statement.setObject(3, order_date);
            prepared_statement.setObject(4, required_date);
            prepared_statement.setObject(5, shipped_date);
            prepared_statement.setInt(6, ship_via);
            prepared_statement.setDouble(7, freight);
            prepared_statement.setString(8, ship_name);
            prepared_statement.setString(9, ship_adress);
            prepared_statement.setString(10, ship_city);
            prepared_statement.setString(11, ship_region);
            prepared_statement.setString(12, ship_postal_code);
            prepared_statement.setString(13, ship_country);
            int i = prepared_statement.executeUpdate();

            if (i > 0) {
                JOptionPane.showMessageDialog(null, "ORDEN AGREGADA!!");
                idOrdenInserted = lastOrder();
            } else {
                JOptionPane.showMessageDialog(null, "ERROR AL INTENTAR AGREGAR LOS DATOS!!");
            }

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

        return idOrdenInserted;
    }

    public static void insertar_datos_orden_detail(int order_id, int product_id, double unit_price, int quantity, double discount) {
        try {

            connetion = SQLConnexion_DB.StarConnetion();
            String searchQuery = "SELECT * FROM orderdetails OD WHERE (OD.OrderID = " + order_id + " and OD.ProductID = " + product_id + ")";
            String updateQuery = "UPDATE orderdetails set Quantity = (Quantity + " + quantity + ") WHERE (OrderID = " + order_id + " and ProductID = " + product_id + ")";
            String insertQuery = "INSERT INTO orderdetails (OrderID, ProductID, UnitPrice, Quantity, Discount) Values (?,?,?,?,?)";
            String updateProductQuery = "UPDATE Products set UnitsInStock = (UnitsInStock - " + quantity + ") WHERE ProductID = " + product_id;

            prepared_statement = connetion.prepareStatement(searchQuery);
            result = prepared_statement.executeQuery();

            int i = 0, j = 0;
            if (!result.next()) {
                prepared_statement = connetion.prepareStatement(insertQuery);
                prepared_statement.setInt(1, order_id);
                prepared_statement.setInt(2, product_id);
                prepared_statement.setDouble(3, unit_price);
                prepared_statement.setInt(4, quantity);
                prepared_statement.setDouble(5, discount);
                i = prepared_statement.executeUpdate();
            } else {
                prepared_statement = connetion.prepareStatement(updateQuery);
                i = prepared_statement.executeUpdate();
            }

            if (i > 0) {
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

    public static ArrayList<String> seleccionar_empleados() {
        ArrayList<String> empleados = new ArrayList<>();
        try {
            connetion = SQLConnexion_DB.StarConnetion();
            String select_query = "SELECT * FROM employees";
            prepared_statement = connetion.prepareStatement(select_query);
            result = prepared_statement.executeQuery();
            System.out.println("Empleados Importados con Exito!!");
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }

        try {
            while (result.next()) {
                String complete_name = result.getString("FirstName") + " " + result.getString("LastName");
                empleados.add(complete_name);
            }
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

    public static ArrayList<String> seleccionar_shippers() {
        ArrayList<String> agencias = new ArrayList<>();
        try {
            connetion = SQLConnexion_DB.StarConnetion();
            String select_query = "SELECT * FROM shippers";
            prepared_statement = connetion.prepareStatement(select_query);
            result = prepared_statement.executeQuery();
            System.out.println("Agencias Importadas con Exito!!");
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }

        try {
            while (result.next()) {
                agencias.add(result.getString("CompanyName"));
            }
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

        return agencias;
    }

    public static ArrayList<String> seleccionar_customers() {
        ArrayList<String> clientes = new ArrayList<>();
        try {
            connetion = SQLConnexion_DB.StarConnetion();
            String select_query = "SELECT * FROM Customers";
            prepared_statement = connetion.prepareStatement(select_query);
            result = prepared_statement.executeQuery();
            System.out.println("Clientes Importados con Exito!!");
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }

        try {
            while (result.next()) {
                clientes.add(result.getString("CompanyName"));
            }
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

        return clientes;
    }

    public static int obtener_codigo_empleado(String complete_name) {
        int code = 0;
        String[] NombreSeparado = complete_name.split("\\s+");
        try {
            connetion = SQLConnexion_DB.StarConnetion();
            //String select_query = "SELECT * FROM employees E WHERE (E.FirstName + ' ' + E.LastName) = ?";
            String select_query = "SELECT * FROM employees E WHERE E.FirstName = '" + NombreSeparado[0] + "' AND E.LastName = '" + NombreSeparado[1] + "'";
            prepared_statement = connetion.prepareStatement(select_query);
            //prepared_statement.setString(1, complete_name);
            result = prepared_statement.executeQuery();

            if (result.next()) {
                code = result.getInt("EmployeeID");
            }
            System.out.println("Empleado Importado con Exito!!");

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

        return code;
    }

    public static int obtener_codigo_shipper(String name) {
        int code = 0;
        try {
            connetion = SQLConnexion_DB.StarConnetion();
            String select_query = "SELECT * FROM shippers E WHERE CompanyName = ?";
            prepared_statement = connetion.prepareStatement(select_query);
            prepared_statement.setString(1, name);
            result = prepared_statement.executeQuery();

            if (result.next()) {
                code = result.getInt("ShipperID");
            }
            System.out.println("Agencia Importada con Exito!!");

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

        return code;
    }

    public static String obtener_codigo_customer(String name) {
        String code = "";
        try {
            connetion = SQLConnexion_DB.StarConnetion();
            String select_query = "SELECT * FROM Customers WHERE CompanyName = ?";
            prepared_statement = connetion.prepareStatement(select_query);
            prepared_statement.setString(1, name);
            result = prepared_statement.executeQuery();

            if (result.next()) {
                code = result.getString("CustomerID") + "";
            }
            System.out.println("Cliente Importado con Exito!!");

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

        return code;
    }

    public void BuscarProducto(String valor, String filtro, JTable tabla) {
        String[] columnas = {"ID", "Nombre", "Precio Unitario", "Stock"};
        String[] productos = new String[4];

        ModeloTabla = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int colunm) {
                return false;
            }
        };

        String query = "";

        if (filtro.equals("Nombre")) {
            query = "SELECT ProductID, ProductName, UnitPrice, UnitsInStock FROM Products WHERE ProductName LIKE '%" + valor + "%'";
        } else if (filtro.equals("ID")) {
            query = "SELECT ProductID, ProductName, UnitPrice, UnitsInStock FROM Products WHERE ProductID LIKE '%" + valor + "%'";
        }

        try {
            connetion = SQLConnexion_DB.StarConnetion();
            prepared_statement = connetion.prepareStatement(query);
            result = prepared_statement.executeQuery();

            while (result.next()) {
                productos[0] = result.getString("ProductID");
                productos[1] = result.getString("ProductName");
                productos[2] = result.getString("UnitPrice");
                productos[3] = result.getString("UnitsInStock");

                ModeloTabla.addRow(productos);
            }

            tabla.setModel(ModeloTabla);

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
    }
}
