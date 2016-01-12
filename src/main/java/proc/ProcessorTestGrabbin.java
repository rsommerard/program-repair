package proc;

import java.util.Set;

import org.junit.Test;

import spoon.Launcher;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;

public class ProcessorTestGrabbin extends AbstractProcessor<CtClass<?>> {

	public void process(CtClass<?> element) {
		System.out.println("Class : " + element.getSimpleName());
		Set<CtMethod<?>> elements = element.getMethodsAnnotatedWith(getFactory().Annotation().createReference(Test.class));

		for (CtMethod<?> method : elements) {
			System.out.println(element.getActualClass());

			System.out.println(element.getSimpleName());

			System.out.println("=======================");
			System.out.println("ANNOTATION :" + method.getAnnotations().toString());
			System.out.println("METHODE :" + method.getSimpleName());
			System.out.println("CONTENU : " + method);
			System.out.println("=======================");
		}
	}

	public static void main(String[] args) throws Exception {
		Launcher spoon = new Launcher();
		spoon.addProcessor(new ProcessorTestGrabbin());
		spoon.run(new String[] { "-i", "../program-repair-test/src/test", "-x" });
	}

}
