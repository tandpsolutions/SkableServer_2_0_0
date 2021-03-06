/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package account;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import support.DBHelper;
import support.Library;

/**
 *
 * @author bhaumik
 */
public class SetReconsilationDate extends HttpServlet {

    DBHelper helper = DBHelper.GetDBHelper();
    Library lb = Library.getInstance();

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        final String rec_date = request.getParameter("rec_date");
        final String ref_no = (request.getParameter("ref_no"));

        final JsonObject jResultObj = new JsonObject();
        Connection dataConnection = null;
        if (dataConnection == null) {
            dataConnection = helper.getConnMpAdmin();
        }
        if (dataConnection != null) {
            try {
                dataConnection.setAutoCommit(false);

                String sql = "";
                PreparedStatement pstLocal = null;
                if (ref_no.startsWith("BP") || ref_no.startsWith("BR")) {
                    sql = "update oldb2_2 set rec_date=? where doc_ref_no=?";
                    pstLocal = dataConnection.prepareStatement(sql);
                    if (rec_date.equalsIgnoreCase("")) {
                        pstLocal.setString(1, null);
                    } else {
                        pstLocal.setString(1, rec_date);
                    }
                    pstLocal.setString(2, ref_no);
                    pstLocal.executeUpdate();

                    sql = "update bprhd set rec_date=? where ref_no=?";
                    pstLocal = dataConnection.prepareStatement(sql);
                    if (rec_date.equalsIgnoreCase("")) {
                        pstLocal.setString(1, null);
                    } else {
                        pstLocal.setString(1, rec_date);
                    }
                    pstLocal.setString(2, ref_no);
                    pstLocal.executeUpdate();
                }
                if (ref_no.startsWith("02")) {
                    sql = "update oldb2_2 set rec_date=? where doc_ref_no=?";
                    pstLocal = dataConnection.prepareStatement(sql);
                    if (rec_date.equalsIgnoreCase("")) {
                        pstLocal.setString(1, null);
                    } else {
                        pstLocal.setString(1, rec_date);
                    }
                    pstLocal.setString(2, ref_no);
                    pstLocal.executeUpdate();

                    sql = "update vilshd set rec_date=? where ref_no=?";
                    pstLocal = dataConnection.prepareStatement(sql);
                    if (rec_date.equalsIgnoreCase("")) {
                        pstLocal.setString(1, null);
                    } else {
                        pstLocal.setString(1, rec_date);
                    }
                    pstLocal.setString(2, ref_no);
                    pstLocal.executeUpdate();
                }
                if (ref_no.startsWith("CV")) {
                    sql = "update oldb2_2 set rec_date=? where doc_ref_no=?";
                    pstLocal = dataConnection.prepareStatement(sql);
                    if (rec_date.equalsIgnoreCase("")) {
                        pstLocal.setString(1, null);
                    } else {
                        pstLocal.setString(1, rec_date);
                    }
                    pstLocal.setString(2, ref_no);
                    pstLocal.executeUpdate();

                    sql = "update contrahd set rec_date=? where ref_no=?";
                    pstLocal = dataConnection.prepareStatement(sql);
                    if (rec_date.equalsIgnoreCase("")) {
                        pstLocal.setString(1, null);
                    } else {
                        pstLocal.setString(1, rec_date);
                    }
                    pstLocal.setString(2, ref_no);
                    pstLocal.executeUpdate();
                }
                
                if (ref_no.startsWith("JV")) {
                    sql = "update oldb2_2 set rec_date=? where doc_ref_no=?";
                    pstLocal = dataConnection.prepareStatement(sql);
                    if (rec_date.equalsIgnoreCase("")) {
                        pstLocal.setString(1, null);
                    } else {
                        pstLocal.setString(1, rec_date);
                    }
                    pstLocal.setString(2, ref_no);
                    pstLocal.executeUpdate();

                    sql = "update jvhd set rec_date=? where ref_no=?";
                    pstLocal = dataConnection.prepareStatement(sql);
                    if (rec_date.equalsIgnoreCase("")) {
                        pstLocal.setString(1, null);
                    } else {
                        pstLocal.setString(1, rec_date);
                    }
                    pstLocal.setString(2, ref_no);
                    pstLocal.executeUpdate();
                }
                dataConnection.commit();
                dataConnection.setAutoCommit(true);
                jResultObj.addProperty("result", 1);
                jResultObj.addProperty("Cause", "success");
            } catch (SQLNonTransientConnectionException ex1) {
                ex1.printStackTrace();
                jResultObj.addProperty("result", -1);
                jResultObj.addProperty("Cause", "Server is down");
            } catch (SQLException ex) {
                ex.printStackTrace();
                jResultObj.addProperty("result", -1);
                jResultObj.addProperty("Cause", ex.getMessage());
                try {
                    dataConnection.rollback();
                    dataConnection.setAutoCommit(true);
                } catch (Exception e) {
                }
            } finally {
                lb.closeConnection(dataConnection);
            }
        }
        response.getWriter().print(jResultObj);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
