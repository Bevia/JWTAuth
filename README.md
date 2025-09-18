If you’re on macOS, the fastest fix is to generate a proper 32-byte secret and export it before running the service.

Run this in your terminal (in the same session where you’ll launch the app):

export JWT_SECRET=$(openssl rand -base64 32)
./gradlew bootRun

This ensures that your app.jwt.secret property has a strong enough key for HS256.

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

