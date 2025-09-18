THis is one microservice ✅ — the JWTAuth service.

It has a few internal components (controllers, filter, service, store), 
but they’re all inside the same Spring Boot app, running on port 9000.

Your architecture so far looks like this:
•	JWTAuth Microservice
•	AuthController → login & refresh endpoints
•	SecureController → protected endpoints
•	JwtAuthFilter → intercepts requests, validates tokens
•	JwtService → generates & parses JWTs
•	UserStore → (in-memory user/password for now)

👉 That’s still one microservice.