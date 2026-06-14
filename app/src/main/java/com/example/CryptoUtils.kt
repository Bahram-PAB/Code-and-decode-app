package com.example

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Portable Base64 Encoder/Decoder in pure Kotlin.
 * Avoids stubbing/mocking issues with android.util.Base64 on local JVM test runners
 * and is extremely fast and light.
 */
object Base64Compat {
    private const val BASE64_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"

    fun encode(bytes: ByteArray): String {
        val result = StringBuilder()
        var i = 0
        while (i < bytes.size) {
            val b1 = bytes[i].toInt() and 0xFF
            if (i + 1 < bytes.size) {
                val b2 = bytes[i + 1].toInt() and 0xFF
                if (i + 2 < bytes.size) {
                    val b3 = bytes[i + 2].toInt() and 0xFF
                    val c1 = b1 ushr 2
                    val c2 = ((b1 and 0x03) shl 4) or (b2 ushr 4)
                    val c3 = ((b2 and 0x0F) shl 2) or (b3 ushr 6)
                    val c4 = b3 and 0x3F
                    result.append(BASE64_CHARS[c1])
                    result.append(BASE64_CHARS[c2])
                    result.append(BASE64_CHARS[c3])
                    result.append(BASE64_CHARS[c4])
                } else {
                    val c1 = b1 ushr 2
                    val c2 = ((b1 and 0x03) shl 4) or (b2 ushr 4)
                    val c3 = (b2 and 0x0F) shl 2
                    result.append(BASE64_CHARS[c1])
                    result.append(BASE64_CHARS[c2])
                    result.append(BASE64_CHARS[c3])
                    result.append('=')
                }
            } else {
                val c1 = b1 ushr 2
                val c2 = (b1 and 0x03) shl 4
                result.append(BASE64_CHARS[c1])
                result.append(BASE64_CHARS[c2])
                result.append("==")
            }
            i += 3
        }
        return result.toString()
    }

    fun decode(str: String): ByteArray {
        val cleanStr = str.replace("\\s".toRegex(), "")
        val equalsCount = cleanStr.count { it == '=' }
        val len = cleanStr.length
        if (len == 0) return ByteArray(0)

        val byteLen = (len * 3 / 4) - equalsCount
        val result = ByteArray(byteLen)

        var i = 0
        var j = 0
        while (i < len) {
            if (cleanStr[i] == '=') break
            val c1 = BASE64_CHARS.indexOf(cleanStr[i])
            val c2 = if (i + 1 < len && cleanStr[i + 1] != '=') BASE64_CHARS.indexOf(cleanStr[i + 1]) else 0
            val c3 = if (i + 2 < len && cleanStr[i + 2] != '=') BASE64_CHARS.indexOf(cleanStr[i + 2]) else 0
            val c4 = if (i + 3 < len && cleanStr[i + 3] != '=') BASE64_CHARS.indexOf(cleanStr[i + 3]) else 0

            val b1 = (c1 shl 2) or (c2 ushr 4)
            val b2 = ((c2 and 0x0F) shl 4) or (c3 ushr 2)
            val b3 = ((c3 and 0x03) shl 6) or c4

            if (j < byteLen) result[j++] = b1.toByte()
            if (j < byteLen) result[j++] = b2.toByte()
            if (j < byteLen) result[j++] = b3.toByte()

            i += 4
        }
        return result
    }
}

/**
 * Utility for secure AES-256-CBC text encryption and decryption.
 * Uses SHA-256 for key derivation from arbitrary string password keys.
 * Encrypts with a cryptographically secure random 16-byte Initialization Vector (IV).
 */
object CryptoUtils {
    private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    private const val KEY_ALGORITHM = "AES"
    private const val IV_SIZE = 16

    /**
     * Derives a 256-bit secret key from any user-provided key string using MessageDigest SHA-256.
     */
    private fun deriveKey(password: String): SecretKeySpec {
        val digest = MessageDigest.getInstance("SHA-256")
        val keyBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        return SecretKeySpec(keyBytes, KEY_ALGORITHM)
    }

    /**
     * Encrypts the plain text using 256-bit AES with CBC mode and PKCS5Padding.
     * Generates a cryptographically strong random IV, prepends it to the ciphertext,
     * and returns the final package encoded in Base64 (NO_WRAP).
     *
     * Output format: Base64(IV + CipherText)
     */
    @Throws(Exception::class)
    fun encrypt(plainText: String, secretKeyStr: String): String {
        if (plainText.isEmpty() || secretKeyStr.isEmpty()) return ""

        val keySpec = deriveKey(secretKeyStr)

        // Generate a new secure, random Initialization Vector
        val iv = ByteArray(IV_SIZE)
        SecureRandom().nextBytes(iv)
        val ivSpec = IvParameterSpec(iv)

        // Initialize Cipher for Encryption
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)

        val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

        // Prepend IV to the encrypted bytes
        val combined = ByteArray(iv.size + encryptedBytes.size)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(encryptedBytes, 0, combined, iv.size, encryptedBytes.size)

        // Return Base64 encoded result using portable compat utility
        return Base64Compat.encode(combined)
    }

    /**
     * Decrypts the Base64-encoded encrypted string using 256-bit AES CBC mode.
     * Extracts the IV from the first 16 bytes of the decoded byte array,
     * and decrypts the remainder using the AES key derived from the user's password.
     */
    @Throws(Exception::class)
    fun decrypt(cipherTextBase64: String, secretKeyStr: String): String {
        if (cipherTextBase64.isEmpty() || secretKeyStr.isEmpty()) return ""

        // Decode Base64 using portable compat utility
        val combined = Base64Compat.decode(cipherTextBase64)
        if (combined.size <= IV_SIZE) {
            throw IllegalArgumentException("متن وارد شده بسیار کوتاه است یا قالب نامعتبر دارد.")
        }

        val keySpec = deriveKey(secretKeyStr)

        // Extract the IV (first 16 bytes)
        val iv = ByteArray(IV_SIZE)
        System.arraycopy(combined, 0, iv, 0, iv.size)
        val ivSpec = IvParameterSpec(iv)

        // Extract the actual encrypted cipher message
        val cipherTextSize = combined.size - IV_SIZE
        val cipherTextBytes = ByteArray(cipherTextSize)
        System.arraycopy(combined, IV_SIZE, cipherTextBytes, 0, cipherTextSize)

        // Initialize Cipher for Decryption
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)

        val decryptedBytes = cipher.doFinal(cipherTextBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }
}
