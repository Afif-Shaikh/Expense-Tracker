package com.Package.ExpenseTracker;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ExpenseTrackerApplicationTests {

    @Test
    void contextLoads() {
        // Verifies the entire Spring context starts without errors
    }
}