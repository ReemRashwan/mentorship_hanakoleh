package com.mentorship.hanakoleh.architecture;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test that enforces database constraint and index naming conventions
 * across all Flyway SQL migration files in the project.
 * <p>
 * Ensures all named objects use the double-underscore ({@code __}) delimiter and
 * start with an approved prefix:
 * <ul>
 *   <li>{@code pk__} - Primary Keys</li>
 *   <li>{@code fk__} - Foreign Keys</li>
 *   <li>{@code idx__} - Non-unique Indexes</li>
 *   <li>{@code ux__} - Unique Indexes</li>
 *   <li>{@code uq__} - Unique Constraints</li>
 *   <li>{@code chk__} - Check Constraints</li>
 * </ul>
 */
class FlywayNamingConventionTest {

    /** Directory path where Flyway SQL migration scripts are located. */
    private static final String MIGRATIONS_DIR = "src/main/resources/db/migration";

    /**
     * Regex pattern enforcing Flyway file naming rules:
     * <ul>
     *   <li>Prefix: Uppercase {@code V}, {@code U}, or {@code R}</li>
     *   <li>Version: Digits separated by dots or underscores (e.g., {@code 1}, {@code 1.1}, {@code 20260819_1000})</li>
     *   <li>Separator: Double underscore {@code __}</li>
     *   <li>Description: Lowercase {@code snake_case}</li>
     *   <li>Extension: {@code .sql}</li>
     * </ul>
     */
    private static final Pattern FLYWAY_FILE_NAME_PATTERN =
            Pattern.compile("^((V|U)\\d+([._]\\d+)*|R)__[a-z0-9_]+\\.sql$");

    /** Regex pattern to extract constraint names following the {@code CONSTRAINT} keyword. */
    private static final Pattern CONSTRAINT_PATTERN =
            Pattern.compile("(?i)CONSTRAINT\\s+([a-zA-Z0-9_]+)", Pattern.CASE_INSENSITIVE);

    /** Regex pattern to extract index names following {@code CREATE [UNIQUE] INDEX}. */
    private static final Pattern INDEX_PATTERN =
            Pattern.compile("(?i)CREATE\\s+(UNIQUE\\s+)?INDEX\\s+([a-zA-Z0-9_]+)", Pattern.CASE_INSENSITIVE);

    /** Regex pattern validating constraint/index prefix, double-underscore, and lowercase body. */
    private static final Pattern VALID_CONSTRAINT_NAME_PATTERN =
            Pattern.compile("^(pk|fk|idx|ux|uq|chk)__[a-z0-9_]+$");

    /**
     * Scans the Flyway migrations directory and validates file names and SQL contents.
     *
     * @throws IOException if an I/O error occurs while reading the directory
     */
    @Test
    void testFlywayMigrationNamingConventions() throws IOException {
        Path migrationPath = Paths.get(MIGRATIONS_DIR);
        if (!Files.exists(migrationPath)) {
            return;
        }

        try (Stream<Path> paths = Files.walk(migrationPath)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".sql"))
                    .forEach(this::validateSqlFile);
        }
    }

    /**
     * Validates both the file name format and the SQL constraint/index contents.
     *
     * @param path the {@link Path} of the SQL migration file to validate
     */
    private void validateSqlFile(Path path) {
        checkFileName(path);

        try {
            String content = Files.readString(path);
            checkConstraints(path, content);
            checkIndexes(path, content);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read migration file: " + path, e);
        }
    }

    /**
     * Validates that the migration file name matches Flyway standards.
     *
     * @param path the path of the file being checked
     */
    private static void checkFileName(Path path) {
        String fileName = path.getFileName().toString();
        assertTrue(FLYWAY_FILE_NAME_PATTERN.matcher(fileName).matches(),
                String.format("Invalid Flyway file name '%s'. Must follow format: " +
                        "'V1__description.sql', 'U1__description.sql', or 'R__description.sql' " +
                        "using double-underscore '__' and lowercase snake_case description.", fileName));
    }

    /**
     * Extracts and validates all index names defined in the SQL content.
     *
     * @param path    the path of the SQL file being checked
     * @param content the raw SQL content
     */
    private static void checkIndexes(Path path, String content) {
        Matcher indexMatcher = INDEX_PATTERN.matcher(content);
        while (indexMatcher.find()) {
            String name = indexMatcher.group(2);
            assertTrue(VALID_CONSTRAINT_NAME_PATTERN.matcher(name).matches(),
                    String.format("Invalid index name '%s' in %s. Must start with valid prefix (idx__, ux__) and use lowercase.",
                            name, path.getFileName()));
        }
    }

    /**
     * Extracts and validates all constraint names defined in the SQL content.
     *
     * @param path    the path of the SQL file being checked
     * @param content the raw SQL content
     */
    private static void checkConstraints(Path path, String content) {
        Matcher constraintMatcher = CONSTRAINT_PATTERN.matcher(content);
        while (constraintMatcher.find()) {
            String name = constraintMatcher.group(1);
            assertTrue(VALID_CONSTRAINT_NAME_PATTERN.matcher(name).matches(),
                    String.format("Invalid constraint name '%s' in %s. Must start with valid prefix (pk__, fk__, uq__, chk__) and use lowercase.",
                            name, path.getFileName()));
        }
    }
}