import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;

/**
 * The Main class serves as the entry point for starting the Monopoly game.
 * It creates an instance of the game with custom settings and initializes the user interface.
 */

public class Main {
    public static void main(String[] args) {
        GUI2 gui = new GUI2(true);
        MainMenu menu = new MainMenu(gui);
    }
}
