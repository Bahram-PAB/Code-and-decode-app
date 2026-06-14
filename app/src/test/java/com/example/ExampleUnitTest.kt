package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Technical unit tests for cryptographic integrity verification.
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun `test encryption and decryption correctness`() {
    val secretKey = "superSecretPassphrase123!_فارسی"
    val originalText = "سلام دنیا! این یک آزمایش رمزنگاری امن است. Hello, World!"

    // 1. Encrypt text
    val encryptedText = CryptoUtils.encrypt(originalText, secretKey)
    assertNotNull(encryptedText)
    assertNotEquals(originalText, encryptedText)
    
    // Outputs should not be empty
    assertTrue(encryptedText.isNotEmpty())

    // 2. Decrypt text with the correct key
    val decryptedText = CryptoUtils.decrypt(encryptedText, secretKey)
    assertEquals(originalText, decryptedText)

    // 3. Modifying the key must fail decryption
    try {
        CryptoUtils.decrypt(encryptedText, "wrong_password_123")
        fail("Decryption should have thrown an exception on incorrect password")
    } catch (e: Exception) {
        // Expected behavior: Decryption fails on wrong key
        assertTrue(true)
    }
  }

  @Test
  fun `test random Initialization Vector uniqueness`() {
    val secretKey = "mySecretKey"
    val originalText = "Same plain message"

    // Consecutive encryptions of the same text with the same key
    // must yield DIFFERENT ciphertexts due to randomized IVs.
    val encryptedText1 = CryptoUtils.encrypt(originalText, secretKey)
    val encryptedText2 = CryptoUtils.encrypt(originalText, secretKey)

    assertNotEquals(encryptedText1, encryptedText2)

    // Both must decrypt back to the identical plaintext
    assertEquals(originalText, CryptoUtils.decrypt(encryptedText1, secretKey))
    assertEquals(originalText, CryptoUtils.decrypt(encryptedText2, secretKey))
  }
}
