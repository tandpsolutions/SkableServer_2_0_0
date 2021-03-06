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
import support.Library;

/**
 *
 * @author bhaumik
 */
public class GenralLedger extends HttpServlet {

    String ac_cd = "";
    String from_date = "";
    String to_date = "";
    Connection dataConnection = null;
    Library lb = Library.getInstance();
    ResultSet viewDataRS;

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
        DBHelper helper = DBHelper.GetDBHelper();
        dataConnection = helper.getConnMpAdmin();
        final JsonObject jResultObj = new JsonObject();
        ac_cd = request.getParameter("ac_cd");
        from_date = request.getParameter("from_date");
        to_date = request.getParameter("to_date");
        try {
            if (dataConnection != null) {
                double opb = 0.00;
                String sql = "(select (select case when sum(val) is null then 0 else sum(val)end  from OLDB2_2 where AC_CD='" + ac_cd + "' and DOC_DATE<'" + from_date + "' and CRDR=0) -"
                        + "(select case when sum(val) is null then 0 else sum(val)end  from OLDB2_2 where AC_CD='" + ac_cd + "' "
                        + "and DOC_DATE<'" + from_date + "' and CRDR=1)  as opb from dual) ";
                PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                viewDataRS = pstLocal.executeQuery();
                if (viewDataRS.next()) {
                    jResultObj.addProperty("opb", viewDataRS.getDouble("opb"));
                    opb = viewDataRS.getDouble("opb");
                } else {
                    jResultObj.addProperty("opb", 0.00);
                }

                makeQuery();
                double opbRs = 0.00;
                JsonArray array = new JsonArray();
                while (viewDataRS.next()) {
                    JsonObject object = new JsonObject();
                    object.addProperty("FNAME", viewDataRS.getString("FNAME"));
                    if (viewDataRS.getString("DOC_REF_NO").startsWith("05")) {
                        object.addProperty("DOC_REF_NO", viewDataRS.getString("BILL_NO"));
                    } else if (viewDataRS.getString("DOC_REF_NO").startsWith("02")) {
                        object.addProperty("DOC_REF_NO", viewDataRS.getString("INV_NO"));
                    } else if (viewDataRS.getString("INV_NO").equalsIgnoreCase("0")) {
                        object.addProperty("DOC_REF_NO", viewDataRS.getString("DOC_REF_NO"));
                    } else {
                        object.addProperty("DOC_REF_NO", viewDataRS.getString("INV_NO"));
                    }
                    object.addProperty("DOC_DATE", viewDataRS.getString("DOC_DATE"));
                    object.addProperty("DOC_CD", viewDataRS.getString("DOC_CD"));
                    object.addProperty("DR", lb.roundOffDoubleValue(viewDataRS.getDouble("DR")));
                    object.addProperty("CR", lb.roundOffDoubleValue(viewDataRS.getDouble("CR")));
                    opbRs += viewDataRS.getDouble("DR") - viewDataRS.getDouble("CR");
                    object.addProperty("BAL", lb.roundOffDoubleValue(opbRs));
                    object.addProperty("PARTICULAR", viewDataRS.getString("PARTICULAR"));
                    object.addProperty("OPP_AC_CD", viewDataRS.getString("OPP_AC_CD"));
                    object.addProperty("REF_NO", viewDataRS.getString("doc_ref_no"));
                    object.addProperty("OPB", opb);
                    array.add(object);
                }

                jResultObj.addProperty("opb", opb);
                jResultObj.addProperty("result", 1);
                jResultObj.addProperty("Cause", "success");
                jResultObj.add("data", array);
            }
        } catch (SQLNonTransientConnectionException ex1) {
            jResultObj.addProperty("result", -1);
            jResultObj.addProperty("Cause", "Server is down");
        } catch (SQLException ex) {
            jResultObj.addProperty("result", -1);
            jResultObj.addProperty("Cause", ex.getMessage());
        } finally {
            lb.closeConnection(dataConnection);
        }
        response.getWriter().print(jResultObj);
    }

    private void makeQuery() throws SQLException {
        try {
            String sql = "select a.fname,l.BILL_NO,o.inv_no, doc_ref_no,DOC_DATE,DOC_CD,OPP_AC_CD,o.AC_CD, case when CRDR = 0 then VAL else  0 end as dr,"
                    + " case when CRDR = 1 then val else  0 end  as Cr,PARTICULAR "
                    + " from OLDB2_2 o left join acntmst a on o.opp_ac_cd=a.ac_cd left join lbrphd l on o.doc_ref_no=l.ref_no "
                    + " WHERE o.AC_CD='" + ac_cd + "'"
                    + " and doc_date >='" + from_date + "' "
                    + " and doc_date <='" + to_date + "' and val<> 0 order by doc_date,doc_ref_no";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            viewDataRS = pstLocal.executeQuery();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
