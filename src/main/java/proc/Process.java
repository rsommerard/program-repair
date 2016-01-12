package proc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import spoon.Launcher;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;

public class Process {

	public void start() throws IOException {
		boolean successBuild = false;

		for (String line : Files.readAllLines(Paths.get("tmp/logs/current-test.log"))) {
			if (line.contains("[INFO] BUILD SUCCESS")) {
				successBuild = true;
			}
		}

		if (successBuild) {
			System.out.println("BUILD SUCCESS");
			return;
		}

		System.out.println("BUILD FAILURE");

		boolean flag = false;
		List<String> tests = new ArrayList<String>();
		for (String line : Files.readAllLines(Paths.get("tmp/logs/current-test.log"))) {
			if (flag) {
				if (line.isEmpty()) {
					flag = false;
					continue;
				}
				tests.add(line.trim());

			}

			if (line.contains("Failed tests:")) {
				flag = true;
				continue;
			}
		}

		for (String test : tests) {
			String className = test.split("\\.")[0];
			String methodName = test.split("\\.")[1].split(":")[0];

			System.out.println(className);
			System.out.println(methodName);

			Launcher spoon = new Launcher();
			spoon.addProcessor(new ProcessorDiff(className, methodName));
			spoon.run(new String[] { "-i", "src/", "-x" });
		}
	}

	public class ProcessorDiff extends AbstractProcessor<CtClass<?>> {

		// private Repository repository;

		// private Git git;

		private String classname = "";

		private String methodname = "";

		public ProcessorDiff(String classname, String methodname) {
			super();
			this.classname = classname;
			this.methodname = methodname;
		}

		public void process(CtClass<?> testClass) {
			System.out.println(testClass.getSimpleName());
			// Git automatically locates us in the current branch
			// We get the head commit

			/*
			 * TODO: Going through every changes of that commit, detecting the
			 * right test file, using the previous version of this file as an
			 * oracle and putting it to his place
			 * 
			 * 
			 * 
			 * String currentFile = BlobUtils.getContent(repository,
			 * commit.getId(), diff.getNewPath());
			 * 
			 * String previousFile = BlobUtils.getContent(repository,
			 * commit.getParent(0).gedId(), diff.getOldPath());
			 */
		}

		@Override
		public boolean isToBeProcessed(CtClass<?> testClass) {

			if (testClass.getSimpleName().equals(methodname)) {
				System.out.println("Is to be processed");
				return true;
			}

			return false;
		}
	}
}
