package tcrs;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.sql.SQLException;

//Controller class for login screen
public class AuthController {

//	Login screen elements
    @FXML TextField usernameField;
    @FXML TextField passwordField;
    @FXML Label loginValidation;

//    Application user class
    protected static User user;
    
//    Login button action that lead to application page based on user type
    @FXML
    private void handleLoginButtonAction() throws IOException, SQLException {

        AuthModel authModel = new AuthModel();

        user = new User(usernameField.getText(), passwordField.getText());
        user = authModel.AuthenticateUser();
        
//        Check user validity
        if (user.isValid) {
//        	Launch application regular page
            if (user.getType().toUpperCase().equals("LOCAL")) {
                App.setRoot("DashboardLocal");
//            	Launch application admin page
            }else if (user.getType().toUpperCase().equals("ADMIN")) {
                App.setRoot("DashboardAdmin");
            }
//            Login error message
        } else {
            loginValidation.setTextFill(Color.RED);
            loginValidation.setVisible(true);
        }
    }
}
