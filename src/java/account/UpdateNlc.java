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
import model.NlcModel;
import support.DBHelper;
import support.Library;

/**
 *
 * @author bhaumik
 */
public class UpdateNlc extends HttpServlet {

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
        TypeToken<List<NlcModel>> token = new TypeToken<List<NlcModel>>() {
        };
        final List<NlcModel> detail = new Gson().fromJson(detailJson, token.getType());
        final ArrayList<NlcModel> data = (ArrayList<NlcModel>) detail;
        final DBHelper helper = DBHelper.GetDBHelper();
        final Connection dataConnection = helper.getConnMpAdmin();
        final Library lb = Library.getInstance();
        final JsonObject jResultObj = new JsonObject();
        if (dataConnection != null) {
            try {
                dataConnection.setAutoCommit(false);
                PreparedStatement pstUpdate1 = null;
                String sql1 = "update tag set disc_per=?,extra_support=?,backend=?,activation=?,prize_drop=? where ref_no=?";
                pstUpdate1 = dataConnection.prepareStatement(sql1);
                for (int i = 0; i < data.size(); i++) {
                    pstUpdate1.setDouble(1, data.get(i).getDisc_per());
                    pstUpdate1.setDouble(2, data.get(i).getExtra_support());
                    pstUpdate1.setDouble(3, data.get(i).getBackend());
                    pstUpdate1.setDouble(4, data.get(i).getActivation());
                    pstUpdate1.setDouble(5, data.get(i).getPrize_drop());
                    pstUpdate1.setString(6, data.get(i).getRef_no());
                    pstUpdate1.executeUpdate();
                }

                String sql = "update oldb2_1 set dr_" + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "=dr_" + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "+? where ac_CD=?";
                PreparedStatement pstUpdate = dataConnection.prepareStatement(sql);
                pstUpdate.setDouble(1, data.get(0).getCnAmount());
                pstUpdate.setString(2, data.get(0).getAc_cd());
                pstUpdate.executeUpdate();

                sql = "insert into oldb2_2 (doc_ref_no,doc_date,doc_cd,ac_cd,"
                        + "val,crdr,particular,opp_ac_cd,time_stamp,INV_NO) values(?,CURRENT_DATE,?,?,?,?,?,?,CURRENT_TIMESTAMP,?)";

                pstUpdate = dataConnection.prepareStatement(sql);
                pstUpdate.setString(1, "PD");
                pstUpdate.setString(2, "PD");
                pstUpdate.setString(3, data.get(0).getAc_cd());
                pstUpdate.setDouble(4, data.get(0).getCnAmount());
                pstUpdate.setString(5, "0");
                pstUpdate.setString(6, data.get(0).getRemark());
                pstUpdate.setString(7, "");
                pstUpdate.setString(8, "0");
                pstUpdate.executeUpdate();

                sql = "insert into prize_drop (pur_tag_no,sr_no,voucher_no,v_date,remark,prev_rate,rate,ac_cd) values (?,?,?,CURRENT_DATE,?,?,?,?)";
                pstUpdate = dataConnection.prepareStatement(sql);
                for (int i = 0; i < data.size(); i++) {
                    pstUpdate.setString(1, data.get(i).getRef_no());
                    pstUpdate.setInt(2, i + 1);
                    pstUpdate.setString(3, lb.generateKey(dataConnection, "prize_drop", "voucher_no", "PZ", 7));
                    pstUpdate.setString(4, data.get(i).getRemark());
                    pstUpdate.setDouble(5, 0.00);
                    pstUpdate.setDouble(6, data.get(i).getExtra_support() + data.get(i).getBackend() + data.get(i).getBackend() + data.get(i).getActivation() + data.get(i).getPrize_drop());
                    pstUpdate.setString(7, data.get(i).getAc_cd());
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
