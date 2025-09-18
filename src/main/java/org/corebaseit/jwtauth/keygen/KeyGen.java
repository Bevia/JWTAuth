package org.corebaseit.jwtauth.keygen;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;

public class KeyGen {
    public static void main(String[] args) {
        var key = Jwts.SIG.HS256.key().build();
        System.out.println("base64:" + Encoders.BASE64.encode(key.getEncoded()));
    }
}