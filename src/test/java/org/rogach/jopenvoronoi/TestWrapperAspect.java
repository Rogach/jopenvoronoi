package org.rogach.jopenvoronoi;

import org.aspectj.lang.annotation.*;
import org.aspectj.lang.*;

@Aspect
public class TestWrapperAspect {

    @Pointcut("execution(* org.rogach.jopenvoronoi.*.*())")
    public void testMethodEntryPoint() {}

    @AfterThrowing(pointcut = "testMethodEntryPoint()", throwing = "t")
    public void afterTest(JoinPoint p, Throwable t) {
        System.out.printf("%s: %s, %s\n",
                          BugHunter.getFailureName(t),
                          t.getClass().getName(),
                          t.getMessage());
        for (StackTraceElement ste : t.getStackTrace()) {
            if (ste.getClassName().startsWith("org.rogach.jopenvoronoi") &&
                !ste.getClassName().startsWith("org.rogach.jopenvoronoi.BugHunter") &&
                !ste.getClassName().startsWith("org.rogach.jopenvoronoi.EuclideanInput")) {
                System.out.printf("    at %s.%s(%s:%s)\n",
                                  ste.getClassName(),
                                  ste.getMethodName(),
                                  ste.getFileName(),
                                  ste.getLineNumber());
            }
        }
    }

}
