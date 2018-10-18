package io.github.actar676309180.blog;

import org.springframework.stereotype.Service;

import java.io.File;
import java.text.MessageFormat;
import java.util.*;

@Service
public class MarkdownService {
    public List<Markdown> markdowns;

    public Map<String, List<Markdown>> tags;

    public Map<String, Markdown> pathMapping;

    public void load() {
        List<Markdown> markdowns = loadMarkdowns();
        Map<String, List<Markdown>> tags = generateTags(markdowns);
        Map<String, Markdown> pathMapping = generatePathMapping(markdowns);
        this.markdowns = markdowns;
        this.tags = tags;
        this.pathMapping = pathMapping;
    }

    public boolean reload() {
        try {
            List<Markdown> markdowns = loadMarkdowns();
            Map<String, List<Markdown>> tags = generateTags(markdowns);
            Map<String, Markdown> pathMapping = generatePathMapping(markdowns);
            this.markdowns = markdowns;
            this.tags = tags;
            this.pathMapping = pathMapping;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Markdown> loadMarkdowns() {
        List<Markdown> markdowns = new ArrayList<>();
        File markdown = new File("markdown");
        requiredDirectory(markdown);
        List<File> mds = new ArrayList<File>();
        findMarkdownFile(markdown, mds);
        for (File md : mds) {
            markdowns.add(Markdown.parse(md));
        }
        Collections.sort(markdowns);
        return markdowns;
    }

    public Map<String, List<Markdown>> generateTags(List<Markdown> markdowns) {
        Map<String, List<Markdown>> tags = new HashMap<>();
        for (Markdown markdown : markdowns) {
            for (String tag : markdown.tags) {
                handTags(tags, tag, markdown);
            }
        }
        return tags;
    }

    public void handTags(Map<String, List<Markdown>> tags, String tag, Markdown markdown) {
        if (tags.containsKey(tag)) {
            List<Markdown> list = tags.get(tag);
            list.add(markdown);
        } else {
            List<Markdown> list = new ArrayList<>();
            list.add(markdown);
            tags.put(tag, list);
        }
    }

    public Map<String, Markdown> generatePathMapping(List<Markdown> markdowns) {
        Map<String, Markdown> pathMapping = new HashMap<>();
        for (Markdown markdown : markdowns) {
            String mapping = generatePath(markdown);
            markdown.mapping = mapping;

            markdown.time = MessageFormat.format("{0,date,yyyy-MM-dd}", markdown.date);

            pathMapping.put(mapping, markdown);
        }
        return pathMapping;
    }

    private String generatePath(Markdown markdown) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(markdown.date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String result = MessageFormat.format("/articles/{0}/{1}/{2}/{3}.html", String.valueOf(year), String.valueOf(month), String.valueOf(day), markdown.title);
        return result.replace(" ", "_");
    }

    private void requiredDirectory(File directory) {
        if (!directory.isDirectory()) {
            System.out.println(MessageFormat.format("{0}目录不存在", directory.getPath()));
            System.exit(0);
        }
    }

    private void findMarkdownFile(File path, List<File> list) {
        File[] files = path.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isDirectory()) {
                findMarkdownFile(file, list);
            }
            if (file.isFile()) {
                if (file.getName().toUpperCase().endsWith(".MD")) {
                    list.add(file);
                }
            }
        }
    }
}
