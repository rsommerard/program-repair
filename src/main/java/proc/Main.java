package proc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.core.CommitUtils;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;

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

		for (String test : tests) {
			String className = test.split("\\.")[0];
			String methodName = test.split("\\.")[1].split(":")[0];

			System.out.println(className);
			System.out.println(methodName);
		}
	}

	public class ProcessorGit extends AbstractProcessor<CtClass<?>> {

		Repository repository;

		Git git;

		public void process(CtClass<?> testClass) {
			// Setting up the repository
			try {
				File gitDir = new File("../program-repair-test/.git");

				RepositoryBuilder builder = new RepositoryBuilder();

				repository = builder.setGitDir(gitDir).readEnvironment().findGitDir().build();

				git = new Git(repository);
			} catch (IOException e) {
				System.out.println("IO Exception happened here");
			}

			// Git automatically locates us in the current branch
			// We get the head commit
			RevCommit latestCommit = CommitUtils.getHead(repository);

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
	}
}
