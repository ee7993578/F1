package com.edufox.schoolerp.student;

import com.edufox.schoolerp.config.DatabaseWriteGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RegistrationRedirectionService {
    private static final String REGISTRATION_URL = "https://admissions.edufox.net/RegistrationForm.aspx?stuDetails=";
    private static final byte[] SALT = {1, 35, 69, 103, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF};
    private final RegistrationRedirectionRepository repository;
    private final DatabaseWriteGuard writeGuard;

    @Transactional
    public RegistrationRedirectionDtos.Response redirect(String schoolId, String staffUid,
                                                          RegistrationRedirectionDtos.Request request) {
        writeGuard.requireWritable();
        Map<String, Object> row = repository.findOrCreate(schoolId, staffUid, request.session(), request.phone());
        String session = String.valueOf(row.get("session"));
        String phone = String.valueOf(row.get("regPhone"));
        String formNo = String.valueOf(row.get("formNo"));
        String plaintext = "stuSession=" + session + "&stuRegPhone=" + phone + "&stuFormNo=" + formNo
            + "&userType=Faculty&schoolId=" + schoolId + "&userId=" + staffUid;
        String encrypted = URLEncoder.encode(encrypt(plaintext), StandardCharsets.UTF_8);
        return new RegistrationRedirectionDtos.Response(REGISTRATION_URL + encrypted, formNo,
            "Registration form opened");
    }

    private static String encrypt(String value) {
        try {
            PBEKeySpec spec = new PBEKeySpec("EduFoxSecret123".toCharArray(), SALT, 1000, 384);
            byte[] derived = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec).getEncoded();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(java.util.Arrays.copyOfRange(derived, 0, 32), "AES"),
                new IvParameterSpec(java.util.Arrays.copyOfRange(derived, 32, 48)));
            return Base64.getEncoder().encodeToString(cipher.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Registration redirect encryption failed", ex);
        }
    }
}
