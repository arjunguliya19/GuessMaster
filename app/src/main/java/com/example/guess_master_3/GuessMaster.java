package com.example.guess_master_3;
// Author: Arjun Guliya

// Importing all the required libraries
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.widget.* ;
import java.util.Random;
import android.content.DialogInterface;
import android.widget.Toast;
import java.text.SimpleDateFormat;

public class GuessMaster extends AppCompatActivity {
    // Defining View components
    private TextView entityName;
    private TextView ticketSum;
    private Button guessButton;
    private EditText userIn;
    private Button btnclearContent;
    private ImageView entityImage;
    String answer;

    // Declaring and initializing instance variables
    private int numOfEntities, totalTicketNum = 0;
    private Entity[] entities;
    private Entity entityInUse; // Global instance variable that represents the current entity being used by the game

    // Constructor
    public GuessMaster() {
        numOfEntities = 0;
        entities = new Entity[10];
    }

    // Method to start the game and show the user the welcome message
    private void welcomeToGame(Entity entity) {
        AlertDialog.Builder welcomeAlert = new AlertDialog.Builder(GuessMaster.this);
        welcomeAlert.setTitle("GuessMaster");
        welcomeAlert.setMessage(entity.welcomeMessage());
        welcomeAlert.setCancelable(false); //No Cancel Button
        welcomeAlert.setNegativeButton("START GAME", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getBaseContext(), "Game is Starting... Enjoy", Toast.LENGTH_SHORT).show();
                entityName.setText(entity.getName());
                ImageSetter(entity.getName());
            }
        });
        // Show Dialog
        AlertDialog dialog = welcomeAlert.create();
        dialog.show();
    }

    // playGame method that starts each round
    public void playGame() {
        // Takes the current entity and calls the other playGame method that uses the given entity to start a round
        // Crucial as if the user makes an error they need to continue from the same entity, so a new round is started with the same entity
        playGame(entityInUse);
    }

    // main playGame method
    public void playGame(Entity entity) {
        // Name of the entity to be guessed in the entityName Text View
        entityName.setText(entity.getName());

        // Get Input from the EditText
        answer = userIn.getText().toString().trim();
        answer = answer.replace("\n", "").replace("\r", "");

        // Check to see if the user wants to quit first
        if(answer.equals("quit")){
            System.exit(0);
        }

        // Check date validity using a helper method
        if (!dateValidity(answer)) {
            // Show toast if invalid
            Toast.makeText(getBaseContext(), "Please enter a valid date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Second check uses the Date class itself to check for a wrong date. If any exception is raised then show toast and return
        Date date;
        try {
            date = new Date(answer);
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Please enter a valid date in the format mm/dd/yyyy", Toast.LENGTH_SHORT).show();
            return;
        }

        if (date.precedes(entity.getBorn())) { // Check if input date is before the actual date, If it is, then show incorrect alert
            AlertDialog.Builder tooEarlyAlert = new AlertDialog.Builder(this);
            tooEarlyAlert.setTitle("Incorrect");
            tooEarlyAlert.setMessage("Try a later date.");
            tooEarlyAlert.setNegativeButton("Ok", null);
            tooEarlyAlert.show();
        } else if (entity.getBorn().precedes(date)) { // Check if input date is after the actual date, If it is, then show incorrect alert
            AlertDialog.Builder tooLateAlert = new AlertDialog.Builder(this);
            tooLateAlert.setTitle("Incorrect");
            tooLateAlert.setMessage("Try an earlier date.");
            tooLateAlert.setNegativeButton("Ok", null);
            tooLateAlert.show();
        } else { // Input date is correct
            int awardedTickets = entity.getAwardedTicketNumber();
            totalTicketNum += awardedTickets;
            // Display the total tickets in the TextView
            ticketSum.setText(String.format("Total Tickets: %d", totalTicketNum));

            // Show winning alert
            AlertDialog.Builder winningAlert = new AlertDialog.Builder(this);
            winningAlert.setTitle("You won");
            winningAlert.setMessage("BINGO! " + entity.closingMessage());
            winningAlert.setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(),
                            "You've won " + entity.getAwardedTicketNumber() + " tickets!",
                            Toast.LENGTH_SHORT).show();
                    // After showing alert and toast, call ContinueGame() to go to next entity
                    ContinueGame();
                }
            });
            winningAlert.show();
        }

    }

    public void addEntity(Entity entity) {
        entities[numOfEntities++] = entity.clone();
    }

    // Method to change the current entity to a random one, and clear Edit Text for the next guess
    public void changeEntity() {
        userIn.setText("");

        int entityId = genRandomEntityId();
        entityInUse = entities[entityId];
        entityName.setText(entityInUse.getName());
        ImageSetter(entityInUse.getName());
    }

    // Generates random number to select one of the entities
    public int genRandomEntityId() {
        Random randomNumber = new Random();
        return randomNumber.nextInt(numOfEntities);
    }

    // Method to set the image for an entity
    private void ImageSetter(String entityName) {
        int imageID = 0;

        // Determine the correct image ID based on the entity's name
        switch (entityName) {
            case "Justin Trudeau":
                imageID = R.drawable.justint;
                break;
            case "Celine Dion":
                imageID = R.drawable.celidion;
                break;
            case "My Creator":
                imageID = R.drawable.alexj;
                break;
            case "United States":
                imageID = R.drawable.usaflag;
                break;
        }

        // If an image is found then update it, else put the default error image
        if (imageID != 0) {
            entityImage.setImageResource(imageID);
        } else {
            entityImage.setImageResource(R.drawable.ic_error_outline_black_24dp);
        }
    }

    // Clears the Text Input and changes the entity to a new one
    public void ContinueGame() {
        userIn.getText().clear();
        changeEntity();
    }

    // Helper function to determine date validity
    private boolean dateValidity(String input) {
        // Use the SimpleDateFormat library to acquire a date format to compare the input to
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        dateFormat.setLenient(false); // Make it strict
        try {
            // Parses the input using the format acquired above.
            // If it cant parse the input date then it is in the wrong format and an exception is raised
            dateFormat.parse(input);

            // Checks to see if the input date is made up of 3 components - month date and year
            String[] parts = input.split("/");
            if (parts.length != 3) {
                return false;
            }
            else{ // Checks to see if each component is within the appropriate range
                int month = Integer.parseInt(parts[0]);
                int day = Integer.parseInt(parts[1]);
                int year = Integer.parseInt(parts[2]);

                if (month < 1 || month > 12) return false;
                if (day < 1 || day > 31) return false;
                if (year < 1000 || year > 9999) return false;

                return true;
            }
        } catch (Exception e) { // If any exception is raised then the date is wrong, and false is returned
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_guess_master);

        // Create the entity objects
        Politician jTrudeau = new Politician("Justin Trudeau", new Date("December", 25, 1971), "Male", "Liberal", 0.25);
        Singer cDion = new Singer("Celine Dion", new Date("March", 30, 1961), "Female", "La voix du bon Dieu", new Date("November", 6, 1981), 0.5);
        Person myCreator = new Person("My Creator", new Date("January", 17, 2004),"Male", 1);
        Country usa = new Country("United States", new Date("July", 4, 1776), "Washington D.C.", 0.1);

        addEntity(jTrudeau);
        addEntity(cDion);
        addEntity(myCreator);
        addEntity(usa);

        // Start the game
        int randomEntity = genRandomEntityId();
        entityInUse = entities[randomEntity];
        welcomeToGame(entityInUse);

        // Define View components
        guessButton = (Button) findViewById(R.id.btnGuess);
        userIn = (EditText) findViewById(R.id.guessinput);
        ticketSum = (TextView) findViewById(R.id.ticket);
        entityName = (TextView) findViewById(R.id.entityName);
        btnclearContent = (Button) findViewById(R.id.btnClear);
        entityImage = (ImageView) findViewById(R.id.entityImage);

        // OnClick Listener action for submit button
        guessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // playing the game
                playGame();
            }
        });

        // OnClick Listener action for clear button
        btnclearContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeEntity();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}