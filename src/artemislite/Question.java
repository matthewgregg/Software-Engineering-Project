package artemislite;

import java.util.Arrays;

/**
 * Represents a question within the game
 */
public class Question {
	private final String question;
	private final String[] choices;
	private String answer;
	private int difficulty;
	private final String INVALID_ANS = "The answer you entered doesn't match any of the choices";
	private final String INVALID_DIFF = "The difficulty must be between 1 and 4;";
	private final int MIN_DIFFICULTY = 1;
	private final int MAX_DIFFICULTY = 4;
	
	/**
	 * @param question the question to be asked
	 * @param choices the choices presented to the user
	 * @param correctAnswer the correct answer
	 */
	public Question(String question, String[] choices, String correctAnswer, int difficulty) throws IllegalArgumentException {
			this.question = question;
			this.choices = choices;
			setAnswer(correctAnswer);
			setDifficulty(difficulty);
			this.difficulty = difficulty;
	}

	/**
	 * @return the question
	 */
	public String getQuestion() {
		return question;
	}

	/**
	 * @return the choices
	 */
	public String[] getChoices() {
		return choices;
	}

	/**
	 * @return the answer
	 */
	public String getAnswer() {
		return answer;
	}

	/**
	 * sets the answer
	 * @param correctAnswer the answer to set
	 */
	private void setAnswer(String correctAnswer) {
		if (Arrays.asList(choices).contains(correctAnswer)) {
			this.answer = correctAnswer;
		} else {
			throw new IllegalArgumentException(INVALID_ANS);
		}
	}

	/**
	 * @return the difficulty
	 */
	public int getDifficulty() {
		return difficulty;
	}

	/**
	 * @param difficulty the choices to set
	 */
	private void setDifficulty(int difficulty) {
		if (difficulty >= MIN_DIFFICULTY && difficulty <= MAX_DIFFICULTY) {
			this.difficulty = difficulty;
		} else {
			throw new IllegalArgumentException(INVALID_DIFF);
		}
	}
}
