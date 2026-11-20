package com.poolsafety.frontend.policy;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

/** Source-level guardrails taken from the project requirements. */
class SourcePolicyTest {

    private static final Path JAVA_MAIN = Path.of("src/main/java");
    private static final Path TS_MAIN = Path.of("frontend/src");
    private static final Path I18N = Path.of("src/main/resources/vaadin-i18n/translations.properties");

    private static List<Path> files(Path root, String extension) throws IOException {
        try (Stream<Path> stream = Files.walk(root)) {
            return stream.filter(p -> p.toString().endsWith(extension)).toList();
        }
    }

    private static List<Path> allSources() throws IOException {
        return Stream.concat(files(JAVA_MAIN, ".java").stream(), files(TS_MAIN, ".ts").stream()).toList();
    }

    @Test
    void noUnsafeHtmlOrBrowserStorageOrEval() throws IOException {
        List<String> banned = List.of("new Html(", "innerHTML", "setInnerHtml", "eval(", "localStorage",
                "sessionStorage");
        for (Path file : allSources()) {
            String content = Files.readString(file);
            for (String token : banned) {
                assertThat(content).as("%s must not contain %s", file, token).doesNotContain(token);
            }
        }
    }

    @Test
    void csrfProtectionIsNeverDisabled() throws IOException {
        for (Path file : files(JAVA_MAIN, ".java")) {
            assertThat(Files.readAllLines(file)).as(file.toString())
                    .noneMatch(l -> l.toLowerCase().contains("csrf") && l.toLowerCase().contains("disable"));
        }
    }

    @Test
    void typeScriptHasNoNetworkOrSecurityLogic() throws IOException {
        for (Path file : files(TS_MAIN, ".ts")) {
            String content = Files.readString(file);
            assertThat(content).doesNotContain("fetch(").doesNotContain("XMLHttpRequest")
                    .doesNotContainIgnoringCase("token").doesNotContainIgnoringCase("password");
        }
    }

    @Test
    void sourceCodeIsEnglishOnly() throws IOException {
        Pattern spanish = Pattern.compile("[áéíóúñÁÉÍÓÚÑ¿¡]");
        for (Path file : allSources()) {
            assertThat(spanish.matcher(Files.readString(file)).find())
                    .as("%s contains Spanish characters; user-facing text belongs in translations", file)
                    .isFalse();
        }
    }

    @Test
    void translationKeysAreEnglishAndEveryUsedKeyExists() throws IOException {
        Properties props = new Properties();
        try (var reader = Files.newBufferedReader(I18N)) {
            props.load(reader);
        }
        Pattern keyPattern = Pattern.compile("[a-z0-9]+(\\.[a-z0-9]+)*");
        props.stringPropertyNames().forEach(k -> assertThat(keyPattern.matcher(k).matches()).as(k).isTrue());

        Pattern used = Pattern.compile("(?:getTranslation\\(|new Entry\\()\"([a-z0-9.]+)\"");
        for (Path file : files(JAVA_MAIN, ".java")) {
            Matcher m = used.matcher(Files.readString(file));
            while (m.find()) {
                assertThat(props.stringPropertyNames()).as("%s uses key %s", file, m.group(1)).contains(m.group(1));
            }
        }
        // Keys that LoginView builds from a prefix: <prefix>.title and <prefix>.message
        for (String prefix : List.of("login.error.invalid", "login.error.unavailable",
                "login.error.ratelimited", "login.expired")) {
            assertThat(props.stringPropertyNames()).contains(prefix + ".title", prefix + ".message");
        }
    }
}
