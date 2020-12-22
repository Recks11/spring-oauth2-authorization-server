package dev.rexijie.auth.service;

/**
 * Class which represent entities able to generate secrets
 * @author Rex Ijiekhuamen
 */
public interface SecretGenerator {
    String generate();
    String generate(int length);
}
