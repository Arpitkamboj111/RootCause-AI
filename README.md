# RootCause AI — Backend

RootCause AI is a Java Spring Boot microservices project that automatically investigates application errors using AI.

### Services
- **API Gateway:** 
- **Telemetry Service:**
- **Incident Correlation Service:** 
- **AI Analysis Service:**

### How It Works
1. Telemetry events are stored in the database.
2. Related events are correlated to identify incidents.
3. New incidents are automatically sent for AI analysis.
4. AI generates a probable root cause, supporting evidence, and suggested fixes.
5. Analysis results are stored in the database and displayed in the frontend.

