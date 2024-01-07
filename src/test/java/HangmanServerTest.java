/*
*  Hangman Server JUnit 5 Test Cases
*  Author: Mark Cirineo
*  System: Intellij IDEA
*  Course: CS 342 Software Design
*
*
*
*
*
*
*
* */



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class HangmanServerTest {

    private TestConsumer testConsumer;
    private HangmanServer hangmanServer;

    @BeforeEach
    void setUp() {
        testConsumer = new TestConsumer();
        hangmanServer = new HangmanServer(testConsumer);
    }

    @Test
    void generateRandomWordFromFile() {
        // Assuming the wordlist.txt contains "TEST"
        String word = hangmanServer.generateRandomWordFromFile("src/main/resources/test_wordlist.txt");
        assertEquals("TEST", word);
    }
    @Test
    void generateRandomWordFromFile2() {
        // Assuming the wordlist.txt contains "TEST"
        String word = hangmanServer.generateRandomWordFromFile("src/main/resources/test_wordlist2.txt");
        assertEquals("CHERRY", word);
    }

    @Test
    void handleSingleLetterGuessCorrect() {
        // Set up the game state
        hangmanServer.wordToGuess = "TEST";
        hangmanServer.guessedLetters.put(0, false);
        hangmanServer.guessedLetters.put(1, false);
        hangmanServer.guessedLetters.put(2, false);
        hangmanServer.guessedLetters.put(3, false);

        // Create a mock client
        HangmanServer.ClientThread mockClient = hangmanServer.new ClientThread(new Socket(), 1);

        // Simulate a correct letter guess from the client
        mockClient.handleSingleLetterGuess('T');

        // Verify that the game state is updated correctly
        assertTrue(hangmanServer.guessedLetters.get(0));
        assertEquals("T--T", mockClient.getUpdatedDashedLine());
    }

    @Test
    void handleSingleLetterGuessCorrect2() {
        // Set up the game state
        hangmanServer.wordToGuess = "SOFTWARE";
        hangmanServer.guessedLetters.put(0, false);
        hangmanServer.guessedLetters.put(1, false);
        hangmanServer.guessedLetters.put(2, false);
        hangmanServer.guessedLetters.put(3, false);
        hangmanServer.guessedLetters.put(4, false);
        hangmanServer.guessedLetters.put(5, false);
        hangmanServer.guessedLetters.put(6, false);
        hangmanServer.guessedLetters.put(7, false);






        // Create a mock client
        HangmanServer.ClientThread mockClient = hangmanServer.new ClientThread(new Socket(), 1);

        // Simulate a correct letter guess from the client
        mockClient.handleSingleLetterGuess('O');

        // Verify that the game state is updated correctly
        assertTrue(hangmanServer.guessedLetters.get(1));
        assertEquals("-O------", mockClient.getUpdatedDashedLine());
    }

    @Test
    void handleSingleLetterGuessCorrect3() {
        // Set up the game state
        hangmanServer.wordToGuess = "DESIGN";
        hangmanServer.guessedLetters.put(0, false);
        hangmanServer.guessedLetters.put(1, false);
        hangmanServer.guessedLetters.put(2, false);
        hangmanServer.guessedLetters.put(3, false);
        hangmanServer.guessedLetters.put(4, false);
        hangmanServer.guessedLetters.put(5, false);







        // Create a mock client
        HangmanServer.ClientThread mockClient = hangmanServer.new ClientThread(new Socket(), 1);

        // Simulate a correct letter guess from the client
        mockClient.handleSingleLetterGuess('G');

        // Verify that the game state is updated correctly
        assertTrue(hangmanServer.guessedLetters.get(4));
        assertEquals("----G-", mockClient.getUpdatedDashedLine());
    }

    @Test
    void handleSingleLetterGuessCorrectMultiple() {
        // Set up the game state
        hangmanServer.wordToGuess = "DESIGN";
        hangmanServer.guessedLetters.put(0, false);
        hangmanServer.guessedLetters.put(1, false);
        hangmanServer.guessedLetters.put(2, false);
        hangmanServer.guessedLetters.put(3, false);
        hangmanServer.guessedLetters.put(4, false);
        hangmanServer.guessedLetters.put(5, false);

        // Create a mock client
        HangmanServer.ClientThread mockClient = hangmanServer.new ClientThread(new Socket(), 1);

        // Simulate correct letter guesses from the client
        mockClient.handleSingleLetterGuess('I');
        mockClient.handleSingleLetterGuess('G');

        // Verify that the game state is updated correctly
        assertTrue(hangmanServer.guessedLetters.get(3)); // 'I' is at index 3
        assertTrue(hangmanServer.guessedLetters.get(4)); // 'G' is at index 4

        assertEquals("---IG-", mockClient.getUpdatedDashedLine());
    }




    @Test
    void handleSingleLetterGuessIncorrect() {
        // Set up the game state
        hangmanServer.wordToGuess = "TEST";
        hangmanServer.guessedLetters.put(0, false);
        hangmanServer.guessedLetters.put(1, false);
        hangmanServer.guessedLetters.put(2, false);
        hangmanServer.guessedLetters.put(3, false);

        // Create a mock client
        HangmanServer.ClientThread mockClient = hangmanServer.new ClientThread(new Socket(), 1);

        // Simulate an incorrect letter guess from the client
        mockClient.handleSingleLetterGuess('X');

        // Verify that the game state is updated correctly
        assertEquals(1, hangmanServer.incorrectGuesses);
        assertEquals("----", mockClient.getUpdatedDashedLine());
    }

    @Test
    void handleSingleLetterGuessIncorrect2() {
        // Set up the game state
        hangmanServer.wordToGuess = "APPLE";
        hangmanServer.guessedLetters.put(0, false);
        hangmanServer.guessedLetters.put(1, false);
        hangmanServer.guessedLetters.put(2, false);
        hangmanServer.guessedLetters.put(3, false);
        hangmanServer.guessedLetters.put(4, false);



        // Create a mock client
        HangmanServer.ClientThread mockClient = hangmanServer.new ClientThread(new Socket(), 1);

        // Simulate an incorrect letter guess from the client
        mockClient.handleSingleLetterGuess('X');

        // Verify that the game state is updated correctly
        assertEquals(1, hangmanServer.incorrectGuesses);
        assertEquals("-----", mockClient.getUpdatedDashedLine());
    }

    @Test
    void resetGame() {
        // Set up the game state
        hangmanServer.wordToGuess = "TEST";
        hangmanServer.guessedLetters.put(0, true);
        hangmanServer.guessedLetters.put(1, true);
        hangmanServer.guessedLetters.put(2, true);
        hangmanServer.guessedLetters.put(3, true);
        hangmanServer.incorrectGuesses = 2;

        // Create a mock client
        HangmanServer.ClientThread mockClient = hangmanServer.new ClientThread(new Socket(), 1);

        // Simulate a reset game request from the client
        mockClient.resetGame();

        // Verify that the game state is reset correctly
        assertFalse(hangmanServer.guessedLetters.containsValue(true));
        assertEquals(0, hangmanServer.incorrectGuesses);
        assertNotEquals("TEST", hangmanServer.wordToGuess);
    }

    @Test
    void endGameWin() {
        // Create a mock client
        HangmanServer.ClientThread mockClient = hangmanServer.new ClientThread(new Socket(), 1);

        // Simulate an end game with a win from the client
        mockClient.endGame(true, mockClient);

        // Verify that the callback message indicates a win
        assertEquals("client #1 has won the game!", testConsumer.getCapturedMessage());
    }

    @Test
    void endGameLose() {
        // Create a mock client
        HangmanServer.ClientThread mockClient = hangmanServer.new ClientThread(new Socket(), 1);

        // Simulate an end game with a loss from the client
        mockClient.endGame(false, mockClient);

        // Verify that the callback message indicates a loss
        assertEquals("client #1 has lost the game.", testConsumer.getCapturedMessage());
    }








    private static class TestConsumer implements Consumer<Serializable> {
        private Serializable capturedMessage;

        @Override
        public void accept(Serializable message) {
            this.capturedMessage = message;
        }

        Serializable getCapturedMessage() {
            return capturedMessage;
        }
    }
}
