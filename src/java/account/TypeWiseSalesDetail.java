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
public class TypeWiseSalesDetail extends HttpServlet {

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
                String sql = "SELECT v.branch_cd,v.ref_no,a.fname,s.sr_name,v.v_date,t.TYPE_NAME,b.brand_name,"
                        + " CASE WHEN v1.IMEI_NO ='' THEN v1.SERAIL_NO ELSE v1.IMEI_NO END AS IMEI_NO,(v1.qty) AS pcs,(v1.RATE) AS tot_sales,"
                        + " t1.ac_name AS pur_party,MOBILE1,email  FROM VILSHD v LEFT JOIN vilsdt v1 ON v.REF_NO=v1.REF_NO "
                        + " LEFT JOIN SERIESMST s ON v1.SR_CD=s.SR_CD LEFT JOIN acntmst a ON v.ac_cd=a.ac_cd "
                        + " LEFT JOIN MODELMST m ON s.MODEL_CD=m.MODEL_CD LEFT JOIN TYPEMST t ON m.TYPE_CD=t.TYPE_CD"
                        + " left join  brandmst b on b.brand_cd=m.brand_cd left join phbkmst p1 on p1.ac_cd=a.ac_cd"
                        + " LEFT JOIN tag t1 ON t1.ref_no=v1.pur_tag_no  WHERE v.IS_DEL=0 "
                        + " and v.v_date>='" + from_date + "' "
                        + " and v.v_date<='" + to_date + "'";
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
                    object.addProperty("fname", viewDataRs.getString("fname"));
                    object.addProperty("sr_name", viewDataRs.getString("sr_name"));
                    object.addProperty("v_date", viewDataRs.getString("v_date"));
                    object.addProperty("type_name", viewDataRs.getString("type_name"));
                    object.addProperty("pcs", viewDataRs.getInt("pcs"));
                    object.addProperty("tot_sales", viewDataRs.getDouble("tot_sales"));
                    object.addProperty("IMEI_NO", viewDataRs.getString("IMEI_NO"));
                    object.addProperty("brand_name", viewDataRs.getString("brand_name"));
                    object.addProperty("PUR_PARTY", viewDataRs.getString("PUR_PARTY"));
                    object.addProperty("REF_NO", viewDataRs.getString("REF_NO"));
                    object.addProperty("MOBILE1", (viewDataRs.getString("MOBILE1") == null) ? "" : viewDataRs.getString("MOBILE1"));
                    object.addProperty("EMAIL", (viewDataRs.getString("EMAIL") == null) ? "" : viewDataRs.getString("EMAIL"));
                    object.addProperty("branch_cd", viewDataRs.getString("branch_cd"));
                    array.add(object);
                }

                sql = "select  v.branch_cd,v.ref_no,a.fname,s.sr_name,v.v_date,t.TYPE_NAME,b.brand_name"
                        + " ,case when IMEI_NO ='' then SERAIL_NO else IMEI_NO end as IMEI_NO,(v1.qty) as pcs,(v1.RATE) as tot_sales"
                        + " ,MOBILE1,email  from srhd v left join srdt v1 on"
                        + " v.REF_NO=v1.REF_NO "
                        + " left join SERIESMST s on v1.SR_CD=s.SR_CD left join acntmst a on v.ac_cd=a.ac_cd "
                        + " left join MODELMST m on s.MODEL_CD=m.MODEL_CD left join brandmst b on b.brand_cd=m.brand_cd"
                        + " left join TYPEMST t on m.TYPE_CD=t.TYPE_CD left join phbkmst p1 on p1.ac_cd=a.ac_cd where v.IS_DEL=0 "
                        + " and v.v_date>='" + from_date + "' "
                        + " and v.v_date<='" + to_date + "'";
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

                pstLocal = dataConnection.prepareStatement(sql);
                viewDataRs = pstLocal.executeQuery();
                while (viewDataRs.next()) {
                    JsonObject object = new JsonObject();
                    object.addProperty("fname", viewDataRs.getString("fname"));
                    object.addProperty("sr_name", viewDataRs.getString("sr_name"));
                    object.addProperty("brand_name", viewDataRs.getString("brand_name"));
                    object.addProperty("v_date", viewDataRs.getString("v_date"));
                    object.addProperty("type_name", viewDataRs.getString("type_name"));
                    object.addProperty("pcs", viewDataRs.getInt("pcs") * -1);
                    object.addProperty("tot_sales", viewDataRs.getDouble("tot_sales") * -1);
                    object.addProperty("IMEI_NO", viewDataRs.getString("IMEI_NO"));
                    object.addProperty("REF_NO", viewDataRs.getString("REF_NO"));
                    object.addProperty("MOBILE1", (viewDataRs.getString("MOBILE1") == null) ? "" : viewDataRs.getString("MOBILE1"));
                    object.addProperty("EMAIL", (viewDataRs.getString("EMAIL") == null) ? "" : viewDataRs.getString("EMAIL"));
                    object.addProperty("PUR_PARTY", "");
                    object.addProperty("branch_cd", viewDataRs.getString("branch_cd"));
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
