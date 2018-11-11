package com.maccimo.breakinglambda;

import net.bytebuddy.agent.ByteBuddyAgent;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;

public class TestAssignmentOrder {

    private static final Instrumentation INSTRUMENTATION = ByteBuddyAgent.install();

    @Test
    public void testOriginalClass() {
        AssignmentOrderMatter instance = new AssignmentOrderMatter();

        Assert.assertEquals("Hallo!", instance.getFirstValue());
        Assert.assertEquals("Farewell!", instance.getLastValue());
    }

    @Test
    public void testRedefinedClass() throws IOException, UnmodifiableClassException {
        redefineClassUnderTest();

        AssignmentOrderMatter instance = new AssignmentOrderMatter();

        Assert.assertEquals("Hello!", instance.getFirstValue());
        Assert.assertEquals("Farewell!", instance.getLastValue());
    }

    @Test
    public void testOriginalThenRedefinedClass() throws IOException, UnmodifiableClassException {
        AssignmentOrderMatter instance = new AssignmentOrderMatter();

        Assert.assertEquals("Hallo!", instance.getFirstValue());
        Assert.assertEquals("Farewell!", instance.getLastValue());

        redefineClassUnderTest();

        Assert.assertEquals("Hello!", instance.getFirstValue());
        Assert.assertEquals("Farewell!", instance.getLastValue());
    }

    private static void redefineClassUnderTest() throws IOException, UnmodifiableClassException {
        InputStream modifiedClassStream = AssignmentOrderMatter.class.getClassLoader().getResourceAsStream("ModifiedClass.bin");

        Assert.assertNotNull(modifiedClassStream);
        
        byte[] modifiedClassBytes = readFully(modifiedClassStream);

        ClassFileTransformer classFileTransformer = (ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) -> {
            if (classBeingRedefined.equals(AssignmentOrderMatter.class)) {
                return modifiedClassBytes;
            } else {
                return null;
            }
        };

        INSTRUMENTATION.addTransformer(classFileTransformer, true);
        try {
            INSTRUMENTATION.retransformClasses(AssignmentOrderMatter.class);
        } finally {
            INSTRUMENTATION.removeTransformer(classFileTransformer);
        }
    }

    private static byte[] readFully(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        int count;
        byte[] data = new byte[16_384];

        while ((count = inputStream.read(data, 0, data.length)) != -1) {
            outputStream.write(data, 0, count);
        }

        return outputStream.toByteArray();
    }

}
