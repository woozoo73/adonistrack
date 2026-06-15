package com.woozooha.adonistrack.writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woozooha.adonistrack.domain.Invocation;
import com.woozooha.adonistrack.format.Format;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FileWriter implements Writer, History {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Getter
    @Setter
    private File root;

    @Getter
    @Setter
    private Predicate<Invocation> filter = (t) -> true;

    @Override
    public Format getFormat() {
        return null;
    }

    @Override
    public void setFormat(Format format) {
        // do nothing.
    }

    @Override
    @SneakyThrows
    public void write(Invocation invocation) {
        try {
            if (!getFilter().test(invocation)) {
                return;
            }

            String id = invocation.getId();
            if (id == null || id.length() != Invocation.ID_LENGTH) {
                throw new RuntimeException("Invalid invocation's id=" + id);
            }

            String day = id.substring(0, Invocation.DATE_PATTERN.length());
            File dayDir = new File(root, day);
            if (!dayDir.exists()) {
                boolean result = dayDir.mkdirs();
                if (!result) {
                    throw new RuntimeException("Can't make the directory: " + dayDir);
                }
            }

            String json = objectMapper.writeValueAsString(invocation);
            File jsonFile = new File(dayDir, id + ".json");

            try (java.io.Writer writer = new java.io.FileWriter(jsonFile)) {
                writer.write(json);
                writer.flush();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Invocation> getInvocationList() {
        String day = Invocation.DATE_FORMATTER.format(LocalDate.now());
        File dayDir = new File(root, day);
        if (!dayDir.exists()) {
            return Collections.emptyList();
        }

        File[] files = dayDir.listFiles();
        if (files == null) {
            return Collections.emptyList();
        }
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());

        return Arrays.stream(files).map(f -> {
                            try {
                                return readInvocation(f);
                            } catch (Exception e) {
                                return null;
                            }
                        }
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private Invocation readInvocation(File file) {
        try (Reader reader = new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8)) {
            return objectMapper.readValue(reader, Invocation.class);
        }
    }

}
