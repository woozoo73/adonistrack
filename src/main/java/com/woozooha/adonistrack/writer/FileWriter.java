package com.woozooha.adonistrack.writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woozooha.adonistrack.domain.Invocation;
import com.woozooha.adonistrack.format.Format;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FileWriter implements Writer, History {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Getter
    @Setter
    private File root;

    private int maxSize = -1;

    private Predicate<Invocation> filter = (t) -> true;

    @Override
    public Format getFormat() {
        return null;
    }

    @Override
    public void setFormat(Format format) {
        // do nothing.
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public Predicate<Invocation> getFilter() {
        return filter;
    }

    public void setFilter(Predicate<Invocation> filter) {
        this.filter = filter;
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
                dayDir.mkdirs();
            }

            String json = objectMapper.writeValueAsString(invocation);
            File jsonFile = new File(dayDir, id + ".json");

            try (java.io.Writer writer = new java.io.FileWriter(jsonFile)) {
                writer.write(json);
                writer.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());

        return Arrays.stream(files).map(f -> {
                            try {
                                return readInvocation(f);
                            } catch (Exception e) {
                                return null;
                            }
                        }
                )
                .filter(e -> e != null)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private Invocation readInvocation(File file) {
        try (Reader reader = new InputStreamReader(new FileInputStream(file), "UTF-8")) {
            return objectMapper.readValue(reader, Invocation.class);
        }
    }

}
