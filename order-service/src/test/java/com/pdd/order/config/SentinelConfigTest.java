package com.pdd.order.config;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SentinelConfig 单元测试
 * 测试目标：覆盖所有方法的正常场景、边界场景、异常场景
 */
class SentinelConfigTest {

    private SentinelConfig sentinelConfig;
    private List<String> testResults;
    private final String RESULT_DIR = "src/test/result";
    private final String RESULT_FILE = "test_result.txt";

    @BeforeEach
    void setUp() {
        sentinelConfig = new SentinelConfig();
        testResults = new ArrayList<>();
        // 确保result目录存在
        ensureResultDirectoryExists();
    }

    /**
     * 确保result目录存在
     */
    private void ensureResultDirectoryExists() {
        File dir = new File(RESULT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 测试场景1: 正常场景 - 验证SentinelResourceAspect Bean创建成功
     */
    @Test
    void testSentinelResourceAspectBeanCreation() {
        String testName = "testSentinelResourceAspectBeanCreation";
        String input = "调用sentinelResourceAspect()方法";
        String expected = "返回非null的SentinelResourceAspect实例";

        try {
            SentinelResourceAspect aspect = sentinelConfig.sentinelResourceAspect();
            assertNotNull(aspect, "SentinelResourceAspect should not be null");
            testResults.add(testName + "," + input + "," + expected + "," + "返回了SentinelResourceAspect实例" + "," + "PASS");
        } catch (Exception e) {
            testResults.add(testName + "," + input + "," + expected + "," + "抛出异常: " + e.getMessage() + "," + "FAIL");
            fail("Should not throw exception");
        } finally {
            saveTestResults();
        }
    }

    /**
     * 测试场景2: 边界场景 - 验证Bean类型正确
     */
    @Test
    void testSentinelResourceAspectBeanType() {
        String testName = "testSentinelResourceAspectBeanType";
        String input = "验证返回对象类型";
        String expected = "返回类型为SentinelResourceAspect";

        try {
            Object aspect = sentinelConfig.sentinelResourceAspect();
            assertTrue(aspect instanceof SentinelResourceAspect, "Should return SentinelResourceAspect instance");
            testResults.add(testName + "," + input + "," + expected + "," + "返回类型为SentinelResourceAspect" + "," + "PASS");
        } catch (Exception e) {
            testResults.add(testName + "," + input + "," + expected + "," + "抛出异常: " + e.getMessage() + "," + "FAIL");
            fail("Should not throw exception");
        } finally {
            saveTestResults();
        }
    }

    /**
     * 测试场景3: 异常场景 - 验证方法不会抛出异常
     */
    @Test
    void testSentinelResourceAspectNoException() {
        String testName = "testSentinelResourceAspectNoException";
        String input = "多次调用方法";
        String expected = "方法执行无异常";

        try {
            // 多次调用验证方法稳定性
            for (int i = 0; i < 10; i++) {
                SentinelResourceAspect aspect = sentinelConfig.sentinelResourceAspect();
                assertNotNull(aspect);
            }
            testResults.add(testName + "," + input + "," + expected + "," + "多次调用无异常" + "," + "PASS");
        } catch (Exception e) {
            testResults.add(testName + "," + input + "," + expected + "," + "抛出异常: " + e.getMessage() + "," + "FAIL");
            fail("Should not throw exception");
        } finally {
            saveTestResults();
        }
    }

    /**
     * 测试场景4: 集成场景 - 验证Spring容器中Bean创建
     */
    @Test
    void testSentinelResourceAspectInSpringContext() {
        String testName = "testSentinelResourceAspectInSpringContext";
        String input = "在Spring容器中创建Bean";
        String expected = "Spring容器中Bean创建成功";

        try {
            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
            context.register(SentinelConfig.class);
            context.refresh();

            SentinelResourceAspect aspect = context.getBean(SentinelResourceAspect.class);
            assertNotNull(aspect, "SentinelResourceAspect should be available in Spring context");

            context.close();
            testResults.add(testName + "," + input + "," + expected + "," + "Spring容器中Bean创建成功" + "," + "PASS");
        } catch (Exception e) {
            testResults.add(testName + "," + input + "," + expected + "," + "抛出异常: " + e.getMessage() + "," + "FAIL");
            fail("Should not throw exception");
        } finally {
            saveTestResults();
        }
    }

    /**
     * 保存测试结果到文件
     */
    private void saveTestResults() {
        try (FileWriter writer = new FileWriter(RESULT_DIR + "/" + RESULT_FILE, true)) {
            for (String result : testResults) {
                writer.write(result);
                writer.write(System.lineSeparator());
            }
            testResults.clear();
        } catch (IOException e) {
            System.err.println("Failed to save test results: " + e.getMessage());
        }
    }
}
