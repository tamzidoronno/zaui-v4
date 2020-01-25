/*
 * Copyright (C) 2016 Southern Storm Software, Pty Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

/*
This example runs tests on the P521 algorithm.

Note: This example is too large to run on the Arduino Uno or other
AVR platforms with 32K or less of flash memory.
*/

#include <Crypto.h>
#include <P521.h>
#include <SHA256.h>
#include <SHA512.h>
#include <RNG.h>
#include <RNG.h>
#include <string.h>
#if defined(ESP8266) || defined(ESP32)
#include <pgmspace.h>
#else
#include <avr/pgmspace.h>
#endif

void printNumber(const char *name, const uint8_t *x, size_t len)
{
    static const char hexchars[] = "0123456789ABCDEF";
    Serial.print(name);
    Serial.print(" = ");
    for (size_t posn = 0; posn < len; ++posn) {
        Serial.print(hexchars[(x[posn] >> 4) & 0x0F]);
        Serial.print(hexchars[x[posn] & 0x0F]);
    }
    Serial.println();
}

static int P521_memcmp_P(const void *s1, const void *s2, size_t len)
{
    const uint8_t *u1 = (const uint8_t *)s1;
    const uint8_t *u2 = (const uint8_t *)s2;
    while (len > 0) {
        int ch1 = *u1++;
        int ch2 = pgm_read_byte(u2++);
        if (ch1 != ch2)
            return ch1 - ch2;
        --len;
    }
    return 0;
}

static uint8_t alice_k[132];
static uint8_t alice_f[66];
static uint8_t bob_k[132];
static uint8_t bob_f[66];

// Check the eval() function using the test vectors from RFC 5903.
void testEval()
{
    static uint8_t const alice_private[66] PROGMEM = {
        0x00, 0x37, 0xAD, 0xE9, 0x31, 0x9A, 0x89, 0xF4,
        0xDA, 0xBD, 0xB3, 0xEF, 0x41, 0x1A, 0xAC, 0xCC,
        0xA5, 0x12, 0x3C, 0x61, 0xAC, 0xAB, 0x57, 0xB5,
        0x39, 0x3D, 0xCE, 0x47, 0x60, 0x81, 0x72, 0xA0,
        0x95, 0xAA, 0x85, 0xA3, 0x0F, 0xE1, 0xC2, 0x95,
        0x2C, 0x67, 0x71, 0xD9, 0x37, 0xBA, 0x97, 0x77,
        0xF5, 0x95, 0x7B, 0x26, 0x39, 0xBA, 0xB0, 0x72,
        0x46, 0x2F, 0x68, 0xC2, 0x7A, 0x57, 0x38, 0x2D,
        0x4A, 0x52
    };
    static uint8_t const alice_public[132] PROGMEM = {
        0x00, 0x15, 0x41, 0x7E, 0x84, 0xDB, 0xF2, 0x8C,
        0x0A, 0xD3, 0xC2, 0x78, 0x71, 0x33, 0x49, 0xDC,
        0x7D, 0xF1, 0x53, 0xC8, 0x97, 0xA1, 0x89, 0x1B,
        0xD9, 0x8B, 0xAB, 0x43, 0x57, 0xC9, 0xEC, 0xBE,
        0xE1, 0xE3, 0xBF, 0x42, 0xE0, 0x0B, 0x8E, 0x38,
        0x0A, 0xEA, 0xE5, 0x7C, 0x2D, 0x10, 0x75, 0x64,
        0x94, 0x18, 0x85, 0x94, 0x2A, 0xF5, 0xA7, 0xF4,
        0x60, 0x17, 0x23, 0xC4, 0x19, 0x5D, 0x17, 0x6C,
        0xED, 0x3E, 0x01, 0x7C, 0xAE, 0x20, 0xB6, 0x64,
        0x1D, 0x2E, 0xEB, 0x69, 0x57, 0x86, 0xD8, 0xC9,
        0x46, 0x14, 0x62, 0x39, 0xD0, 0x99, 0xE1, 0x8E,
        0x1D, 0x5A, 0x51, 0x4C, 0x73, 0x9D, 0x7C, 0xB4,
        0xA1, 0x0A, 0xD8, 0xA7, 0x88, 0x01, 0x5A, 0xC4,
        0x05, 0xD7, 0x79, 0x9D, 0xC7, 0x5E, 0x7B, 0x7D,
        0x5B, 0x6C, 0xF2, 0x26, 0x1A, 0x6A, 0x7F, 0x15,
        0x07, 0x43, 0x8B, 0xF0, 0x1B, 0xEB, 0x6C, 0xA3,
        0x92, 0x6F, 0x95, 0x82
    };
    static uint8_t const bob_private[66] PROGMEM = {
        0x01, 0x45, 0xBA, 0x99, 0xA8, 0x47, 0xAF, 0x43,
        0x79, 0x3F, 0xDD, 0x0E, 0x87, 0x2E, 0x7C, 0xDF,
        0xA1, 0x6B, 0xE3, 0x0F, 0xDC, 0x78, 0x0F, 0x97,
        0xBC, 0xCC, 0x3F, 0x07, 0x83, 0x80, 0x20, 0x1E,
        0x9C, 0x67, 0x7D, 0x60, 0x0B, 0x34, 0x37, 0x57,
        0xA3, 0xBD, 0xBF, 0x2A, 0x31, 0x63, 0xE4, 0xC2,
        0xF8, 0x69, 0xCC, 0xA7, 0x45, 0x8A, 0xA4, 0xA4,
        0xEF, 0xFC, 0x31, 0x1F, 0x5C, 0xB1, 0x51, 0x68,
        0x5E, 0xB9
    };
    static uint8_t const bob_public[132] PROGMEM = {
        0x00, 0xD0, 0xB3, 0x97, 0x5A, 0xC4, 0xB7, 0x99,
        0xF5, 0xBE, 0xA1, 0x6D, 0x5E, 0x13, 0xE9, 0xAF,
        0x97, 0x1D, 0x5E, 0x9B, 0x98, 0x4C, 0x9F, 0x39,
        0x72, 0x8B, 0x5E, 0x57, 0x39, 0x73, 0x5A, 0x21,
        0x9B, 0x97, 0xC3, 0x56, 0x43, 0x6A, 0xDC, 0x6E,
        0x95, 0xBB, 0x03, 0x52, 0xF6, 0xBE, 0x64, 0xA6,
        0xC2, 0x91, 0x2D, 0x4E, 0xF2, 0xD0, 0x43, 0x3C,
        0xED, 0x2B, 0x61, 0x71, 0x64, 0x00, 0x12, 0xD9,
        0x46, 0x0F, 0x01, 0x5C, 0x68, 0x22, 0x63, 0x83,
        0x95, 0x6E, 0x3B, 0xD0, 0x66, 0xE7, 0x97, 0xB6,
        0x23, 0xC2, 0x7C, 0xE0, 0xEA, 0xC2, 0xF5, 0x51,
        0xA1, 0x0C, 0x2C, 0x72, 0x4D, 0x98, 0x52, 0x07,
        0x7B, 0x87, 0x22, 0x0B, 0x65, 0x36, 0xC5, 0xC4,
        0x08, 0xA1, 0xD2, 0xAE, 0xBB, 0x8E, 0x86, 0xD6,
        0x78, 0xAE, 0x49, 0xCB, 0x57, 0x09, 0x1F, 0x47,
        0x32, 0x29, 0x65, 0x79, 0xAB, 0x44, 0xFC, 0xD1,
        0x7F, 0x0F, 0xC5, 0x6A
    };
    static uint8_t const shared_secret[66] PROGMEM = {
        0x01, 0x14, 0x4C, 0x7D, 0x79, 0xAE, 0x69, 0x56,
        0xBC, 0x8E, 0xDB, 0x8E, 0x7C, 0x78, 0x7C, 0x45,
        0x21, 0xCB, 0x08, 0x6F, 0xA6, 0x44, 0x07, 0xF9,
        0x78, 0x94, 0xE5, 0xE6, 0xB2, 0xD7, 0x9B, 0x04,
        0xD1, 0x42, 0x7E, 0x73, 0xCA, 0x4B, 0xAA, 0x24,
        0x0A, 0x34, 0x78, 0x68, 0x59, 0x81, 0x0C, 0x06,
        0xB3, 0xC7, 0x15, 0xA3, 0xA8, 0xCC, 0x31, 0x51,
        0xF2, 0xBE, 0xE4, 0x17, 0x99, 0x6D, 0x19, 0xF3,
        0xDD, 0xEA
    };

    // Evaluate the curve function and check the public keys.
    uint8_t result[132];
    Serial.println("Fixed test vectors:");
    Serial.print("Computing Alice's public key ... ");
    Serial.flush();
    memcpy_P(alice_f, alice_private, 66);
    unsigned long start = micros();
    P521::eval(result, alice_f, 0);
    unsigned long elapsed = micros() - start;
    if (P521_memcmp_P(result, alice_public, 132) == 0) {
        Serial.print("ok");
    } else {
        Serial.println("failed");
        printNumber("actual  ", result, 132);
        printNumber("expected", alice_f, 132);
    }
    Serial.print(" (elapsed ");
    Serial.print(elapsed);
    Serial.println(" us)");
    Serial.print("Computing Bob's public key ... ");
    Serial.flush();
    memcpy_P(bob_f, bob_private, 66);
    start = micros();
    P521::eval(result, bob_f, 0);
    elapsed = micros() - start;
    if (P521_memcmp_P(result, bob_public, 132) == 0) {
        Serial.print("ok");
    } else {
        Serial.println("failed");
        printNumber("actual  ", result, 132);
        printNumber("expected", bob_f, 132);
    }
    Serial.print(" (elapsed ");
    Serial.print(elapsed);
    Serial.println(" us)");

    // Compute the shared secret from each side.
    Serial.print("Computing Alice's shared secret ... ");
    Serial.flush();
    memcpy_P(alice_f, alice_private, 66);
    memcpy_P(bob_k, bob_public, 132);
    memcpy_P(bob_f, shared_secret, 66);
    start = micros();
    P521::eval(result, alice_f, bob_k);
    elapsed = micros() - start;
    if (P521_memcmp_P(result, shared_secret, 66) == 0) {
        Serial.print("ok");
    } else {
        Serial.println("failed");
        printNumber("actual  ", result, 66);
        printNumber("expected", bob_f, 66);
    }
    Serial.print(" (elapsed ");
    Serial.print(elapsed);
    Serial.println(" us)");
    Serial.print("Computing Bob's shared secret ... ");
    Serial.flush();
    memcpy_P(bob_f, bob_private, 66);
    memcpy_P(alice_k, alice_public, 132);
    memcpy_P(alice_f, shared_secret, 66);
    start = micros();
    P521::eval(result, bob_f, alice_k);
    elapsed = micros() - start;
    if (P521_memcmp_P(result, shared_secret, 66) == 0) {
        Serial.print("ok");
    } else {
        Serial.println("failed");
        printNumber("actual  ", result, 66);
        printNumber("expected", alice_f, 66);
    }
    Serial.print(" (elapsed ");
    Serial.print(elapsed);
    Serial.println(" us)");
}

void testDH()
{
    Serial.println("Diffie-Hellman key exchange:");
    Serial.print("Generate random k/f for Alice ... ");
    Serial.flush();
    unsigned long start = micros();
    P521::dh1(alice_k, alice_f);
    unsigned long elapsed = micros() - start;
    Serial.print("elapsed ");
    Serial.print(elapsed);
    Serial.println(" us");

    Serial.print("Generate random k/f for Bob ... ");
    Serial.flush();
    start = micros();
    P521::dh1(bob_k, bob_f);
    elapsed = micros() - start;
    Serial.print("elapsed ");
    Serial.print(elapsed);
    Serial.println(" us");

    Serial.print("Generate shared secret for Alice ... ");
    Serial.flush();
    start = micros();
    P521::dh2(bob_k, alice_f);
    elapsed = micros() - start;
    Serial.print("elapsed ");
    Serial.print(elapsed);
    Serial.println(" us");

    Serial.print("Generate shared secret for Bob ... ");
    Serial.flush();
    start = micros();
    P521::dh2(alice_k, bob_f);
    elapsed = micros() - start;
    Serial.print("elapsed ");
    Serial.print(elapsed);
    Serial.println(" us");

    Serial.print("Check that the shared secrets match ... ");
    if (memcmp(alice_f, bob_f, 66) == 0) {
        Serial.println("ok");
    } else {
        Serial.println("failed");
        printNumber("actual  ", alice_f, 66);
        printNumber("expected", bob_f, 66);
    }
}

struct TestSignKey
{
    uint8_t privateKey[66];
    uint8_t publicKey[132];
};

// Test key from RFC 6979, Appendix A.2.7.
static TestSignKey const testKeyP521 PROGMEM = {
    {0x00, 0xFA, 0xD0, 0x6D, 0xAA, 0x62, 0xBA, 0x3B,        // x
     0x25, 0xD2, 0xFB, 0x40, 0x13, 0x3D, 0xA7, 0x57,
     0x20, 0x5D, 0xE6, 0x7F, 0x5B, 0xB0, 0x01, 0x8F,
     0xEE, 0x8C, 0x86, 0xE1, 0xB6, 0x8C, 0x7E, 0x75,
     0xCA, 0xA8, 0x96, 0xEB, 0x32, 0xF1, 0xF4, 0x7C,
     0x70, 0x85, 0x58, 0x36, 0xA6, 0xD1, 0x6F, 0xCC,
     0x14, 0x66, 0xF6, 0xD8, 0xFB, 0xEC, 0x67, 0xDB,
     0x89, 0xEC, 0x0C, 0x08, 0xB0, 0xE9, 0x96, 0xB8,
     0x35, 0x38},
    {0x01, 0x89, 0x45, 0x50, 0xD0, 0x78, 0x59, 0x32,        // Ux
     0xE0, 0x0E, 0xAA, 0x23, 0xB6, 0x94, 0xF2, 0x13,
     0xF8, 0xC3, 0x12, 0x1F, 0x86, 0xDC, 0x97, 0xA0,
     0x4E, 0x5A, 0x71, 0x67, 0xDB, 0x4E, 0x5B, 0xCD,
     0x37, 0x11, 0x23, 0xD4, 0x6E, 0x45, 0xDB, 0x6B,
     0x5D, 0x53, 0x70, 0xA7, 0xF2, 0x0F, 0xB6, 0x33,
     0x15, 0x5D, 0x38, 0xFF, 0xA1, 0x6D, 0x2B, 0xD7,
     0x61, 0xDC, 0xAC, 0x47, 0x4B, 0x9A, 0x2F, 0x50,
     0x23, 0xA4,
     0x00, 0x49, 0x31, 0x01, 0xC9, 0x62, 0xCD, 0x4D,        // Uy
     0x2F, 0xDD, 0xF7, 0x82, 0x28, 0x5E, 0x64, 0x58,
     0x41, 0x39, 0xC2, 0xF9, 0x1B, 0x47, 0xF8, 0x7F,
     0xF8, 0x23, 0x54, 0xD6, 0x63, 0x0F, 0x74, 0x6A,
     0x28, 0xA0, 0xDB, 0x25, 0x74, 0x1B, 0x5B, 0x34,
     0xA8, 0x28, 0x00, 0x8B, 0x22, 0xAC, 0xC2, 0x3F,
     0x92, 0x4F, 0xAA, 0xFB, 0xD4, 0xD3, 0x3F, 0x81,
     0xEA, 0x66, 0x95, 0x6D, 0xFE, 0xAA, 0x2B, 0xFD,
     0xFC, 0xF5}
};

struct TestSignVector
{
    const char *name;
    const char *data;
    uint8_t signature[132];
};

// Test vectors from RFC 6979, Appendix A.2.7.
static TestSignVector const testVectorP521_1 PROGMEM = {
    // P-521 test case with SHA-256 and message "sample".
    "P-521 #1",
    "sample",
    {0x01, 0x51, 0x1B, 0xB4, 0xD6, 0x75, 0x11, 0x4F,        // r
     0xE2, 0x66, 0xFC, 0x43, 0x72, 0xB8, 0x76, 0x82,
     0xBA, 0xEC, 0xC0, 0x1D, 0x3C, 0xC6, 0x2C, 0xF2,
     0x30, 0x3C, 0x92, 0xB3, 0x52, 0x60, 0x12, 0x65,
     0x9D, 0x16, 0x87, 0x6E, 0x25, 0xC7, 0xC1, 0xE5,
     0x76, 0x48, 0xF2, 0x3B, 0x73, 0x56, 0x4D, 0x67,
     0xF6, 0x1C, 0x6F, 0x14, 0xD5, 0x27, 0xD5, 0x49,
     0x72, 0x81, 0x04, 0x21, 0xE7, 0xD8, 0x75, 0x89,
     0xE1, 0xA7,
     0x00, 0x4A, 0x17, 0x11, 0x43, 0xA8, 0x31, 0x63,        // s
     0xD6, 0xDF, 0x46, 0x0A, 0xAF, 0x61, 0x52, 0x26,
     0x95, 0xF2, 0x07, 0xA5, 0x8B, 0x95, 0xC0, 0x64,
     0x4D, 0x87, 0xE5, 0x2A, 0xA1, 0xA3, 0x47, 0x91,
     0x6E, 0x4F, 0x7A, 0x72, 0x93, 0x0B, 0x1B, 0xC0,
     0x6D, 0xBE, 0x22, 0xCE, 0x3F, 0x58, 0x26, 0x4A,
     0xFD, 0x23, 0x70, 0x4C, 0xBB, 0x63, 0xB2, 0x9B,
     0x93, 0x1F, 0x7D, 0xE6, 0xC9, 0xD9, 0x49, 0xA7,
     0xEC, 0xFC}
};
static TestSignVector const testVectorP521_2 PROGMEM = {
    // P-521 test case with SHA-512 and message "sample".
    "P-521 #2",
    "sample",
    {0x00, 0xC3, 0x28, 0xFA, 0xFC, 0xBD, 0x79, 0xDD,        // r
     0x77, 0x85, 0x03, 0x70, 0xC4, 0x63, 0x25, 0xD9,
     0x87, 0xCB, 0x52, 0x55, 0x69, 0xFB, 0x63, 0xC5,
     0xD3, 0xBC, 0x53, 0x95, 0x0E, 0x6D, 0x4C, 0x5F,
     0x17, 0x4E, 0x25, 0xA1, 0xEE, 0x90, 0x17, 0xB5,
     0xD4, 0x50, 0x60, 0x6A, 0xDD, 0x15, 0x2B, 0x53,
     0x49, 0x31, 0xD7, 0xD4, 0xE8, 0x45, 0x5C, 0xC9,
     0x1F, 0x9B, 0x15, 0xBF, 0x05, 0xEC, 0x36, 0xE3,
     0x77, 0xFA,
     0x00, 0x61, 0x7C, 0xCE, 0x7C, 0xF5, 0x06, 0x48,        // s
     0x06, 0xC4, 0x67, 0xF6, 0x78, 0xD3, 0xB4, 0x08,
     0x0D, 0x6F, 0x1C, 0xC5, 0x0A, 0xF2, 0x6C, 0xA2,
     0x09, 0x41, 0x73, 0x08, 0x28, 0x1B, 0x68, 0xAF,
     0x28, 0x26, 0x23, 0xEA, 0xA6, 0x3E, 0x5B, 0x5C,
     0x07, 0x23, 0xD8, 0xB8, 0xC3, 0x7F, 0xF0, 0x77,
     0x7B, 0x1A, 0x20, 0xF8, 0xCC, 0xB1, 0xDC, 0xCC,
     0x43, 0x99, 0x7F, 0x1E, 0xE0, 0xE4, 0x4D, 0xA4,
     0xA6, 0x7A}
};
static TestSignVector const testVectorP521_3 PROGMEM = {
    // P-521 test case with SHA-256 and message "test".
    "P-521 #3",
    "test",
    {0x00, 0x0E, 0x87, 0x1C, 0x4A, 0x14, 0xF9, 0x93,        // r
     0xC6, 0xC7, 0x36, 0x95, 0x01, 0x90, 0x0C, 0x4B,
     0xC1, 0xE9, 0xC7, 0xB0, 0xB4, 0xBA, 0x44, 0xE0,
     0x48, 0x68, 0xB3, 0x0B, 0x41, 0xD8, 0x07, 0x10,
     0x42, 0xEB, 0x28, 0xC4, 0xC2, 0x50, 0x41, 0x1D,
     0x0C, 0xE0, 0x8C, 0xD1, 0x97, 0xE4, 0x18, 0x8E,
     0xA4, 0x87, 0x6F, 0x27, 0x9F, 0x90, 0xB3, 0xD8,
     0xD7, 0x4A, 0x3C, 0x76, 0xE6, 0xF1, 0xE4, 0x65,
     0x6A, 0xA8,
     0x00, 0xCD, 0x52, 0xDB, 0xAA, 0x33, 0xB0, 0x63,        // s
     0xC3, 0xA6, 0xCD, 0x80, 0x58, 0xA1, 0xFB, 0x0A,
     0x46, 0xA4, 0x75, 0x4B, 0x03, 0x4F, 0xCC, 0x64,
     0x47, 0x66, 0xCA, 0x14, 0xDA, 0x8C, 0xA5, 0xCA,
     0x9F, 0xDE, 0x00, 0xE8, 0x8C, 0x1A, 0xD6, 0x0C,
     0xCB, 0xA7, 0x59, 0x02, 0x52, 0x99, 0x07, 0x9D,
     0x7A, 0x42, 0x7E, 0xC3, 0xCC, 0x5B, 0x61, 0x9B,
     0xFB, 0xC8, 0x28, 0xE7, 0x76, 0x9B, 0xCD, 0x69,
     0x4E, 0x86}
};
static TestSignVector const testVectorP521_4 PROGMEM = {
    // P-521 test case with SHA-512 and message "test".
    "P-521 #4",
    "test",
    {0x01, 0x3E, 0x99, 0x02, 0x0A, 0xBF, 0x5C, 0xEE,        // r
     0x75, 0x25, 0xD1, 0x6B, 0x69, 0xB2, 0x29, 0x65,
     0x2A, 0xB6, 0xBD, 0xF2, 0xAF, 0xFC, 0xAE, 0xF3,
     0x87, 0x73, 0xB4, 0xB7, 0xD0, 0x87, 0x25, 0xF1,
     0x0C, 0xDB, 0x93, 0x48, 0x2F, 0xDC, 0xC5, 0x4E,
     0xDC, 0xEE, 0x91, 0xEC, 0xA4, 0x16, 0x6B, 0x2A,
     0x7C, 0x62, 0x65, 0xEF, 0x0C, 0xE2, 0xBD, 0x70,
     0x51, 0xB7, 0xCE, 0xF9, 0x45, 0xBA, 0xBD, 0x47,
     0xEE, 0x6D,
     0x01, 0xFB, 0xD0, 0x01, 0x3C, 0x67, 0x4A, 0xA7,        // s
     0x9C, 0xB3, 0x98, 0x49, 0x52, 0x79, 0x16, 0xCE,
     0x30, 0x1C, 0x66, 0xEA, 0x7C, 0xE8, 0xB8, 0x06,
     0x82, 0x78, 0x6A, 0xD6, 0x0F, 0x98, 0xF7, 0xE7,
     0x8A, 0x19, 0xCA, 0x69, 0xEF, 0xF5, 0xC5, 0x74,
     0x00, 0xE3, 0xB3, 0xA0, 0xAD, 0x66, 0xCE, 0x09,
     0x78, 0x21, 0x4D, 0x13, 0xBA, 0xF4, 0xE9, 0xAC,
     0x60, 0x75, 0x2F, 0x7B, 0x15, 0x5E, 0x2D, 0xE4,
     0xDC, 0xE3}
};

void testSignCommon(const struct TestSignVector *_test, Hash *hash)
{
    uint8_t *privateKey = alice_f;
    uint8_t *publicKey = alice_k;
    uint8_t *sig = bob_k;
    static TestSignVector test;

    memcpy_P(&test, _test, sizeof(test));

    Serial.print(test.name);
    Serial.print(" Sign ... ");
    Serial.flush();

    memcpy_P(privateKey, testKeyP521.privateKey, 66);

    unsigned long start = micros();
    P521::sign(sig, privateKey, test.data, strlen(test.data), hash);
    unsigned long elapsed = micros() - start;
    Serial.print(elapsed);
    Serial.print(" us ... ");

    bool ok = !memcmp(sig, test.signature, 132);
    if (ok) {
        Serial.println("ok");
    } else {
        Serial.println("failed");
        printNumber("actual  ", sig, 132);
        printNumber("expected", test.signature, 132);
    }

    Serial.print(test.name);
    Serial.print(" Verify ... ");
    Serial.flush();

    memcpy_P(publicKey, testKeyP521.publicKey, 132);

    start = micros();
    bool verified = P521::verify
        (test.signature, publicKey, test.data, strlen(test.data), hash);
    elapsed = micros() - start;
    Serial.print(elapsed);
    Serial.print(" us ... ");

    if (verified)
        Serial.println("ok");
    else
        Serial.println("failed");

    Serial.print(test.name);
    Serial.print(" Derive Public Key ... ");
    Serial.flush();

    memcpy_P(privateKey, testKeyP521.privateKey, 66);

    start = micros();
    P521::derivePublicKey(publicKey, privateKey);
    elapsed = micros() - start;
    Serial.print(elapsed);
    Serial.print(" us ... ");

    ok = !P521_memcmp_P(publicKey, testKeyP521.publicKey, 132);
    if (ok) {
        Serial.println("ok");
    } else {
        Serial.println("failed");
        printNumber("actual  ", publicKey, 132);
        memcpy_P(publicKey, testKeyP521.publicKey, 132);
        printNumber("expected", publicKey, 132);
    }
}

void testSignSHA256(const struct TestSignVector *test)
{
    SHA256 hash;
    testSignCommon(test, &hash);
}

void testSignSHA512(const struct TestSignVector *test)
{
    SHA512 hash;
    testSignCommon(test, &hash);
}

void testSign()
{
    Serial.println("Digital signatures:");
    testSignSHA256(&testVectorP521_1);
    testSignSHA512(&testVectorP521_2);
    testSignSHA256(&testVectorP521_3);
    testSignSHA512(&testVectorP521_4);
}

void setup()
{
    Serial.begin(9600);

    // Start the random number generator.  We don't initialise a noise
    // source here because we don't need one for testing purposes.
    // Real DH applications should of course use a proper noise source.
    RNG.begin("TestP521 1.0");

    // Perform the tests.
    testEval();
    Serial.println();
    testDH();
    Serial.println();
    testSign();
    Serial.println();
}

void loop()
{
}
