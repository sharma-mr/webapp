//package com.csye6225.neu.user.test;
//
//import static org.junit.Assert.assertNotNull;
//
//import org.junit.Before;
//import org.junit.ClassRule;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.rules.SpringClassRule;
//import org.springframework.test.context.junit4.rules.SpringMethodRule;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import junitparams.JUnitParamsRunner;
//
//@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
//@RunWith(JUnitParamsRunner.class)
//@ContextConfiguration
//public class BaseControllerTest {
//
//    /**
//     * Inject {@link WebApplicationContext} here.
//     */
//    @Autowired
//    protected WebApplicationContext wac;
//
//    /**
//     * Reference of {@link MockMvc}.
//     */
//    protected static MockMvc mockMvc;
//
//    /**
//     * New instance of {@link SpringClassRule}.
//     */
//    @ClassRule
//    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();
//
//    /**
//     * New instance of {@link SpringMethodRule}.
//     */
//    @Rule
//    public final SpringMethodRule springMethodRule = new SpringMethodRule();
//
//    /**
//     * Inject {@link TestRestTemplate} here.
//     */
//    @Autowired
//    protected TestRestTemplate testRestTemplate;
//
//    /**
//     * Before method invoked before any test starts running.
//     */
//    @Before
//    public void setup() {
//        if (null == mockMvc) { // Initialize mockMvc if null.
//            mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
//        }
//    }
//
//    /**
//     * Test to verify that {@link #wac} and {@link #mockMvc} are not null.
//     */
//    @Test
//    public void defaultTest() {
//        assertNotNull("WebApplicationContext is null.", wac);
//        assertNotNull("MockMvc is null.", mockMvc);
//    }
//}
