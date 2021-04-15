package artemislite;

import java.util.Arrays;

public class Question {
	private final String question;
	private final String[] choices;
	private final String answer;
	private final int difficulty;
	
	/**
	 * @param question
	 * @param choices
	 * @param correctAnswer
	 */
	public Question(String question, String[] choices, String correctAnswer, int difficulty) throws IllegalArgumentException {
		if (Arrays.asList(choices).contains(correctAnswer)) {
			this.question = question;
			this.choices = choices;
			this.answer = correctAnswer;
			this.difficulty = difficulty;
		} else {
			throw new IllegalArgumentException("The answer you entered doesn't match any of the choices");
		}
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
	 * @return the difficulty
	 */
	public int getDifficulty() {
		return difficulty;
	}
}
