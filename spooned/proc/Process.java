package proc;


public class Process {
    public void start() throws java.io.IOException {
        boolean successBuild = false;
        for (java.lang.String line : java.nio.file.Files.readAllLines(java.nio.file.Paths.get("tmp/logs/current-test.log"))) {
            if (line.contains("[INFO] BUILD SUCCESS")) {
                successBuild = true;
            } 
        }
        if (successBuild) {
            java.lang.System.out.println("BUILD SUCCESS");
            return ;
        } 
        java.lang.System.out.println("BUILD FAILURE");
        boolean flag = false;
        java.util.List<java.lang.String> tests = new java.util.ArrayList<java.lang.String>();
        for (java.lang.String line : java.nio.file.Files.readAllLines(java.nio.file.Paths.get("tmp/logs/current-test.log"))) {
            if (flag) {
                if (line.isEmpty()) {
                    flag = false;
                    continue;
                } 
                tests.add(line.trim());
            } 
            if (line.contains("Failed tests:")) {
                if (java.lang.System.getProperty("os.name").contains("Windows")) {
                    line = line.replace("Failed tests:", "");
                    tests.add(line.trim());
                } 
                flag = true;
                continue;
            } 
        }
        for (java.lang.String test : tests) {
            java.lang.System.out.println(test);
            java.lang.String className = test.split("\\.")[0];
            java.lang.String methodName = test.split("\\.")[1].split(":")[0];
            java.lang.System.out.println(className);
            java.lang.System.out.println(methodName);
            spoon.Launcher spoon = new spoon.Launcher();
            spoon.addProcessor(new ProcessorDiff(className , methodName));
            spoon.run(new java.lang.String[]{ "-i" , "src/" , "-x" });
        }
    }

    public class ProcessorDiff extends spoon.processing.AbstractProcessor<spoon.reflect.declaration.CtClass<?>> {
        private java.lang.String classname = "";

        private java.lang.String methodname = "";

        public ProcessorDiff(java.lang.String classname ,java.lang.String methodname) {
            super();
            this.classname = classname;
            this.methodname = methodname;
        }

        public void process(spoon.reflect.declaration.CtClass<?> testClass) {
            java.lang.System.out.println(testClass.getSimpleName());
        }

        @java.lang.Override
        public boolean isToBeProcessed(spoon.reflect.declaration.CtClass<?> testClass) {
            if (testClass.getSimpleName().equals(methodname)) {
                java.lang.System.out.println("Is to be processed");
                return true;
            } 
            return false;
        }
    }
}

