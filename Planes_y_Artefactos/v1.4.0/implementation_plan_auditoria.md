# Technical Audit Plan (Lighthouse & OWASP)

This plan outlines the steps to perform a comprehensive Technical and Security Audit of the `v1.4.0` release of the Hotel Reservations system.

## Proposed Steps

### 1. Frontend Performance & Accessibility Audit (Lighthouse)
We will execute a Google Lighthouse scan against the Angular frontend (`http://localhost:4200`) in headless mode. 
- **Metrics to capture:** Performance, Accessibility, Best Practices, and SEO.
- **Output:** We will generate an HTML report and I will analyze the JSON results to present you with a summary of the scores and any recommended improvements.

### 2. Backend Dependency Security Audit (OWASP Dependency-Check)
We will run the `owasp-dependency-check` Maven plugin against the Spring Boot backend.
- **Purpose:** Identifies if any of our Java/Spring dependencies (like JJWT, Spring Security, etc.) have known publicly disclosed vulnerabilities (CVEs).
- **Command:** `mvn org.owasp:dependency-check-maven:check`

### 3. API Security & Code Audit (OWASP Top 10)
Since a dynamic ZAP scanner (like `owasp/zap2docker-stable`) will simply be blocked by our JWT authentication screen (returning 401s without seeing the app), we will combine a manual and static OWASP API Top 10 Audit.
- **Review Areas:** JWT Token Validation, CORS Configuration (`SecurityConfig.java`), Cross-Site Scripting (XSS) protections in Angular, and SQL Injection prevention in our Spring Data JPA repositories.

## User Review Required
> [!IMPORTANT]
> If you specifically need a **dynamic OWASP ZAP scan via Docker** (`zap-baseline.py`), please let me know. However, keep in mind that dynamic scanners struggle with SPA apps heavily protected by JWTs without complex authentication scripting. The proposed Dependency Check + Static Code Audit is usually much more effective for this architecture.

Do you approve this plan to begin the audit?
