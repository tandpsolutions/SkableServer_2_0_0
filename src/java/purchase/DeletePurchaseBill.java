/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package purchase;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import oldbUpdate.PurchaseBillUpdate;
import support.DBHelper;
import support.Library;

/**
 *
 * @author bhaumik
 */
public class DeletePurchaseBill extends HttpServlet {

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
        final String ref_no = request.getParameter("ref_no");
        response.getWriter().print(saveVoucher(ref_no));

    }

    private JsonObject saveVoucher(String ref_no) {
        final JsonObject jResultObj = new JsonObject();
        Connection dataConnection = null;
        if (dataConnection == null) {
            dataConnection = helper.getConnMpAdmin();
        }
        if (dataConnection != null) {
            try {
                int inv_no = 0;
                dataConnection.setAutoCommit(false);
                String sql = "";
                sql = "select tag_no from tag where REF_NO in (select PUR_TAG_NO from LBRPDT where REF_NO ='" + ref_no + "') and is_del= 1";
                PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                ResultSet rsLocal = pstLocal.executeQuery();
//                if (rsLocal.next()) {
//                    jResultObj.addProperty("result", 0);
//                    jResultObj.addProperty("Cause", "Tag use in sales process");
//                } else {
                    sql = "update lbrphd set is_del = 1 where ref_no='" + ref_no + "'";
                    pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.executeUpdate();

                    new PurchaseBillUpdate().deleteEntry(dataConnection, ref_no);

                    sql = "delete from tag where REF_NO in (select PUR_TAG_NO from LBRPDT where REF_NO ='" + ref_no + "') ";
                    pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.executeUpdate();

                    dataConnection.commit();
                    dataConnection.setAutoCommit(true);
                    jResultObj.addProperty("result", 1);
                    jResultObj.addProperty("Cause", "success");
//                }
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
            }
        }
        return jResultObj;
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
