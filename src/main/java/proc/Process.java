package proc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import spoon.Launcher;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;

public class Process {
	
	private CtBlock mCtBlock;

	public void start() throws IOException {
		boolean successBuild = false;

		for (String line : Files.readAllLines(Paths.get("tmp/logs/current-test.log"))) {
		//for (String line : Files.readAllLines(Paths.get("tmp/logs/current-test.log"))) {
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
		//for (String line : Files.readAllLines(Paths.get("tmp/logs/current-test.log"))) {
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
			String className = test.split("\\.")[0].replace("Test", "");
			String methodName = test.split("\\.")[1].split(":")[0].replace("test", "");
			methodName = String.valueOf(methodName.charAt(0)).toLowerCase() + methodName.substring(1);

			Launcher spoon = new Launcher();
			spoon.addProcessor(new ProcessorDiff(className, methodName));
			//spoon.run(new String[] { "-i", "program-repair-test-previous/src/", "-x" });
			spoon.run(new String[] { "-i", "tmp/program-repair-test-previous/src/", "-x" });
			
			spoon = new Launcher();
			spoon.addProcessor(new ProcessorApplyDiff(className, methodName));
			//spoon.run(new String[] { "-i", "src/", "-x" });
			spoon.run(new String[] { "-i", "tmp/program-repair-test-current/src/", "-x" });
			
			Runtime.getRuntime().exec("mv spooned/programrepairtest/" + className + ".java tmp/program-repair-test-after/src/main/java/programrepairtest/" + className + ".java");
		}
	}

	public class ProcessorDiff extends AbstractProcessor<CtClass<?>> {
		
		private String classname = "";
		private String methodname = "";

		public ProcessorDiff(String classname, String methodname) {
			super();
			this.classname = classname;
			this.methodname = methodname;
		}

		public void process(CtClass<?> testClass) {
			
			List<CtMethod<?>> methods = testClass.getMethodsByName(methodname);
		
			for(CtMethod method : methods) {
				mCtBlock = method.getBody();
			}
		}

		@Override
		public boolean isToBeProcessed(CtClass<?> testClass) {
			if (testClass.getSimpleName().equals(classname)) {
				return true;
			}

			return false;
		}
	}
	
public class ProcessorApplyDiff extends AbstractProcessor<CtClass<?>> {
		
		private String classname = "";
		private String methodname = "";

		public ProcessorApplyDiff(String classname, String methodname) {
			super();
			this.classname = classname;
			this.methodname = methodname;
		}

		public void process(CtClass<?> testClass) {
			System.out.println(testClass.getSimpleName());
			
			List<CtMethod<?>> methods = testClass.getMethodsByName(methodname);
		
			for(CtMethod method : methods) {
				System.out.println("mCtBlock = " + mCtBlock);
				method.setBody(getFactory().Core().clone(mCtBlock));
				System.out.println(method);
			}
		}

		@Override
		public boolean isToBeProcessed(CtClass<?> testClass) {
			if (testClass.getSimpleName().equals(classname)) {
				return true;
			}

			return false;
		}
	}
}
