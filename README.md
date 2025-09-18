@startuml
title Auth ↔ Payment: JWT Login, Use, and Refresh (compact)

actor Client
participant "Auth Service\n(:9000)" as AUTH #LightBlue
participant "Payment Service\n(:9100)" as PAY #LightGreen
participant "JwtAuthFilter" as FILTER <<filter>> #LightYellow
participant "JWT (AT/RT)" as TOKENS #LightGrey

== Login ==
Client -> AUTH : POST /auth/login\n{ username, password }
AUTH -> AUTH : validate credentials
AUTH -> TOKENS : issue AccessToken (AT)\nissue RefreshToken (RT)
AUTH --> Client : 200 { accessToken=AT,\nrefreshToken=RT, expires_in }

== Use Protected API ==
Client -> PAY : GET /payments/echo\nAuthorization: Bearer AT
PAY -> FILTER : validate signature, issuer,\nexpiry, audience, skew
alt valid AT
FILTER --> PAY : SecurityContext set
PAY --> Client : 200 { "hello": "secure world" }
else invalid/expired AT
FILTER --> Client : 401 Unauthorized
end

== Refresh (optional) ==
Client -> AUTH : POST /auth/refresh\n{ refreshToken=RT }
AUTH -> TOKENS : verify RT
alt valid RT
AUTH -> TOKENS : rotate/new AT (and maybe RT)
AUTH --> Client : 200 { accessToken=AT' }
else invalid RT
AUTH --> Client : 401/403
end

== Retry Protected API ==
Client -> PAY : GET /payments/echo\nAuthorization: Bearer AT'
PAY -> FILTER : validate AT'
FILTER --> PAY : ok
PAY --> Client : 200

@enduml