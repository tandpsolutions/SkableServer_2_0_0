/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package account;

import com.google.gson.JsonArray;
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
import support.DBHelper;

/**
 *
 * @author bhaumik
 */
public class SalesReportAccount extends HttpServlet {

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

        final String from_date = request.getParameter("from_date");
        final String to_date = request.getParameter("to_date");
        final String ac_cd = request.getParameter("ac_cd");
        final DBHelper helper = DBHelper.GetDBHelper();
        final Connection dataConnection = helper.getConnMpAdmin();
        final JsonObject jResultObj = new JsonObject();
        final String sr_cd = request.getParameter("sr_cd");
        final String type_cd = request.getParameter("type_cd");
        final String brand_cd = request.getParameter("brnad_cd");
        final String model_cd = request.getParameter("model_cd");
        final String branch_cd = request.getParameter("branch_cd");
        if (dataConnection != null) {
            try {

                PreparedStatement pstLocal = null;
                String sql = "SELECT v.v_date,v.V_TYPE,v.INV_NO,b.BRAND_NAME,m.MODEL_NAME,s.SR_NAME,v1.MRP,v.INS_AMT,v.branch_cd,v1.TAG_NO,"
                        + " (SELECT SUM(sale_rate) FROM tag WHERE tag_no=v1.tag_no) as rate  FROM vilshd v "
                        + " LEFT JOIN vilsdt v1 ON v.REF_NO=v1.REF_NO LEFT JOIN seriesmst s ON v1.SR_CD=s.SR_CD "
                        + " LEFT JOIN acntmst a ON a.ac_cd=v.AC_CD LEFT JOIN modelmst m ON s.MODEL_CD=m.MODEL_CD "
                        + " LEFT JOIN brandmst b ON b.BRAND_CD=m.BRAND_CD WHERE v.IS_DEL=0 and s.sr_cd not in('S000050','S000047') "
                        + " and v.v_date>='" + from_date + "' "
                        + " and v.v_date<='" + to_date + "'  ";
                if (!type_cd.equalsIgnoreCase("")) {
                    sql += " and m.type_cd='" + type_cd + "' ";
                }
                if (!sr_cd.equalsIgnoreCase("")) {
                    sql += " and s.SR_CD='" + sr_cd + "'";
                } else if (!brand_cd.equalsIgnoreCase("")) {
                    sql += " and m.brand_cd='" + brand_cd + "'";
                } else if (!model_cd.equalsIgnoreCase("")) {
                    sql += " and s.model_cd='" + model_cd + "'";
                }
                if (!ac_cd.equalsIgnoreCase("")) {
                    sql += " and v.ac_cd='" + ac_cd + "'";
                }
                if (!branch_cd.equalsIgnoreCase("0")) {
                    sql += " and v.branch_cd=" + branch_cd;
                }
                sql += " order by v.branch_cd";
                pstLocal = dataConnection.prepareStatement(sql);
                ResultSet viewDataRs = pstLocal.executeQuery();

                JsonArray array = new JsonArray();
                while (viewDataRs.next()) {
                    JsonObject object = new JsonObject();
                    object.addProperty("INV_NO", (viewDataRs.getString("inv_no") == null) ? "" : viewDataRs.getString("inv_no"));
                    object.addProperty("V_TYPE", (viewDataRs.getString("v_type") == null) ? "" : viewDataRs.getString("v_type"));
                    object.addProperty("BRAND_NAME", (viewDataRs.getString("BRAND_NAME") == null) ? "" : viewDataRs.getString("BRAND_NAME"));
                    object.addProperty("MODEL_NAME", (viewDataRs.getString("MODEL_NAME") == null) ? "" : viewDataRs.getString("MODEL_NAME"));
                    object.addProperty("SR_NAME", (viewDataRs.getString("sr_name") == null) ? "" : viewDataRs.getString("sr_name"));
                    object.addProperty("MRP", (viewDataRs.getString("rate") == null) ? 0.00 : viewDataRs.getDouble("rate"));
                    object.addProperty("INS_AMT", (viewDataRs.getString("INS_AMT") == null) ? 0.00 : viewDataRs.getDouble("INS_AMT"));
                    object.addProperty("V_DATE", (viewDataRs.getString("v_date") == null) ? "" : viewDataRs.getString("v_date"));
                    object.addProperty("TAG_NO", (viewDataRs.getString("TAG_NO") == null) ? "" : viewDataRs.getString("TAG_NO"));
                    object.addProperty("branch_cd", (viewDataRs.getString("branch_cd") == null) ? "" : viewDataRs.getString("branch_cd"));
                    array.add(object);
                }

                jResultObj.addProperty("result", 1);
                jResultObj.addProperty("Cause", "success");
                jResultObj.add("data", array);
            } catch (SQLNonTransientConnectionException ex1) {
                jResultObj.addProperty("result", -1);
                jResultObj.addProperty("Cause", "Server is down");
            } catch (SQLException ex) {
                jResultObj.addProperty("result", -1);
                jResultObj.addProperty("Cause", ex.getMessage());
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
