THis is one microservice ✅ — the JWTAuth service.
The companion microservice is JWTPaymentClient.

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

----

Step-by-step README for your current setup.
This covers generating the signing key, running both microservices,
and testing with Postman/cURL.

⸻

JWT Microservices Demo

This project contains two Spring Boot microservices:
1.	Auth Service (JwtAuthApplication, port 9000)
      Issues access and refresh tokens.
2.	Payment Service (JwtPaymentClientApplication, port 9100)
      Protects business endpoints using JWT authentication.

⸻

Prerequisites
•	Java 17+
•	Gradle 8+
•	Postman or cURL

⸻

1. Generate a Secure Signing Key

Both services must share the same Base64-encoded secret.

./gradlew run -PmainClass=KeyGen

Example output:

base64:YWFhYmJiY2NjZGRkZWVlZmZmZ2dn...

Copy this value.

⸻

2. Configure Application Secrets

In both services, set the same key in application.properties:

app.jwt.issuer=msp-auth
app.jwt.secret=YWFhYmJiY2NjZGRkZWVlZmZmZ2dn...   # use the value from KeyGen
app.jwt.accessMinutes=60
app.jwt.refreshDays=7


⸻

3. Run Services

Auth service (port 9000):

./gradlew bootRun --args='--server.port=9000'

Payment service (port 9100):

./gradlew bootRun --args='--server.port=9100'


⸻

4. Test the Flow

A) Login (Auth Service)

curl -X POST http://localhost:9000/auth/login \
-H "Content-Type: application/json" \
-d '{"username":"user","password":"password"}'

Response includes accessToken and refreshToken.

⸻

B) Call Payment Service with Access Token

curl http://localhost:9100/payments/echo \
-H "Authorization: Bearer <ACCESS_TOKEN>"


⸻

C) Refresh Access Token (Auth Service)

curl -X POST http://localhost:9000/auth/refresh \
-H "Content-Type: application/json" \
-d '{"refreshToken": "<REFRESH_TOKEN>"}'


⸻

5. Notes
   •	Access tokens expire after 60 minutes.
   •	Refresh tokens expire after 7 days.
   •	If the Payment service returns 401 or 403, check:
   •	Both services share the same Base64 key.
   •	Authorization header is set correctly:

Authorization: Bearer <token>

-----------

If you’re on macOS, the fastest fix is to generate a proper 32-byte secret 
and export it before running the service.

Run this in your terminal (in the same session where you’ll launch the app):

export JWT_SECRET=$(openssl rand -base64 32)
./gradlew bootRun

This ensures that your app.jwt.secret property has a strong enough key for HS256.

⸻

✅ To run your local server:
./gradlew bootRun

⸻

✅ After the app starts, test it with curl:

Login

curl -i http://localhost:9000/auth/login \
-H 'Content-Type: application/json' \
-d '{"username":"demo","password":"demo"}'

Refresh

curl -i http://localhost:9000/auth/refresh \
-H 'Content-Type: application/json' \
-d '{"refreshToken":"<PASTE_REFRESH>"}'

