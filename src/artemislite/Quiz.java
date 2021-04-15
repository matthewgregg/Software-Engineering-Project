package artemislite;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Quiz {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		test(scanner, 3);
	}

	public static boolean test(Scanner scanner,int difficulty) {

		Question q1 = new Question("How many months are there in 12 years?", new String[] { "124", "142", "144"}, "144", 1);
		Question q2 = new Question("What is NASA's motto?", new String[] { "To infinity and beyond", "For the love of space exploration", "For the benefit of all" }, "For the benefit of all", 1);
		Question q3 = new Question("Which NASA program landed the first humans on the Moon?", new String[] { "Apollo", "Mercury", "Gemini" },"Apollo", 1);

		Question q4 = new Question("Which planet in our solar system has the most moon?", new String[] { "Neptune", "Jupiter", "Saturn" }, "Saturn", 2);
		Question q5 = new Question("What is 4/5 as a decimal?", new String[] { "0.80", "0.45", "4.5" }, "0.80", 2);
		Question q6 = new Question("True or false? The first person in space was an American.", new String[] { "True", "False" }, "False", 2);
		Question q7 = new Question("NASA quarantined the astronauts who landed on the moon when they returned to Earth?. Why?", new String[] { "Moon germs", "Rest", "Adjusting to gravity", "Dizziness" }, "Moon germs", 2);

		Question q8 = new Question("What is the name of the Artemis mission's capsule?", new String[] { "Lunar", "Orion", "SLS", "Explorer" }, "Orion", 3);
		Question q9 = new Question("Uranus has only been visited once, by which spacecraft?", new String[] { "Artemis", "Voyager 2", "Orion" }, "Voyager 2", 3);
		Question q10 = new Question("Which is the second smallest planet in our solar system?", new String[] { "Mars", "Venus", "Mercury" }, "Mars", 3);
		Question q11 = new Question("What is 25 - 4 x 3?", new String[] { "63", "13", "37" }, "13", 3);
		Question q12 = new Question("What is the sun's outermost atmosphere called?", new String[] { "Karena", "Alonda", "Corona" }, "Corona", 3);
		Question q13 = new Question("Which of these is not an Apollo command module name?", new String[] { "Garfield", "Gumdrop", "Charlie Brown", "Casper" }, "Garfield", 3);

		Question q14 = new Question("When did NASA launch their first mission to the sun?", new String[] { "2016", "2017", "2018" }, "2018", 4);
		Question q15 = new Question("NASA stands for the National ____ and Space Administration. Fill in the blank.", new String[] { "Astronomy", "Aeronautics", "Aerospace", "Air" }, "Aeronautics", 4);
		Question q16 = new Question("When was NASA founded?", new String[] { "1945", "1958", "1969", "1980" }, "1958", 4);
		Question q17 = new Question("You are travelling at an average of 5000km/h, how many hours does it take to get to the moon, 380,000km away?", new String[] { "76 hours", "175 hours", "17.5 hours", "34 hours" }, "76 hours", 4);
		Question q18 = new Question("What asteroid disc, made of rock and ice, is around 20 AU wide and starts at Neptune's orbit?", new String[] { "Oort cloud", "Hills cloud", "Asteroid belt", "Kuiper belt" }, "Kuiper belt", 4);

		ArrayList<Question> questions = new ArrayList<>();
		Collections.addAll(questions, q1, q2, q3, q4, q5, q6, q7, q8, q9, q10, q11, q12, q13, q14, q15, q16, q17, q18);

		int correct = 0;
		List<Question> questionsFiltered = questions.stream().filter(q -> q.getDifficulty() == difficulty).collect(Collectors.toList());
		List<Question> questionList = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			int index = new Random().nextInt(questionsFiltered.size());
			Question question = questionsFiltered.get(index);
			questionList.add(question);
		}
		Iterator<Question> i = questionList.iterator();
		while (i.hasNext()) {
			Question question = i.next();
			System.out.println(question.getQuestion());
			int count = 1;
			int ansInt = -1;
			List<String> choices = Arrays.asList(question.getChoices());
			Collections.shuffle(choices);
			for (String c : choices) {
				System.out.printf("%d. %s\n", count++, c);
				if (c.equals(question.getAnswer())) {
					ansInt = count - 1;
				}
			}

			boolean valid = false;
			do {
				String ans = scanner.nextLine().trim();
				if (Arrays.stream(question.getChoices()).anyMatch(a -> a.equalsIgnoreCase(ans))
						|| Arrays.stream(IntStream.range(1, question.getChoices().length + 1).toArray()).anyMatch(n -> Integer.toString(n).equals(ans))) {
					valid = true;
					i.remove();
					if (question.getAnswer().equalsIgnoreCase(ans) || Integer.toString(ansInt).equals(ans)) {
						correct++;
						System.out.print("Correct!");
					} else {
						System.out.print("Incorrect!");
					}
					System.out.print(" Loading");
					Game.loading(3, true);
				} else {
					System.out.println("Please enter one of the above answers.");
				}
			} while (!valid);
		}
		scanner.close();

		System.out.println("You got " + correct + " out of 4.");

		return (double) correct / (double) 4 > 0.6;
	}
}
