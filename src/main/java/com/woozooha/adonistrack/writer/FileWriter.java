package com.woozooha.adonistrack.writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woozooha.adonistrack.domain.Invocation;
import com.woozooha.adonistrack.format.Format;
import com.woozooha.adonistrack.fuction.Predicate;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

public class FileWriter implements Writer, History {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Getter
    @Setter
    private File root;

    @Getter
    @Setter
    private Predicate<Invocation> filter = new Predicate<Invocation>() {
        public boolean test(Invocation invocation) {
            return true;
        }
    };

    public Format getFormat() {
        return null;
    }

    public void setFormat(Format format) {
        // do nothing.
    }

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

            java.io.Writer writer = null;
            try {
                writer = new java.io.FileWriter(jsonFile);
                writer.write(json);
                writer.flush();
            } catch (java.io.IOException e) {
                // ignore
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (java.io.IOException e) {
                        // close 도중 발생하는 예외는 보통 무시하거나 로그를 남깁니다.
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Invocation> getInvocationList() {
        String day = Invocation.DATE_FORMATTER.get().format(new Date());
        File dayDir = new File(root, day);
        if (!dayDir.exists()) {
            return Collections.emptyList();
        }

        File[] files = dayDir.listFiles();
        if (files == null) {
            return Collections.emptyList();
        }
        Arrays.sort(files, new Comparator<File>() {
            public int compare(java.io.File f1, java.io.File f2) {
                long m1 = f1.lastModified();
                long m2 = f2.lastModified();
                // 내림차순 정렬 (f2와 f1의 위치를 바꿈)
                if (m1 < m2) return 1;
                if (m1 > m2) return -1;
                return 0;
            }
        });

        List<Invocation> list = new ArrayList<Invocation>();
        for (File file : files) {
            try {
                Invocation invocation = readInvocation(file);
                if (invocation != null) {
                    list.add(invocation);
                }
            } catch (Exception e) {
                // ignore
            }
        }

        return list;
    }

    @SneakyThrows
    private Invocation readInvocation(File file) {
        Reader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(file), "UTF-8");

            return objectMapper.readValue(reader, Invocation.class);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (java.io.IOException e) {
                    // ignore
                }
            }
        }
    }

}
