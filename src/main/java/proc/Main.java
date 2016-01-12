package proc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) throws IOException {
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
		
		for(String test : tests) {
			String className = test.split("\\.")[0];
			String methodName = test.split("\\.")[1].split(":")[0];
			
			System.out.println(className);
			System.out.println(methodName);
		}
	}
}
