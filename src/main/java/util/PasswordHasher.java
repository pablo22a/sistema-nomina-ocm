package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Clase utilitaria para el hash y verificación de contraseñas
 * Utiliza SHA-256 con salt para mayor seguridad
 * 
 * @author Pablo
 */
public class PasswordHasher {
    
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16; // 16 bytes = 128 bits
    
    /**
     * Genera un hash seguro de la contraseña con salt
     * @param password Contraseña en texto plano
     * @return Hash de la contraseña en formato "salt:hash" (Base64)
     */
    public static String hashPassword(String password) {
        try {
            // 1. Generar salt aleatorio
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // 2. Crear hash con salt + password
            String hashedPassword = hashPasswordWithSalt(password, salt);
            
            // 3. Retornar en formato "salt:hash" (ambos en Base64)
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            return saltBase64 + ":" + hashedPassword;
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar hash de contraseña: " + e.getMessage());
        }
    }
    
    /**
     * Verifica si la contraseña proporcionada coincide con el hash almacenado
     * @param password Contraseña en texto plano
     * @param storedHash Hash almacenado en formato "salt:hash"
     * @return true si la contraseña es correcta, false en caso contrario
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            // 1. Separar salt y hash
            String[] parts = storedHash.split(":");
            if (parts.length != 2) {
                return false; // Formato inválido
            }
            
            // 2. Decodificar salt desde Base64
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            String expectedHash = parts[1];
            
            // 3. Generar hash con la contraseña proporcionada y el salt original
            String actualHash = hashPasswordWithSalt(password, salt);
            
            // 4. Comparar hashes
            return actualHash.equals(expectedHash);
            
        } catch (Exception e) {
            System.err.println("Error al verificar contraseña: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Genera hash SHA-256 de la contraseña con un salt específico
     * @param password Contraseña en texto plano
     * @param salt Salt como array de bytes
     * @return Hash en Base64
     * @throws NoSuchAlgorithmException Si SHA-256 no está disponible
     */
    private static String hashPasswordWithSalt(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
        
        // Agregar salt al hash
        md.update(salt);
        
        // Hash de la contraseña
        byte[] hashedPassword = md.digest(password.getBytes());
        
        // Convertir a Base64
        return Base64.getEncoder().encodeToString(hashedPassword);
    }
    
    /**
     * Método de prueba para verificar el funcionamiento
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        // Ejemplo de uso
        String password = "miContraseñaSecreta";
        
        // Hash
        String hashedPassword = hashPassword(password);
        System.out.println("Contraseña original: " + password);
        System.out.println("Hash generado: " + hashedPassword);
        
        // Verificación
        boolean isValid = verifyPassword(password, hashedPassword);
        System.out.println("Verificación correcta: " + isValid);
        
        // Verificación con contraseña incorrecta
        boolean isInvalid = verifyPassword("contraseñaIncorrecta", hashedPassword);
        System.out.println("Verificación incorrecta: " + isInvalid);
    }
}