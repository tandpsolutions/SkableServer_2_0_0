/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package account;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.PrizeDrop;
import support.DBHelper;
import support.Library;

/**
 *
 * @author bhaumik
 */
public class UpdatePrize extends HttpServlet {

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

        final String detailJson = request.getParameter("detail");
        TypeToken<List<PrizeDrop>> token = new TypeToken<List<PrizeDrop>>() {
        };
        final List<PrizeDrop> detail = new Gson().fromJson(detailJson, token.getType());
        final ArrayList<PrizeDrop> data = (ArrayList<PrizeDrop>) detail;
        final DBHelper helper = DBHelper.GetDBHelper();
        final Connection dataConnection = helper.getConnMpAdmin();
        final Library lb = Library.getInstance();
        final JsonObject jResultObj = new JsonObject();
        if (dataConnection != null) {
            try {
                dataConnection.setAutoCommit(false);
                PreparedStatement pstLocal = null;
                PreparedStatement pstLocal1 = null;
                String sql = "insert into prize_drop (pur_tag_no,sr_no,voucher_no,v_date,remark,prev_rate,rate,ac_cd) values (?,?,?,CURRENT_DATE,?,?,?,?)";
                String sql1 = "update tag set pur_rate=pur_rate-? where ref_no=?";
                pstLocal = dataConnection.prepareStatement(sql);
                pstLocal1 = dataConnection.prepareStatement(sql1);
                for (int i = 0; i < data.size(); i++) {
                    pstLocal.setString(1, data.get(i).getPur_tag_no());
                    pstLocal.setInt(2, i + 1);
                    if (data.get(i).getVoucher_no().equalsIgnoreCase("")) {
                        pstLocal.setString(3, lb.generateKey(dataConnection, "prize_drop", "voucher_no", "PZ", 7));
                    } else {
                        pstLocal.setString(3, data.get(i).getVoucher_no());
                    }
                    pstLocal.setString(4, data.get(i).getRemark());
                    pstLocal.setDouble(5, data.get(i).getPrevious_rate());
                    pstLocal.setDouble(6, data.get(i).getRate());
                    pstLocal.setString(7, data.get(i).getAc_cd());
                    pstLocal.executeUpdate();

                    pstLocal1.setDouble(1, data.get(i).getRate());
                    pstLocal1.setString(2, data.get(i).getPur_tag_no());
                    pstLocal1.executeUpdate();

                    sql = "update oldb2_1 set dr_" + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "=dr_" + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "+? where ac_CD=?";
                    PreparedStatement pstUpdate = dataConnection.prepareStatement(sql);
                    pstUpdate.setDouble(1, data.get(i).getRate());
                    pstUpdate.setString(2, data.get(i).getAc_cd());
                    pstUpdate.executeUpdate();

                    sql = "insert into oldb2_2 (doc_ref_no,doc_date,doc_cd,ac_cd,"
                            + "val,crdr,particular,opp_ac_cd,time_stamp,INV_NO) values(?,CURRENT_DATE,?,?,?,?,?,?,CURRENT_TIMESTAMP,?)";

                    pstUpdate = dataConnection.prepareStatement(sql);
                    pstUpdate.setString(1, "PD");
                    pstUpdate.setString(2, "PD");
                    pstUpdate.setString(3, data.get(i).getAc_cd());
                    pstUpdate.setDouble(4, data.get(i).getRate());
                    pstUpdate.setString(5, "0");
                    pstUpdate.setString(6, data.get(i).getRemark());
                    pstUpdate.setString(7, "");
                    pstUpdate.setString(8, "0");
                    pstUpdate.executeUpdate();
                }
                dataConnection.commit();
                dataConnection.setAutoCommit(true);
                jResultObj.addProperty("result", 1);
                jResultObj.addProperty("Cause", "success");
            } catch (SQLNonTransientConnectionException ex1) {
                jResultObj.addProperty("result", -1);
                jResultObj.addProperty("Cause", "Server is down");
                try {
                    dataConnection.rollback();
                    dataConnection.setAutoCommit(true);
                } catch (SQLException ex) {
                    Logger.getLogger(UpdatePrize.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                jResultObj.addProperty("result", -1);
                jResultObj.addProperty("Cause", ex.getMessage());
                try {
                    dataConnection.rollback();
                    dataConnection.setAutoCommit(true);
                } catch (SQLException ex2) {
                    Logger.getLogger(UpdatePrize.class.getName()).log(Level.SEVERE, null, ex2);
                }
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
