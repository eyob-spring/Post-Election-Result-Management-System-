/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import utils.ConnectionUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author oXCToo
 */
public class HomeController implements Initializable {

    @FXML
    private Label headerLabel;

    @FXML
    private TextField valid;

    @FXML
    private Label validLabel;

    @FXML
    private TextField inValid;

    @FXML
    private Label inValidLabel;

    @FXML
    private TextField spoiled;

    @FXML
    private Label spoiledLabel;

    @FXML
    private TextField unUsed;

    @FXML
    private Label unUsedLabel;

    @FXML
    private TextField prosperity;

    @FXML
    private Label prosperityLabel;

    @FXML
    private TextField libration;

    @FXML
    private Label librationLabel;

    @FXML
    private TextField renaisense;

    @FXML
    private Label renaisenseLabel;

    @FXML
    private Button submitButton;


    @FXML
    Label lblStatus;

    @FXML
    TableView tblData;

    @FXML
    GridPane gridPane;


    ///////////////////////////////////////////////////////////////

    private static SecretKeySpec secretKey;
    private static byte[] key;

    public static void setKey(String myKey) {
        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(String strToEncrypt, String secret) {
        try {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public static String decrypt(String strToDecrypt, String secret) {
        try {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }

    /////////////////////////////////////////////////////////////
    private static int id;
    private static String Name;
    private static ArrayList<Ballot> ballots = new ArrayList<>();
    private ArrayList<Person> persons = new ArrayList<>();
    private static ArrayList<Party> parties = new ArrayList<>();
    private static Person Admin;
    private static Party winner;
    private static String errorMessage = "There seems to be an error with your input";

    private TextField party1, party2, party3;
    private static int noValidBallot = 0;
    private static int noInvalidBallot = 0;
    private static int noSpoiledBallot = 0;
    private static int noUnusedBallot = 0;


    PreparedStatement preparedStatement;
    Connection connection;

    public HomeController() {
        connection = (Connection) ConnectionUtil.conDB();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        //headerLabel.setStyle("-fx-font-weight: bold;");
    }

    @FXML
    private void HandleEvents(MouseEvent event) {
        //check if not empty
        if (valid.getText().isEmpty() || inValid.getText().isEmpty() || unUsed.getText().isEmpty() ||
                spoiled.getText().isEmpty() || prosperity.getText().isEmpty() || libration.getText().isEmpty()
                || renaisense.getText().isEmpty()) {
            lblStatus.setTextFill(Color.TOMATO);
            lblStatus.setText("ሁሉንም ቦታ ያስገቡ");
        } else {
            saveData();
        }

    }

    private void clearFields() {

    }

    private String saveData() {

        try {

            final String secretKey = "ssshhhhhhhhhhh!!!!";
            Admin = new Person();
            Admin.setName("a");
            id = 1;
            Name = "xxx";
            Party be = new Party();
            be.setName("prosperity");
            parties.add(be);

            Party che = new Party();
            che.setName("libration");
            parties.add(che);

            Party chu = new Party();
            chu.setName("hedase");
            parties.add(chu);

            for (int i = 0; i < 100; i++) {
                Ballot b = new Ballot();
                ballots.add(b);
            }

            noInvalidBallot = Integer.parseInt(inValid.getText());
            noValidBallot = Integer.parseInt(valid.getText());
            noSpoiledBallot = Integer.parseInt(spoiled.getText());
            noUnusedBallot = Integer.parseInt(unUsed.getText());
            parties.get(0).setNoVote(Integer.parseInt(prosperity.getText()));
            parties.get(1).setNoVote(Integer.parseInt(libration.getText()));
            parties.get(2).setNoVote(Integer.parseInt(renaisense.getText()));


            Statement st = connection.createStatement();

            if (ballots.size() != (noUnusedBallot + noSpoiledBallot + noValidBallot + noInvalidBallot) ||
                    ballots.size() - noUnusedBallot != noInvalidBallot + noSpoiledBallot + noValidBallot ||
                    (ballots.size() - noUnusedBallot) - noSpoiledBallot != noInvalidBallot + noValidBallot) {
                return "የመቁጠር ስህተት አለ";
            }

            Party temp = parties.get(0);
            int tempVoteCount = 0;

            for (int i = 0; i < parties.size(); i++) {
                tempVoteCount = tempVoteCount + parties.get(i).getNoVote();
                if (temp.getNoVote() < parties.get(i).getNoVote()) {
                    temp = parties.get(i);
                }
            }

            winner = temp;

            System.out.println(tempVoteCount);

            if (tempVoteCount != noValidBallot) {
                return "የትክክለኛ ድምፅ ቆጠራ ችግር አለ";
            }


            String q = "INSERT INTO `report1`(`polId`, `pollname`, `unUsedBallet`, `spoiledBallet`, `invalidBallet`, `validBallet`, `winner`) VALUES " +
                    "('" + encrypt(id + "", secretKey) + "', '" + encrypt(Name, secretKey) + "' ,'" + encrypt(noUnusedBallot + "", secretKey) +
                    "','" + encrypt(noSpoiledBallot + "", secretKey) + "','" + encrypt(noInvalidBallot + "", secretKey) + "','" +
                    encrypt(noValidBallot + "", secretKey) + "','" + encrypt(winner.getName(), secretKey) + "')";
            System.out.println(q);
            st.executeUpdate(q);

            lblStatus.setTextFill(Color.GREEN);
            lblStatus.setText("Added Successfully");

            clearFields();
            //String delete = "DELETE FROM `admins` WHERE Name ="+Admin.getName();
            //st.executeUpdate(delete);

            return "በተሳካ ሁኔታ ተጠናቋል";

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            lblStatus.setTextFill(Color.TOMATO);
            lblStatus.setText(ex.getMessage());
            return "Exception";
        }
    }
}
